/* eslint-disable no-underscore-dangle */
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { GAME_CONVO_NAME, N_MESSAGE_TO_FETCH } from '@app/chat/constants';
import { ChatMessage, Message, MessageType, SystemMessage } from '@app/chat/interfaces/message.interface';
import { ConversationService } from '@app/chat/services/conversation/conversation.service';
import { MessageFactoryService } from '@app/chat/services/message-factory/message-factory.service';
import { OnlineChatHandlerService } from '@app/chat/services/online-chat-handler/online-chat-handler.service';
import { AccountService } from '@app/services/account.service';
import { ElectronIpcService } from '@app/services/electron-ipc.service';
import { BehaviorSubject, Subscription, zip } from 'rxjs';
import { first, takeWhile } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { v4 as uuid } from 'uuid';

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

    private joinedConversation$$: Subscription | undefined;
    private currentConversation$$: Subscription | undefined;
    private fetchingConvos = new Map<string, string>(); // convoId => requestId

    constructor(
        private onlineChat: OnlineChatHandlerService,
        private http: HttpClient,
        private messageFactory: MessageFactoryService,
        private conversationService: ConversationService,
        private accountService: AccountService,
        private electron: ElectronIpcService,
    ) {
        this.onlineChat.newMessages$.subscribe((chatMessage: ChatMessage) => {
            if (!this.conversationService.currentConversation) {
                return;
            }

            const { name, _id } = this.conversationService.currentConversation;
            const convoIdentifier = name === GAME_CONVO_NAME ? _id : name;
            if (convoIdentifier !== chatMessage.conversation) {
                return;
            }
            this.receiveNewMessage(chatMessage);
        });

        this.onlineChat.errorMessage$.subscribe((errorContent: string) => {
            this.receiveErrorMessage(errorContent);
        });

        this.onlineChat.systemMessage$.subscribe((message: SystemMessage) => {
            if (!this.conversationService.currentConversation) {
                return;
            }

            const { name, _id } = this.conversationService.currentConversation;
            const convoIdentifier = name === GAME_CONVO_NAME ? _id : name;
            if (convoIdentifier !== message.conversation) {
                return;
            }
            this.receiveSystemMessage(message);
        });

        this.electron.leaveGameConvo$.subscribe(() => {
            this.conversationService.leaveGameConversation();
        });

        this.electron.joinGameConvo$.subscribe((gameToken) => {
            this.conversationService.joinGameConversation(gameToken);
        });

        this.electron.externalWindow$.subscribe((isPoped) => {
            if (isPoped) {
                this.disconnect();
                return;
            }
            this.connect();
        });
    }

    get currentConversation() {
        return this.conversationService.currentConversation;
    }

    disconnect() {
        if (this.joinedConversation$$) {
            this.joinedConversation$$.unsubscribe();
            this.joinedConversation$$ = undefined;
        }

        if (this.currentConversation$$) {
            this.currentConversation$$.unsubscribe();
            this.currentConversation$$ = undefined;
        }
        this.onlineChat.disconnect();
        this.clearLog();
    }

    connect() {
        // Safe guard for account
        this.accountService.account$.pipe(takeWhile((account) => account === undefined, true)).subscribe((account) => {
            if (!account) {
                return;
            }

            this.listenJoinedConversations();
            this.listenCurrentConversation();
        });
    }

    refreshMessages() {
        this.disconnect();
        this.connect();
    }

    joinGameConversation(gameToken: string) {
        this.accountService.account$.pipe(takeWhile((account) => account === undefined, true)).subscribe((account) => {
            if (!account) {
                return;
            }
            this.onlineChat.joinChatRoomWithUser(gameToken);
            this.conversationService.joinGameConversation(gameToken);
            this.changeCurrentConversation(gameToken);
            this.electron.sendGameToken(gameToken);
        });
    }

    leaveGameConversation() {
        const conversation = this.conversationService.leaveGameConversation();
        if (!conversation) {
            return;
        }
        const { _id: roomId } = conversation;
        this.onlineChat.leaveChatRoom(roomId);
        this.electron.leaveGameConvo();
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

        const fetchingId = uuid();
        this.fetchingConvos.set(conversationId, fetchingId);

        this.http.get(`${environment.serverUrl}/conversations/${conversationId}/messages`, { params }).subscribe(
            (body) => {
                const currentFetchingId = this.fetchingConvos.get(conversationId);

                // IF NOT LAST FETCHING DISCARD
                if (currentFetchingId !== fetchingId) {
                    return;
                }
                this.fetchingConvos.delete(conversationId);

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
            },
            () => {
                const currentFetchingId = this.fetchingConvos.get(conversationId);
                // IF NOT LAST FETCHING DISCARD
                if (currentFetchingId !== fetchingId) {
                    return;
                }
                this.fetchingConvos.delete(conversationId);
                return;
            },
        );
    }

    receiveSystemMessage(sysMessage: SystemMessage) {
        const { date, content } = sysMessage;
        const systemMessage: Message = {
            content,
            date,
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
        if (!this.conversationService.currentConversation) {
            return;
        }

        if (this.onlineChat.isConnected) {
            const { name: conversationName, _id: conversationId } = this.conversationService.currentConversation;
            const conversation = conversationName === GAME_CONVO_NAME ? conversationId : conversationName;
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

    private listenJoinedConversations() {
        this.joinedConversation$$?.unsubscribe();
        this.joinedConversation$$ = this.conversationService.joinedConversations$.subscribe((conversations) => {
            this.onlineChat.joinChatRooms(conversations);
            if (!this.currentConversation) {
                this.conversationService.setFirstConversationCurrent();
            }
        });
    }

    private listenCurrentConversation() {
        this.currentConversation$$?.unsubscribe();
        this.currentConversation$$ = this.conversationService.currentConversation$.subscribe(async (conversation) => {
            if (!conversation) {
                return;
            }
            const { _id: conversationId } = conversation;
            this.changeCurrentConversation(conversationId);
        });
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
