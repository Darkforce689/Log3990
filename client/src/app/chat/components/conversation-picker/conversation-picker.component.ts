import { ChangeDetectorRef, Component, Input, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTabGroup } from '@angular/material/tabs';
import { CreateConversationComponent } from '@app/chat/components/create-conversation/create-conversation.component';
import { DeleteConversationComponent } from '@app/chat/components/delete-conversation/delete-conversation.component';
import { JoinConversationComponent } from '@app/chat/components/join-conversation/join-conversation.component';
import { GAME_CONVO_NAME, GENERAL_CHANNEL } from '@app/chat/constants';
import { Conversation } from '@app/chat/interfaces/conversation.interface';
import { ConversationService } from '@app/chat/services/conversation/conversation.service';
import { PopChatService } from '@app/services/pop-chat.service';
import { BehaviorSubject, Subject } from 'rxjs';
import { map, takeUntil } from 'rxjs/operators';

@Component({
    selector: 'app-conversation-picker',
    templateUrl: './conversation-picker.component.html',
    styleUrls: ['./conversation-picker.component.scss'],
})
export class ConversationPickerComponent implements OnInit, OnDestroy {
    @Input() isPoped = false;
    @ViewChild(MatTabGroup) matGroup: MatTabGroup | undefined;
    selectedConversationIndex = 0;
    readonly conversations$ = new BehaviorSubject<Conversation[]>([]);
    options = ['Rejoindre', 'Créer', 'Supprimer'];
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

    constructor(
        private conversationService: ConversationService,
        private matDialog: MatDialog,
        private snackBarRef: MatSnackBar,
        private cdRef: ChangeDetectorRef,
        public popOutService: PopChatService,
    ) {}

    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
    }

    ngOnInit(): void {
        this.currentConversation$.subscribe((currentConversation) => {
            if (!currentConversation) {
                this.selectedConversationIndex = 0;
                this.changeSelectedConversation();
                return;
            }
            this.cdRef.detectChanges();
            this.focusOnConversation(currentConversation);
        });

        this.conversationService.joinedConversations$
            .pipe(takeUntil(this.destroy$))
            .pipe(map((conversations) => this.arrangeConversations(conversations)))
            .subscribe((arrangedConvos) => {
                if (this.selectedConversationIndex >= arrangedConvos.length) {
                    const selected = this.conversations[this.selectedConversationIndex];
                    this.selectedConversationIndex = arrangedConvos.findIndex(({ name }) => name === selected.name);
                    this.changeSelectedConversation();
                }
                this.conversations$.next(arrangedConvos);
                this.cdRef.detectChanges();
            });
    }

    focusOnConversation(conversation: Conversation) {
        const conversationIndex = this.conversations.findIndex(({ name }) => name === conversation.name);
        if (!this.matGroup) {
            return;
        }
        this.matGroup.selectedIndex = conversationIndex;
    }

    onSelectedTab(tabIndex: number) {
        const currentConversation = this.conversations[tabIndex];
        this.setSelectedConversation(currentConversation);
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
        this.conversationService.leaveConversation(conversation._id).subscribe(
            () => {
                this.selectedConversationIndex = 0;
                this.changeSelectedConversation();
            },
            () => {
                this.snackBarRef.open("Les/La conversation(s) supprimée(s) ont été retirées de l'affichage", 'Ok', { duration: 4000 });
            },
        );
    }

    openAddConversationMenu(option: string) {
        const dialogOption = {
            disableClose: true,
            autoFocus: true,
        };

        switch (option) {
            case 'Rejoindre':
                this.matDialog.open(JoinConversationComponent, dialogOption);
                break;
            case 'Créer':
                this.matDialog.open(CreateConversationComponent, dialogOption);
                break;
            case 'Supprimer':
                this.matDialog.open(DeleteConversationComponent, dialogOption);
                break;
            default:
                throw Error(`No modal bound to option: ${option}`);
        }
    }

    openWindow() {
        this.popOutService.toggleExternalWindow();
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
