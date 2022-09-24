import { Injectable } from '@angular/core';
import { ChatMessage, Message, MessageType } from '@app/game-logic/messages/message.interface';
import { OnlineChatHandlerService } from '@app/game-logic/messages/online-chat-handler/online-chat-handler.service';
import { BehaviorSubject } from 'rxjs';

@Injectable({
    providedIn: 'root',
})
export class MessagesService {
    static readonly sysName = 'System';
    static readonly sysErrorName = 'SystemError';
    messagesLog: Message[] = [];
    messages$: BehaviorSubject<Message[]> = new BehaviorSubject([] as Message[]);

    constructor(private onlineChat: OnlineChatHandlerService) {
        this.onlineChat.opponentMessage$.subscribe((chatMessage: ChatMessage) => {
            this.receiveOpponentMessage(chatMessage);
        });

        this.onlineChat.playerMessage$.subscribe((chatMessage: ChatMessage) => {
            this.receivePlayerMessage(chatMessage);
        });

        this.onlineChat.errorMessage$.subscribe((errorContent: string) => {
            this.receiveErrorMessage(errorContent);
        });

        this.onlineChat.systemMessage$.subscribe((content: string) => {
            this.receiveSystemMessage(content);
        });
    }

    receiveSystemMessage(content: string) {
        const systemMessage: Message = {
            content,
            from: MessagesService.sysName,
            type: MessageType.System,
        };
        this.addMessageToLog(systemMessage);
    }

    receiveErrorMessage(content: string) {
        const errorMessage = {
            content,
            from: MessagesService.sysErrorName,
            type: MessageType.System,
        };
        this.addMessageToLog(errorMessage);
    }

    receivePlayerMessage(chatMessage: ChatMessage) {
        const { content, from, date } = chatMessage;
        const message: Message = {
            content,
            from,
            date,
            type: MessageType.Player1,
        };
        this.addMessageToLog(message);
    }

    receiveNonDistributedPlayerMessage(content: string) {
        // TODO add to not distributed message
        // this.addMessageToLog(message);
        if (this.onlineChat.isConnected) {
            this.onlineChat.sendMessage(content);
        }
    }

    receiveOpponentMessage(chatMessage: ChatMessage) {
        const { content, from, date } = chatMessage;
        const message: Message = {
            content,
            from,
            date,
            type: MessageType.Player2,
        };
        this.addMessageToLog(message);
    }

    clearLog(): void {
        this.messagesLog = [];
        this.messages$.next(this.messagesLog);
    }

    private addMessageToLog(message: Message) {
        this.messagesLog.push(message);
        this.messages$.next(this.messagesLog);
    }
}
