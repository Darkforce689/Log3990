import { Injectable } from '@angular/core';
import { GAME_CONVO_NAME } from '@app/chat/constants';
import { Conversation } from '@app/chat/interfaces/conversation.interface';
import { BaseMessage, ChatMessage, SystemMessage } from '@app/chat/interfaces/message.interface';
import { isSocketConnected } from '@app/game-logic/utils';
import { AccountService } from '@app/services/account.service';
import { AuthService } from '@app/services/auth.service';
import { Observable, Subject } from 'rxjs';
import { io, Socket } from 'socket.io-client';
import { environment } from 'src/environments/environment';

@Injectable({
    providedIn: 'root',
})
export class OnlineChatHandlerService {
    socket: Socket;
    private newRoomMessageSubject = new Subject<ChatMessage>();
    private errorSubject = new Subject<string>();
    private sysMessageSubject = new Subject<SystemMessage>();
    private joinedRooms = new Set<string>();

    // TODO refactor remove accountService
    constructor(private accountService: AccountService, private authService: AuthService) {
        this.authService.isAuthenticated$.subscribe((isAuth) => {
            if (!isAuth) {
                this.disconnect();
            }
        });
    }

    joinChatRoomWithUser(roomID: string) {
        const account = this.accountService.account;
        if (account === undefined) {
            throw Error("Can't join chat room because client not connected");
        }

        this.joinChatRoom(roomID);
    }

    disconnect() {
        if (!this.socket) {
            return;
        }
        this.socket.close();
        this.joinedRooms.clear();
        this.socket = undefined as unknown as Socket;
    }

    leaveChatRoom(roomId: string) {
        if (!this.socket) {
            throw Error('No socket to leave the room with');
        }
        const isRoomJoined = this.joinedRooms.delete(roomId) !== undefined;
        if (!isRoomJoined) {
            throw Error('You have not joined the room you trying to leave');
        }

        this.socket.emit('leaveRoom', roomId);
    }

    sendMessage(message: BaseMessage) {
        if (!this.socket) {
            throw Error('No socket to send message from');
        }
        this.socket.emit('newMessage', message);
    }

    joinChatRooms(conversations: Conversation[]) {
        if (!this.socket) {
            this.socket = this.connectToSocket();
            this.bindRoomChannels();
        }

        const getRoomId = (conversation: Conversation) => {
            // eslint-disable-next-line no-underscore-dangle
            return conversation.name === GAME_CONVO_NAME ? conversation._id : conversation.name;
        };

        const roomIds = conversations.map((conversation) => getRoomId(conversation));

        roomIds.forEach((roomId) => {
            if (this.joinedRooms.has(roomId)) {
                return;
            }
            this.socket.emit('joinRoom', roomId);
        });

        const roomIdsSet = new Set(roomIds);
        this.joinedRooms.forEach((roomId) => {
            if (roomIdsSet.has(roomId)) {
                return;
            }
            this.socket.emit('leaveRoom', roomId);
        });

        this.joinedRooms = roomIdsSet;
    }

    joinChatRoom(roomId: string) {
        if (!this.socket) {
            this.socket = this.connectToSocket();
            this.bindRoomChannels();
        }

        if (this.joinedRooms.has(roomId)) {
            return;
        }
        this.joinedRooms.add(roomId);
        this.socket.emit('joinRoom', roomId);
    }

    private bindRoomChannels() {
        this.socket.on('error', (errorContent: string) => {
            this.receiveChatServerError(errorContent);
        });

        this.socket.on('roomMessages', (message: ChatMessage) => {
            this.receiveServerMessage(message);
        });

        this.socket.on('systemMessages', (message: SystemMessage) => {
            this.receiveSystemMessage(message);
        });
    }

    private connectToSocket() {
        // transports used to prevent cors error
        return io(environment.serverSocketUrl, { path: '/messages', withCredentials: true, transports: ['websocket'] });
    }

    private receiveChatServerError(content: string) {
        this.errorSubject.next(content);
    }

    private receiveServerMessage(message: ChatMessage) {
        this.newRoomMessageSubject.next(message);
    }

    private receiveSystemMessage(message: SystemMessage) {
        this.sysMessageSubject.next(message);
    }

    get isConnected(): boolean {
        return isSocketConnected(this.socket);
    }

    get newMessages$(): Observable<ChatMessage> {
        return this.newRoomMessageSubject;
    }

    get newRoomMessages$(): Observable<ChatMessage> {
        return this.newRoomMessageSubject;
    }

    get errorMessage$(): Observable<string> {
        return this.errorSubject;
    }

    get systemMessage$(): Observable<SystemMessage> {
        return this.sysMessageSubject;
    }
}
