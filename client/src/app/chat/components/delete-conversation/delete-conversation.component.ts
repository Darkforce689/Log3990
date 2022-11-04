import { AfterViewInit, Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableDataSource } from '@angular/material/table';
import { Conversation } from '@app/chat/interfaces/conversation.interface';
import { ConversationService } from '@app/chat/services/conversation/conversation.service';
import { BehaviorSubject, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
    selector: 'app-delete-conversation',
    templateUrl: './delete-conversation.component.html',
    styleUrls: ['./delete-conversation.component.scss'],
})
export class DeleteConversationComponent implements OnDestroy, OnInit, AfterViewInit {
    @ViewChild(MatPaginator) paginator: MatPaginator;

    dataSource = new MatTableDataSource<Conversation>();
    displayedColumns = ['name', 'deleteConversation'];
    isDeleting = new BehaviorSubject(false);
    private destroy$ = new Subject<void>();

    constructor(
        private dialogRef: MatDialogRef<DeleteConversationComponent>,
        private conversationService: ConversationService,
        private snackBarRef: MatSnackBar,
    ) {}

    ngOnInit(): void {
        this.getCreatedConversations();
    }

    ngAfterViewInit(): void {
        this.dataSource.paginator = this.paginator;
    }

    ngOnDestroy(): void {
        this.destroy$.next();
    }

    deleteConversation(conversation: Conversation) {
        const { _id: conversationId } = conversation;
        this.isDeleting.next(true);
        this.conversationService
            .deleteConversation(conversationId)
            .pipe(takeUntil(this.destroy$))
            .subscribe(
                () => {
                    this.getCreatedConversations();
                    this.isDeleting.next(false);
                },
                () => {
                    this.snackBarRef.open('Un problème est survenu lors de la suppression de la conversation.', 'Ok', { duration: 2000 });
                    this.isDeleting.next(false);
                },
            );
    }

    getCreatedConversations() {
        const conversation$ = this.conversationService.getCreatedConversations();
        conversation$.pipe(takeUntil(this.destroy$)).subscribe((conversations) => {
            this.dataSource.data = conversations;
        });
    }

    cancel() {
        this.dialogRef.close();
    }
}
