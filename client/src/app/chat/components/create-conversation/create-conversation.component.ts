import { HttpErrorResponse } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { CONVO_NAME_MAX_SIZE } from '@app/chat/constants';
import { ConversationCrudError } from '@app/chat/interfaces/conversation-creation-errors.enum';
import { ConversationService } from '@app/chat/services/conversation/conversation.service';
import { NOT_ONLY_SPACE_RGX } from '@app/game-logic/constants';
import { first } from 'rxjs/operators';

@Component({
    selector: 'app-create-conversation',
    templateUrl: './create-conversation.component.html',
    styleUrls: ['./create-conversation.component.scss'],
})
export class CreateConversationComponent {
    conversationNameForm = new FormControl('', [
        Validators.required,
        Validators.pattern(NOT_ONLY_SPACE_RGX),
        Validators.maxLength(CONVO_NAME_MAX_SIZE),
    ]);

    constructor(private dialogRef: MatDialogRef<CreateConversationComponent>, private conversationService: ConversationService) {}

    cancel() {
        this.dialogRef.close();
    }

    create() {
        this.conversationNameForm.markAllAsTouched();
        if (!this.conversationNameForm.valid) {
            return;
        }
        const name: string = this.conversationNameForm.value;
        const request$ = this.conversationService.createConversation({ name });
        request$.subscribe(
            () => {
                this.dialogRef.close();
                this.conversationService.joinedConversationUpdated$.pipe(first()).subscribe(() => {
                    this.conversationService.focusOnConversation(name);
                });
            },
            (httpError: HttpErrorResponse) => {
                const {
                    error: { errors },
                } = httpError;
                errors.forEach((error: string) => {
                    if (error === ConversationCrudError.ConversationAlreadyExist) {
                        this.conversationNameForm.setErrors({ alreadyExists: true });
                    }

                    if (error === ConversationCrudError.ConversationCreationForbiden) {
                        this.conversationNameForm.setErrors({ forbidenName: true });
                    }
                });
            },
        );
    }

    get conversationValid() {
        return (
            (!this.conversationNameForm.hasError('pattern') && !this.conversationNameForm.hasError('maxlength')) ||
            this.conversationNameForm.hasError('required')
        );
    }

    get conversationEmpty() {
        return this.conversationNameForm.hasError('required');
    }
}
