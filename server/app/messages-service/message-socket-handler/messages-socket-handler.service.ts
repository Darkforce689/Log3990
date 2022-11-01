import { AuthService } from '@app/auth/services/auth.service';
import { SessionMiddlewareService } from '@app/auth/services/session-middleware.service';
import { Session } from '@app/auth/services/session.interface';
import { ENABLE_SOCKET_LOGIN, MAX_MESSAGE_LENGTH } from '@app/constants';
import { ServerLogger } from '@app/logger/logger';
import { ChatUser } from '@app/messages-service/chat-user.interface';
import { BaseMessage, Message } from '@app/messages-service/message.interface';
import { Room } from '@app/messages-service/room/room';
import { RoomFactory } from '@app/messages-service/room/room-factory.service';
import { GlobalSystemMessage, IndividualSystemMessage, SystemMessageDTO } from '@app/messages-service/system-message.interface';
import { SystemMessagesService } from '@app/messages-service/system-messages-service/system-messages.service';
import { UserService } from '@app/user/user.service';
import * as http from 'http';
import * as io from 'socket.io';
import { DefaultEventsMap } from 'socket.io/dist/typed-events';

export const SYSTEM_MESSAGES = 'systemMessages';
export const NEW_MESSAGE = 'newMessage';
export const ROOM_MESSAGES = 'roomMessages';
export const NEW_USER_NAME = 'userName';
export const JOIN_ROOM = 'joinRoom';
export const LEAVE_ROOM = 'leaveRoom';

export class MessagesSocketHandler {
    activeRooms = new Map<string, Room>();
    users = new Map<string, ChatUser>();
    readonly sio: io.Server;

    constructor(
        server: http.Server,
        private systemMessagesService: SystemMessagesService,
        private sessionMiddleware: SessionMiddlewareService,
        private authService: AuthService,
        private userService: UserService,
        private roomFactory: RoomFactory,
    ) {
        this.sio = new io.Server(server, {
            path: '/messages',
            cors: { origin: '*', methods: ['GET', 'POST'] },
        });

        this.systemMessagesService.globalSystemMessages$.subscribe((message) => {
            this.sendGlobalSystemMessage(message);
        });

        this.systemMessagesService.individualSystemMessages$.subscribe((message) => {
            this.sendIndividualSystemMessage(message);
        });
    }

    handleSockets(enableAuth: boolean = ENABLE_SOCKET_LOGIN, enableRedisSession: boolean = true) {
        if (enableAuth) {
            const sessionMiddleware = this.sessionMiddleware.getSocketSessionMiddleware(enableRedisSession);
            this.sio.use(sessionMiddleware);
            this.sio.use(this.authService.socketAuthGuard);
        }

        this.sio.on('connection', async (socket) => {
            if (!enableAuth) {
                socket.on(NEW_USER_NAME, (userName: string) => {
                    try {
                        this.createUser(userName, socket.id);
                    } catch (error) {
                        this.sendError(socket, error);
                    }
                });
            }

            socket.on(NEW_MESSAGE, (baseMessage: BaseMessage) => {
                if (!baseMessage.content === undefined || !baseMessage.conversation) {
                    this.sendError(socket, Error('Message Invalide'));
                    return;
                }
                try {
                    this.sendMessage(socket.id, baseMessage);
                } catch (error) {
                    this.sendError(socket, error);
                }
            });

            socket.on(JOIN_ROOM, async (roomID: string) => {
                try {
                    if (enableAuth) {
                        const { userId: _id } = (socket.request as unknown as { session: Session }).session;
                        const user = await this.userService.getUser({ _id });
                        if (user === undefined) {
                            throw Error(`No user found with userId ${_id}`);
                        }
                        ServerLogger.logDebug(`User joined ${roomID}: ${user}`);
                        // eslint-disable-next-line no-underscore-dangle
                        this.createUser(_id, socket.id);
                    }

                    await this.addUserToRoom(socket, roomID);
                } catch (error) {
                    this.sendError(socket, error);
                }
            });

            socket.on(LEAVE_ROOM, (roomId: string) => {
                const user = this.users.get(socket.id);
                if (!user) {
                    return;
                }
                user.rooms.delete(roomId);
                try {
                    const room = this.activeRooms.get(roomId);
                    if (!room) {
                        throw Error('The room you are trying to leave is not active');
                    }
                    room.deleteUser(user.id);
                    if (room.userIds.size === 0) {
                        this.deleteRoom(roomId);
                    }
                } catch (error) {
                    this.sendError(socket, error);
                }
            });

            socket.on('disconnect', () => {
                this.deleteUser(socket.id);
            });
        });
    }

    private sendMessage(socketID: string, baseMessage: BaseMessage): void {
        const user = this.users.get(socketID);
        const { content, conversation } = baseMessage;
        if (!user) {
            throw Error("Vous n'avez pas encore entré votre nom dans notre systême");
        }

        if (content.length > MAX_MESSAGE_LENGTH) {
            throw Error('Le message doit être plus petit que 512 charactères');
        }

        if (!user.rooms.has(conversation)) {
            throw Error(`Vous n'avez pas rejoint la room: ${conversation}`);
        }

        const userId = user.id;

        const rooms = user.rooms;
        if (rooms.size === 0) {
            throw Error("Vous n'avez pas rejoint de salle de chat");
        }

        const room = this.activeRooms.get(conversation);
        if (!room) {
            // TODO handle when conversation is deleted
            throw Error("La salle de chat n'est plus active");
        }
        const message: Message = {
            from: userId,
            date: new Date(),
            content,
            conversation,
        };

        this.sendMessageToRoomSockets(conversation, message);
        room.addMessage(message);
    }

    private createUser(userId: string, socketID: string) {
        if (this.users.has(socketID)) {
            return;
        }
        const newUser: ChatUser = {
            id: userId,
            rooms: new Set(),
        };
        this.users.set(socketID, newUser);
    }

    private async addUserToRoom(socket: io.Socket<DefaultEventsMap, DefaultEventsMap, DefaultEventsMap>, roomID: string) {
        const socketID = socket.id;
        const user = this.users.get(socketID);
        if (!user) {
            throw Error("Vous n'avez pas encore choisi un nom");
        }

        if (user.rooms.has(roomID)) {
            throw Error(`Vous êtes déjà dans la salle ${roomID}`);
        }

        let activeRoom = this.activeRooms.get(roomID);
        if (!activeRoom) {
            activeRoom = await this.createRoom(roomID);
        }
        await activeRoom.addUser(user.id, socketID);
        user.rooms.add(roomID);
        socket.join(roomID);
    }

    private async createRoom(roomID: string): Promise<Room> {
        const newRoom = await this.roomFactory.createRoom(roomID);
        this.activeRooms.set(roomID, newRoom);
        return newRoom;
    }

    private deleteUser(socketID: string): void {
        const user = this.users.get(socketID);
        if (!user) {
            return;
        }
        this.users.delete(socketID);
        const rooms = user.rooms;
        if (!rooms) {
            return;
        }
        rooms.forEach((roomID) => {
            const room = this.activeRooms.get(roomID);
            if (!room) {
                return;
            }
            room.deleteUser(user.id);
            if (room.userIds.size === 0) {
                this.deleteRoom(roomID);
            }
        });
    }

    private deleteRoom(roomID: string) {
        this.activeRooms.delete(roomID);
    }

    private sendError(socket: io.Socket, error: Error) {
        ServerLogger.logError(error);
        const errorMessage = error.message;
        socket.emit('error', errorMessage);
    }

    private sendMessageToRoomSockets(roomID: string, message: Message) {
        this.sio.to(roomID).emit(ROOM_MESSAGES, message);
    }

    private async sendGlobalSystemMessage(globalMessage: GlobalSystemMessage) {
        const roomId = globalMessage.gameToken;
        const content = globalMessage.content;
        const { _id: from } = await this.userService.getSystemUser();

        const room = this.activeRooms.get(roomId);
        if (!room) {
            return;
        }

        const sysMessage: SystemMessageDTO = {
            content,
            conversation: roomId,
            date: new Date(),
        };

        const message = {
            from,
            ...sysMessage,
        };
        room.addSystemMessage(message);

        this.sio.to(roomId).emit(SYSTEM_MESSAGES, sysMessage);
    }

    private async sendIndividualSystemMessage(individualMessage: IndividualSystemMessage) {
        const userName = individualMessage.playerName;
        const conversation = individualMessage.gameToken;
        const socketID = await this.getSocketId(userName, conversation);
        if (!socketID) {
            return;
        }
        const content = individualMessage.content;
        const message: SystemMessageDTO = {
            content,
            conversation,
            date: new Date(),
        };
        this.sio.to(socketID).emit(SYSTEM_MESSAGES, message);
    }

    private async getSocketId(name: string, roomId: string) {
        const room = this.activeRooms.get(roomId);
        if (!room) {
            return;
        }
        const user = await this.userService.getUser({ name });
        if (!user) {
            return;
        }
        const { _id: userId } = user;
        const socketID = room.userIdToSocketId.get(userId);
        return socketID;
    }
}
