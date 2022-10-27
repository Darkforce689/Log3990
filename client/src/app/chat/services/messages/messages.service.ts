/* eslint-disable no-underscore-dangle */
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { N_MESSAGE_TO_FETCH } from '@app/chat/constants';
import { ChatMessage, Message, MessageType } from '@app/chat/interfaces/message.interface';
import { ConversationService } from '@app/chat/services/conversation/conversation.service';
import { MessageFactoryService } from '@app/chat/services/message-factory/message-factory.service';
import { OnlineChatHandlerService } from '@app/chat/services/online-chat-handler/online-chat-handler.service';
import { AccountService } from '@app/services/account.service';
import { BehaviorSubject, zip } from 'rxjs';
import { first, takeWhile } from 'rxjs/operators';
import { environment } from 'src/environments/environment';

export interface MessagesUpdate {
    messages: Message[];
    reason: MessageUpdateReason;
}

export enum MessageUpdateReason {
    ReceivedPreviouslySentMessage = 'Mine',
    ReceiveMessageFromOther = 'newOther',
    Other = 'Other',
}

@Injectable({
    providedIn: 'root',
})
export class MessagesService {
    static readonly sysName = 'System';
    static readonly sysErrorName = 'SystemError';

    messages$: BehaviorSubject<MessagesUpdate> = new BehaviorSubject<MessagesUpdate>({
        messages: [] as Message[],
        reason: MessageUpdateReason.Other,
    });
    messages: Message[] = [];

    constructor(
        private onlineChat: OnlineChatHandlerService,
        private http: HttpClient,
        private messageFactory: MessageFactoryService,
        private conversationService: ConversationService,
        private accountService: AccountService,
    ) {
        this.onlineChat.newMessages$.subscribe((chatMessage: ChatMessage) => {
            if (!this.conversationService.currentConversation) {
                return;
            }

            const { name } = this.conversationService.currentConversation;

            if (name !== chatMessage.conversation) {
                return;
            }
            this.receiveNewMessage(chatMessage);
        });

        this.onlineChat.errorMessage$.subscribe((errorContent: string) => {
            this.receiveErrorMessage(errorContent);
        });

        this.onlineChat.systemMessage$.subscribe((content: string) => {
            this.receiveSystemMessage(content);
        });

        this.conversationService.joinedConversations$.subscribe((conversations) => {
            this.accountService.account$.pipe(takeWhile((account) => account === undefined, true)).subscribe((account) => {
                if (!account) {
                    return;
                }
                conversations.forEach((conversation) => {
                    this.onlineChat.joinChatRoomWithUser(conversation.name);
                });
            });
        });

        this.conversationService.currentConversation$.subscribe(async (conversation) => {
            if (!conversation) {
                this.clearLog();
                return;
            }

            const { _id: conversationId } = conversation;
            this.accountService.account$.pipe(takeWhile((account) => account === undefined, true)).subscribe((account) => {
                if (!account) {
                    return;
                }
                this.changeCurrentConversation(conversationId);
            });
        });
    }

    get currentConversation() {
        return this.conversationService.currentConversation;
    }

    disconnect() {
        this.onlineChat.disconnect();
        this.clearLog();
        // TODO clear messages etc
    }

    joinConversation(roomId: string) {
        this.onlineChat.joinChatRoomWithUser(roomId);
    }

    fetchNextMessagesFromCurrentConvo() {
        if (!this.currentConversation) {
            throw Error('No current conversation to fetch messages from');
        }
        this.fetchNextMessages(this.currentConversation._id);
    }

    fetchNextMessages(conversationId: string) {
        const params = {
            perPage: N_MESSAGE_TO_FETCH,
            page: 0,
            offset: this.messages.length,
        };
        this.http.get(`${environment.serverUrl}/conversations/${conversationId}/messages`, { params }).subscribe((body) => {
            const { messages: chatMessages } = body as { messages: ChatMessage[] };
            if (this.currentConversation && this.currentConversation._id !== conversationId) {
                return;
            }
            zip(...chatMessages.map((chatMessage) => this.messageFactory.createMessage(chatMessage))).subscribe((messages) => {
                this.messages.reverse();
                messages.forEach((message) => {
                    this.messages.push(message);
                });
                this.messages.reverse();
                this.messages$.next({ messages: this.messages, reason: MessageUpdateReason.Other });
            });
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

    receiveNonDistributedPlayerMessage(content: string) {
        // TODO add to not distributed message
        if (!this.conversationService.currentConversation) {
            return;
        }

        if (this.onlineChat.isConnected) {
            const { name: conversation } = this.conversationService.currentConversation;
            const message = {
                content,
                conversation,
            };
            this.onlineChat.sendMessage(message);
        }
    }

    receiveNewMessage(chatMessage: ChatMessage) {
        this.messageFactory
            .createMessage(chatMessage)
            .pipe(first())
            .subscribe((message) => {
                this.addMessageToLog(message);
            });
    }

    clearLog(): void {
        this.messages.splice(0, this.messages.length);
        this.messages$.next({ messages: this.messages, reason: MessageUpdateReason.Other });
    }

    private addMessageToLog(message: Message) {
        this.messages.push(message);
        const myName = this.accountService.account?.name;
        const reason = message.from === myName ? MessageUpdateReason.ReceivedPreviouslySentMessage : MessageUpdateReason.ReceiveMessageFromOther;
        this.messages$.next({ messages: this.messages, reason });
    }

    private changeCurrentConversation(conversationId: string) {
        this.messages.splice(0, this.messages.length);
        this.fetchNextMessages(conversationId);
    }
}
