import { Injectable } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { GameManagerService } from '@app/game-logic/game/games/game-manager/game-manager.service';
import { InvitationModalComponent } from '@app/invitations/components/invitation-modal/invitation-modal.component';
import { Invitation, InvitationDTO } from '@app/invitations/interfaces/invitation.interface';
import { InvitationFactoryService } from '@app/invitations/invitation-factory.service';
import { AppSocketHandlerService } from '@app/socket-handler/app-socket-handler.service';
import { GameLauncherService } from '@app/socket-handler/game-launcher/game-laucher';
import { JoinGameError, NewOnlineGameSocketHandler } from '@app/socket-handler/new-online-game-socket-handler/new-online-game-socket-handler.service';
import { first } from 'rxjs/operators';

@Injectable({
    providedIn: 'root',
})
export class InvitationService {
    private pendingInvitations: Invitation[] = [];
    private isDisplaying = false;

    get invitations$() {
        return this.appSocketHander.invitations$;
    }

    constructor(
        private appSocketHander: AppSocketHandlerService,
        private invitationFactory: InvitationFactoryService,
        private matDialog: MatDialog,
        private router: Router,
        private newGameSocketHandler: NewOnlineGameSocketHandler,
        private gameLaucherService: GameLauncherService,
        private snackBar: MatSnackBar,
        private gameManager: GameManagerService,
    ) {
        this.invitations$.subscribe((invitationDTO) => {
            // Discard invite if in game
            if (this.gameManager.isInGame) {
                return;
            }
            this.onNewInvitation(invitationDTO);
        });
    }

    private onNewInvitation(invitationDTO: InvitationDTO) {
        const invitation$ = this.invitationFactory.createInvitation(invitationDTO);
        invitation$.subscribe((invitation) => {
            this.pendingInvitations.push(invitation);
            if (this.isDisplaying) {
                return;
            }
            this.takeNextInvitation();
        });
    }

    private takeNextInvitation() {
        const invitation = this.pendingInvitations.shift();
        if (!invitation) {
            return;
        }
        const dialog = this.matDialog.open(InvitationModalComponent, { data: invitation });
        this.isDisplaying = true;
        dialog.afterClosed().subscribe((isAccepted) => {
            if (isAccepted) {
                this.acceptInvitation(invitation);
                this.clearPendingInvitations();
            }
            this.isDisplaying = false;
            this.takeNextInvitation();
        });
    }

    private clearPendingInvitations() {
        this.pendingInvitations = [];
    }

    private acceptInvitation(invitation: Invitation) {
        this.router.navigate(['new-game']);
        const {
            args: { id, password },
        } = invitation;
        this.newGameSocketHandler.error$.pipe(first()).subscribe((errorContent) => {
            this.gameLaucherService.cancelWait();

            const errorMessage = this.getErrorMessage(errorContent);
            if (!errorMessage) {
                return;
            }
            this.snackBar.open(errorMessage, 'OK', { duration: 3000 });
        });
        this.newGameSocketHandler.quitJoinedPendingGame();
        this.newGameSocketHandler.joinPendingGame(id, password);
        this.gameLaucherService.waitForOnlineGameStart();
    }

    private getErrorMessage(error: string): string | undefined {
        switch (error) {
            case JoinGameError.InexistantGame:
                return "L'invitation a expirée.";
            case JoinGameError.InvalidPassword:
                return "L'invitation est invalide.";
            case JoinGameError.NotEnoughPlace:
                return 'La partie que vous essayez de rejoindre est pleine.';
            default:
                return;
        }
    }
}
