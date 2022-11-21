import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Invitation } from '@app/invitations/interfaces/invitation.interface';

@Component({
    selector: 'app-invitation-modal',
    templateUrl: './invitation-modal.component.html',
    styleUrls: ['./invitation-modal.component.scss'],
})
export class InvitationModalComponent {
    constructor(@Inject(MAT_DIALOG_DATA) public invitation: Invitation, private dialogRef: MatDialogRef<InvitationModalComponent>) {}

    refuse() {
        this.dialogRef.close(false);
    }

    accept() {
        this.dialogRef.close(true);
    }
}
