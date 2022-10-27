import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ConversationGetQuery } from '@app/chat/interfaces/conversation-get-query';
import { Conversation, ConversationCreation } from '@app/chat/interfaces/conversation.interface';
import { Pagination } from '@app/chat/interfaces/pagination.interface';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { tap } from 'rxjs/operators';
import { environment } from 'src/environments/environment';

export interface ConversationsGetRes {
    pagination: Pagination;
    conversations: Conversation[];
}

@Injectable({
    providedIn: 'root',
})
export class ConversationService {
    joinedConversationUpdated$ = new Subject<void>();
    joinedConversations$ = new BehaviorSubject<Conversation[]>([]);

    private currentConversationSubject = new BehaviorSubject<Conversation | undefined>(undefined);
    get currentConversation$(): Observable<Conversation | undefined> {
        return this.currentConversationSubject;
    }

    get currentConversation(): Conversation | undefined {
        return this.currentConversationSubject.value;
    }

    constructor(private http: HttpClient) {
        this.refreshJoinedConversations();
    }

    setCurrentConversation(conversation: Conversation) {
        if (conversation === this.currentConversation) {
            return;
        }
        this.currentConversationSubject.next(conversation);
    }

    refreshJoinedConversations() {
        this.http.get(`${environment.serverUrl}/conversations?joined=true`).subscribe(
            (body) => {
                const { conversations } = body as { conversations: Conversation[] };
                this.joinedConversations$.next(conversations as Conversation[]);
                this.joinedConversationUpdated$.next();
            },
            () => {
                return;
            },
        );
    }

    leaveConversation(conversationId: string) {
        return this.http.get(`${environment.serverUrl}/conversations/${conversationId}/quit`).pipe(
            tap(() => {
                this.refreshJoinedConversations();
            }),
        );
    }

    createConversation(conversationCreation: ConversationCreation) {
        return this.http.post(`${environment.serverUrl}/conversations`, conversationCreation).pipe(
            tap((body) => {
                const {
                    conversation: { _id: conversationId },
                } = body as { conversation: { _id: string } };
                this.joinConversation(conversationId).subscribe();
            }),
        );
    }

    joinConversation(conversationId: string) {
        return this.http.get(`${environment.serverUrl}/conversations/${conversationId}/join`).pipe(
            tap(() => {
                this.refreshJoinedConversations();
            }),
        );
    }

    getConversations(query: ConversationGetQuery): Observable<ConversationsGetRes> {
        const {
            pagination: { perPage, page },
            search,
        } = query;
        if (!search) {
            return this.http.get<ConversationsGetRes>(`${environment.serverUrl}/conversations`, { params: { perPage, page } });
        }
        return this.http.get<ConversationsGetRes>(`${environment.serverUrl}/conversations`, { params: { perPage, page, search } });
    }

    focusOnConversation(conversationName: string) {
        const conversation = this.joinedConversations$.value.find(({ name }) => name === conversationName);
        if (conversation === undefined) {
            throw Error('The conversation you are trying to focus does not exist');
        }
        this.setCurrentConversation(conversation);
    }
}
