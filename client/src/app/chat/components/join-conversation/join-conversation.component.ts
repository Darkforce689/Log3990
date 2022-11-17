/* eslint-disable no-underscore-dangle */
import { AfterViewInit, Component, ViewChild } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { MatInput } from '@angular/material/input';
import { MatPaginator } from '@angular/material/paginator';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableDataSource } from '@angular/material/table';
import { Conversation, JoinConversationUI } from '@app/chat/interfaces/conversation.interface';
import { Pagination } from '@app/chat/interfaces/pagination.interface';
import { ConversationService } from '@app/chat/services/conversation/conversation.service';
import { AccountService } from '@app/services/account.service';
import { first, map } from 'rxjs/operators';

@Component({
    selector: 'app-join-conversation',
    templateUrl: './join-conversation.component.html',
    styleUrls: ['./join-conversation.component.scss'],
})
export class JoinConversationComponent implements AfterViewInit {
    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatInput) searchInput: MatInput;

    displayedColumns: string[] = ['joined', 'name'];

    selectedRow: JoinConversationUI | undefined = undefined;

    search: string = '';

    dataSource = new MatTableDataSource<JoinConversationUI>();

    get user() {
        return this.accountService.account ?? { avatar: '' };
    }

    constructor(
        private dialogRef: MatDialogRef<JoinConversationComponent>,
        private conversationService: ConversationService,
        private snackBarRef: MatSnackBar,
        private accountService: AccountService,
    ) {}

    isRowSelected(conversation: JoinConversationUI) {
        return this.selectedRow === conversation;
    }

    setSelectedRow(conversation: JoinConversationUI) {
        if (!this.isRowSelectable(conversation)) {
            return;
        }
        this.selectedRow = conversation;
    }

    isRowSelectable(conversation: JoinConversationUI) {
        return !conversation.joined;
    }

    ngAfterViewInit() {
        const { pageIndex: page, pageSize: perPage } = this.paginator;
        const pagination = { page, perPage };

        this.getConversations(pagination, '').subscribe((conversations) => {
            this.dataSource.data = conversations;
            this.paginator.length = conversations.length < perPage ? conversations.length : 2 * perPage;
        });
    }

    transformConversations(conversations: Conversation[]) {
        const joinedConversationId = new Set(this.conversationService.joinedConversations$.value.map((conversation) => conversation._id));
        return conversations.map((conversation) => {
            return {
                joined: joinedConversationId.has(conversation._id),
                ...conversation,
            };
        });
    }

    joinConversation() {
        const conversation = this.selectedRow;
        if (conversation === undefined) {
            return;
        }
        this.conversationService.joinConversation(conversation._id).subscribe(
            () => {
                this.dialogRef.close();
                this.conversationService.joinedConversationUpdated$.pipe(first()).subscribe(() => {
                    this.conversationService.focusOnConversation(conversation.name);
                });
            },
            () => {
                this.snackBarRef.open("Vous n'avez pas pu rejoindre la conversation: une erreur s'est produite", 'OK', { duration: 3000 });
            },
        );
    }

    getConversations(pagination: Pagination, search: string) {
        return this.conversationService
            .getConversations({ pagination, search: search.length === 0 ? undefined : search })
            .pipe(map(({ conversations }) => this.transformConversations(conversations)));
    }

    getConversationsInPage() {
        const { pageIndex: page, pageSize: perPage } = this.paginator;
        const pagination = { page, perPage };
        this.getConversations(pagination, this.search).subscribe((conversations) => {
            this.dataSource.data = conversations;
            this.paginator.length = conversations.length < perPage ? page * perPage + conversations.length : (page + 2) * perPage;
        });
    }

    searchConversations() {
        this.search = this.searchInput.value;
        this.paginator.pageIndex = 0;
        this.getConversationsInPage();
    }

    cancel() {
        this.dialogRef.close();
    }
}
