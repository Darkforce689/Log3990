import { Component, OnDestroy, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { CreateConversationComponent } from '@app/chat/components/create-conversation/create-conversation.component';
import { JoinConversationComponent } from '@app/chat/components/join-conversation/join-conversation.component';
import { GAME_CONVO_NAME, GENERAL_CHANNEL } from '@app/chat/constants';
import { Conversation } from '@app/chat/interfaces/conversation.interface';
import { ConversationService } from '@app/chat/services/conversation/conversation.service';
import { BehaviorSubject, Subject } from 'rxjs';
import { map, takeUntil } from 'rxjs/operators';

@Component({
    selector: 'app-conversation-picker',
    templateUrl: './conversation-picker.component.html',
    styleUrls: ['./conversation-picker.component.scss'],
})
export class ConversationPickerComponent implements OnInit, OnDestroy {
    selectedConversationIndex = 0;
    readonly conversations$ = new BehaviorSubject<Conversation[]>([]);
    options = ['Rejoindre', 'Créer'];

    private destroy$ = new Subject<void>();

    get conversations() {
        return this.conversations$.value;
    }

    get currentConversation() {
        return this.conversationService.currentConversation;
    }

    get currentConversation$() {
        return this.conversationService.currentConversation$;
    }

    constructor(private conversationService: ConversationService, private matDialog: MatDialog) {
        this.conversationService.joinedConversations$
            .pipe(takeUntil(this.destroy$))
            .pipe(map((conversations) => this.arrangeConversations(conversations)))
            .subscribe((arrangedConvos) => {
                this.conversations$.next(arrangedConvos);
                // TODO does not work all the time need to investigate
                this.setSelectedConversation(arrangedConvos[0]);
            });
    }

    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
    }

    ngOnInit(): void {
        this.currentConversation$.subscribe((currentConversation) => {
            if (!currentConversation) {
                return;
            }
            this.focusOnConversation(currentConversation);
        });
    }

    focusOnConversation(conversation: Conversation) {
        const conversationIndex = this.conversations.findIndex(({ name }) => name === conversation.name);
        this.selectedConversationIndex = conversationIndex;
    }

    changeSelectedConversation() {
        const currentConversation = this.conversations[this.selectedConversationIndex];
        this.setSelectedConversation(currentConversation);
    }

    setSelectedConversation(conversation: Conversation) {
        this.conversationService.setCurrentConversation(conversation);
    }

    isConversationLeavable(conversation: Conversation) {
        return conversation.name !== GAME_CONVO_NAME && conversation.name !== GENERAL_CHANNEL;
    }

    leaveConversation(conversation: Conversation) {
        // eslint-disable-next-line no-underscore-dangle
        this.conversationService.leaveConversation(conversation._id).subscribe();
    }

    openAddConversationMenu(option: string) {
        const dialogOption = {
            disableClose: true,
            autoFocus: true,
        };

        if (option === 'Rejoindre') {
            this.matDialog.open(JoinConversationComponent, dialogOption);
        } else {
            this.matDialog.open(CreateConversationComponent, dialogOption);
        }
    }

    private arrangeConversations(conversations: Conversation[]): Conversation[] {
        // arrange conversations with this priority: [ game, general, ...]
        const general = conversations.find(({ name }) => name === GENERAL_CHANNEL);
        const game = conversations.find(({ name }) => name === GAME_CONVO_NAME);
        const otherConvos = conversations.filter(({ name }) => name !== GAME_CONVO_NAME && name !== GENERAL_CHANNEL);
        const arrangedConvos = [] as Conversation[];
        if (game) {
            arrangedConvos.push(game);
        }
        if (general) {
            arrangedConvos.push(general);
        }
        arrangedConvos.push(...otherConvos);
        return arrangedConvos;
    }
}
