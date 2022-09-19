import { Injectable } from '@angular/core';
import { ChatMessage } from '@app/game-logic/messages/message.interface';
import { isSocketConnected } from '@app/game-logic/utils';
import { AccountService } from '@app/services/account.service';
import { AuthService } from '@app/services/auth.service';
import { Observable, Subject } from 'rxjs';
import { filter } from 'rxjs/operators';
import { io, Socket } from 'socket.io-client';
import { environment } from 'src/environments/environment';

@Injectable({
    providedIn: 'root',
})
export class OnlineChatHandlerService {
    // TODO refactor class for multi-room

    socket: Socket;
    private newRoomMessageSubject = new Subject<ChatMessage>();
    private errorSubject = new Subject<string>();
    private sysMessageSubject = new Subject<string>();
    // private userName: string | undefined;

    constructor(private accountService: AccountService, private authService: AuthService) {
        this.authService.isAuthenticated$.subscribe((isAuth) => {
            if (!isAuth) {
                this.leaveChatRoom();
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

    leaveChatRoom() {
        if (!this.socket) {
            return;
        }
        this.socket.close();
        this.socket = undefined as unknown as Socket;
    }

    sendMessage(content: string) {
        if (!this.socket) {
            throw Error('No socket to send message from');
        }
        this.socket.emit('newMessage', content);
    }

    joinChatRoom(roomID: string) {
        this.socket = this.connectToSocket();
        this.bindRoomChannels(roomID);
    }
    // TODO refactor
    private bindRoomChannels(roomID: string) {
        this.socket.on('error', (errorContent: string) => {
            this.receiveChatServerError(errorContent);
        });

        this.socket.on('roomMessages', (message: ChatMessage) => {
            this.receiveServerMessage(message);
        });

        this.socket.on('systemMessages', (content: string) => {
            this.receiveSystemMessage(content);
        });

        this.socket.emit('joinRoom', roomID);
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

    private receiveSystemMessage(content: string) {
        this.sysMessageSubject.next(content);
    }

    get isConnected(): boolean {
        return isSocketConnected(this.socket);
    }

    get opponentMessage$(): Observable<ChatMessage> {
        return this.newRoomMessageSubject.pipe(
            filter((chatMessage: ChatMessage) => {
                const name = chatMessage.from;
                const account = this.accountService.account;
                if (!account) {
                    throw Error('No account defined');
                }
                return name !== account.name;
            }),
        );
    }

    get newRoomMessages$(): Observable<ChatMessage> {
        return this.newRoomMessageSubject;
    }

    get errorMessage$(): Observable<string> {
        return this.errorSubject;
    }

    get systemMessage$(): Observable<string> {
        return this.sysMessageSubject;
    }
}
