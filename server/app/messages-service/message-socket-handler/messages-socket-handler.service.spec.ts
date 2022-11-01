/* eslint-disable no-underscore-dangle */
/* eslint-disable max-lines */
/* eslint-disable @typescript-eslint/no-magic-numbers */
/* eslint-disable no-unused-expressions */
/* eslint-disable @typescript-eslint/no-unused-expressions */
import { AuthService } from '@app/auth/services/auth.service';
import { SessionMiddlewareService } from '@app/auth/services/session-middleware.service';
import { MAX_MESSAGE_LENGTH, SYSTEM_USER_NAME } from '@app/constants';
import { MessagesSocketHandler, SYSTEM_MESSAGES } from '@app/messages-service/message-socket-handler/messages-socket-handler.service';
import { Message } from '@app/messages-service/message.interface';
import { Room } from '@app/messages-service/room/room';
import { RoomFactory } from '@app/messages-service/room/room-factory.service';
import { GlobalSystemMessage, IndividualSystemMessage, SystemMessageDTO } from '@app/messages-service/system-message.interface';
import { SystemMessagesService } from '@app/messages-service/system-messages-service/system-messages.service';
import { createSinonStubInstance, StubbedClass } from '@app/test.util';
import { User } from '@app/user/interfaces/user.interface';
import { UserService } from '@app/user/user.service';
import { expect } from 'chai';
import { createServer, Server } from 'http';
import { AddressInfo } from 'net';
import { Subject } from 'rxjs';
import * as sinon from 'sinon';
import { Socket } from 'socket.io';
import { io as Client, Socket as ClientSocket } from 'socket.io-client';

describe('MessagesService', () => {
    let handler: MessagesSocketHandler;
    let clientSocket: ClientSocket;
    let serverSocket: Socket;
    let port: number;
    let httpServer: Server;
    let room: StubbedClass<Room>;
    const mockUser = {
        _id: 'abcdefghijklmnop',
        name: 'abc',
    } as unknown as User;

    const playerName = 'abc';

    const mockGlobalSystemMessages$ = new Subject<GlobalSystemMessage>();
    const mockIndividualSystemMessages$ = new Subject<IndividualSystemMessage>();

    before((done) => {
        httpServer = createServer();
        httpServer.listen(() => {
            port = (httpServer.address() as AddressInfo).port;
            const systemMessagesService = createSinonStubInstance<SystemMessagesService>(SystemMessagesService);
            sinon.stub(systemMessagesService, 'globalSystemMessages$').get(() => mockGlobalSystemMessages$);
            sinon.stub(systemMessagesService, 'individualSystemMessages$').get(() => mockIndividualSystemMessages$);
            const sessionMiddleware = createSinonStubInstance(SessionMiddlewareService);
            const authService = createSinonStubInstance(AuthService);
            const userService = createSinonStubInstance(UserService);
            userService.getUser.resolves(mockUser);
            userService.getSystemUser.resolves({ name: SYSTEM_USER_NAME, _id: 'system-very-long-id' } as unknown as User);
            const roomFactory = createSinonStubInstance(RoomFactory);

            room = createSinonStubInstance(Room);
            room.userIds = new Set();
            room.userIdToSocketId = new Map();
            roomFactory.createRoom.resolves(room);
            handler = new MessagesSocketHandler(httpServer, systemMessagesService, sessionMiddleware, authService, userService, roomFactory);
            handler.handleSockets(false, false);
            handler.sio.on('connection', (socket) => {
                serverSocket = socket;
            });
            done();
        });
    });

    beforeEach((done) => {
        clientSocket = Client(`http://localhost:${port}`, { path: '/messages' });
        clientSocket.on('connect', done);
    });

    afterEach(() => {
        clientSocket.close();
    });

    after(() => {
        httpServer.close();
    });

    it('should create new user', (done) => {
        const userName = 'test';
        clientSocket.emit('userName', userName);
        serverSocket.once('userName', () => {
            const user = [...handler.users.values()][0];
            expect(user.id).to.equal(userName);
            done();
        });
    });

    it('should join room', (done) => {
        const userName = 'test';
        clientSocket.emit('userName', userName);
        const conversation = 'abc';
        clientSocket.emit('joinRoom', conversation);
        serverSocket.once('joinRoom', () => {
            setTimeout(() => {
                expect(handler.activeRooms.has(conversation)).to.be.true;
                done();
            }, 10);
        });
    });

    it('should send message to other user in room', (done) => {
        const clientSocket2 = Client(`http://localhost:${port}`, { path: '/messages', multiplex: false });

        const userName1 = 'test';
        clientSocket.emit('userName', userName1);
        const userName2 = 'test2';
        clientSocket2.emit('userName', userName2);

        const conversation = 'abc';
        clientSocket.emit('joinRoom', conversation);
        clientSocket2.emit('joinRoom', conversation);

        const messageToSend = {
            content: 'Hi',
            conversation,
        };
        const expectedMessage = {
            from: userName2,
            date: new Date().toISOString(),
            content: 'Hi',
            conversation,
        };

        setTimeout(() => {
            clientSocket.on('roomMessages', (message: Message) => {
                expect(message.content).equal(expectedMessage.content);
                expect(message.from).equal(expectedMessage.from);
                done();
            });
            clientSocket2.emit('newMessage', messageToSend);
        }, 175);
    });

    it('should not send message to user in other room', (done) => {
        const clientSocket2 = Client(`http://localhost:${port}`, { path: '/messages', multiplex: false });

        const userName1 = 'test';
        clientSocket.emit('userName', userName1);
        const userName2 = 'test2';
        clientSocket2.emit('userName', userName2);

        const conversation1 = 'abc';
        clientSocket.emit('joinRoom', conversation1);
        const conversation2 = 'def';
        clientSocket2.emit('joinRoom', conversation2);

        let receivedMessage = false;
        clientSocket.on('roomMessages', () => {
            receivedMessage = true;
        });

        const messageToSend = {
            content: 'Hi',
            conversation: conversation2,
        };
        clientSocket2.emit('newMessage', messageToSend);

        setTimeout(() => {
            expect(receivedMessage).to.be.false;
            clientSocket2.close();
            done();
        }, 30);
    });

    it('client should receive error when sending message with no userName', (done) => {
        const messageToSend = {
            content: 'Hi',
            conversation: 'abc',
        };
        clientSocket.once('error', (errorMessage: string) => {
            expect(errorMessage).to.equal("Vous n'avez pas encore entré votre nom dans notre systême");
            done();
        });
        clientSocket.emit('newMessage', messageToSend);
    });

    it('client should receive error when sending message with message over max length allowed', (done) => {
        const name = 'hello';
        clientSocket.emit('userName', name);
        const bigContent = 'a'.repeat(MAX_MESSAGE_LENGTH + 1);
        const message = {
            content: bigContent,
            conversation: 'abc',
        };
        clientSocket.once('error', (errorMessage: string) => {
            expect(errorMessage).to.equal('Le message doit être plus petit que 512 charactères');
            done();
        });
        clientSocket.emit('newMessage', message);
    });

    it('client should receive error when message sent without joining a room', (done) => {
        const name = 'hello';
        clientSocket.emit('userName', name);
        clientSocket.once('error', (errorMessage: string) => {
            expect(errorMessage).to.equal("Vous n'avez pas rejoint la room: abc");
            done();
        });
        const content = {
            content: 'hi',
            conversation: 'abc',
        };
        clientSocket.emit('newMessage', content);
    });

    it('client should receive an error when sending message in a not active room', (done) => {
        const name = 'hello';
        clientSocket.emit('userName', name);
        clientSocket.once('error', (errorMessage: string) => {
            expect(errorMessage).to.equal("La salle de chat n'est plus active");
            done();
        });

        const conversation = 'abc';
        clientSocket.emit('joinRoom', conversation);
        serverSocket.on('joinRoom', () => {
            setTimeout(() => handler.activeRooms.delete(conversation), 10);
        });

        const message = {
            content: 'hi',
            conversation,
        };
        setTimeout(() => clientSocket.emit('newMessage', message), 150);
    });

    it('client should receive an error when joining a room without user name', (done) => {
        clientSocket.once('error', (errorMessage: string) => {
            expect(errorMessage).to.equal("Vous n'avez pas encore choisi un nom");
            done();
        });
        const conversation = 'abc';
        clientSocket.emit('joinRoom', conversation);
    });

    it('should delete user on deconnection', (done) => {
        const name = 'allo';
        clientSocket.emit('userName', name);

        serverSocket.on('disconnect', () => {
            const socketID = serverSocket.id;
            expect(handler.users.has(socketID)).to.be.false;
            done();
        });
        clientSocket.close();
    });

    it('should delete room on when no user in it', (done) => {
        const name = 'allo';
        clientSocket.emit('userName', name);

        const conversation = 'abc';
        clientSocket.emit('joinRoom', conversation);

        serverSocket.on('disconnect', () => {
            expect(handler.activeRooms.has(conversation)).to.be.false;
            done();
        });

        clientSocket.close();
    });

    it('should create room when a new user joins it', (done) => {
        const name = 'allo';
        clientSocket.emit('userName', name);

        const conversation = 'abc';
        serverSocket.on('joinRoom', () => {
            setTimeout(() => {
                expect(handler.activeRooms.has(conversation)).to.be.true;
                done();
            }, 10);
        });

        clientSocket.emit('joinRoom', conversation);
    });

    it('should receive global system message', (done) => {
        const name = 'abc';
        clientSocket.emit('userName', name);
        const conversation = 'abc';
        clientSocket.emit('joinRoom', conversation);
        serverSocket.on('joinRoom', () => {
            setTimeout(() => {
                mockGlobalSystemMessages$.next(sysMessage);
            }, 15);
        });
        const sysMessage: GlobalSystemMessage = {
            content: 'allo',
            gameToken: conversation,
        };
        clientSocket.on(SYSTEM_MESSAGES, (message: SystemMessageDTO) => {
            expect(message.content).to.deep.equal(sysMessage.content);
            expect(message.conversation).to.deep.equal(sysMessage.gameToken);
            done();
        });
    });

    it('should send individual system message', (done) => {
        clientSocket.emit('userName', playerName);
        const gameToken = 'def';
        clientSocket.emit('joinRoom', gameToken);
        room.userIdToSocketId.set(mockUser._id, clientSocket.id);
        serverSocket.on('joinRoom', () => {
            setTimeout(() => mockIndividualSystemMessages$.next(sysMessage), 50);
        });
        const sysMessage: IndividualSystemMessage = {
            content: 'allo',
            gameToken,
            playerName,
        };
        clientSocket.on(SYSTEM_MESSAGES, (message: SystemMessageDTO) => {
            expect(message.content).to.deep.equal(sysMessage.content);
            expect(message.conversation).to.deep.equal(sysMessage.gameToken);
            done();
        });
    });

    it('should throws when sending individual system message to unconnected client', (done) => {
        clientSocket.emit('userName', playerName);
        const sysMessage: IndividualSystemMessage = {
            content: 'allo',
            gameToken: '3',
            playerName,
        };
        mockIndividualSystemMessages$.next(sysMessage);
        clientSocket.on(SYSTEM_MESSAGES, () => {
            expect.fail();
        });

        setTimeout(() => {
            expect(true).be.true;
            done();
        }, 20);
    });
});
