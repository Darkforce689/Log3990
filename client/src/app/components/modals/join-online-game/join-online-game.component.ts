import { AfterContentChecked, ChangeDetectorRef, Component, Inject } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ErrorDialogComponent } from '@app/components/modals/error-dialog/error-dialog.component';
import { GameLauncherService } from '@app/socket-handler/game-launcher/game-laucher';
import { OnlineGameSettings } from '@app/socket-handler/interfaces/game-settings-multi.interface';
import { NewOnlineGameSocketHandler } from '@app/socket-handler/new-online-game-socket-handler/new-online-game-socket-handler.service';
@Component({
    selector: 'app-join-online-game',
    templateUrl: './join-online-game.component.html',
    styleUrls: ['./join-online-game.component.scss'],
})
export class JoinOnlineGameComponent implements AfterContentChecked {
    password: FormControl = new FormControl('', []);
    deleted: boolean = false;
    constructor(
        @Inject(MAT_DIALOG_DATA) public data: OnlineGameSettings,
        private dialogRef: MatDialogRef<JoinOnlineGameComponent>,
        private dialog: MatDialog,
        private cdref: ChangeDetectorRef,
        private socketHandler: NewOnlineGameSocketHandler,
        private gameLaucherService: GameLauncherService,
    ) {
        this.socketHandler.pendingGames$.subscribe((pendingGames) => {
            if (pendingGames.find((settings) => this.data.id === settings.id) !== undefined) {
                return;
            }
            if (this.socketHandler.observableGames$.value.find((settings) => this.data.id === settings.id) !== undefined) {
                return;
            }
            this.deleted = true;
        });
    }

    ngAfterContentChecked() {
        this.cdref.detectChanges();
    }

    cancel(): void {
        this.dialogRef.close();
        this.deleted = false;
        this.password.reset();
    }

    sendParameter(): void {
        this.dialogRef.close(this.password.value);
        this.socketHandler.joinPendingGame(this.data.id, this.password.value);
        this.socketHandler.error$.subscribe((error: string) => {
            if (error) {
                if (error === 'Mauvais mot de passe' || error === "L'hôte vous a retiré de la partie") {
                    this.dialog.closeAll();
                }
                this.dialog.open(ErrorDialogComponent, { disableClose: true, autoFocus: true, data: error });
            }
        });

        this.gameLaucherService.waitForOnlineGameStart();
    }

    get randomBonusType() {
        return this.data.randomBonus ? 'est activé' : 'est désactivé';
    }

    get privateGameType() {
        return this.data.privateGame ? 'Privée' : 'Publique';
    }

    get hasPassword() {
        return this.data.password !== undefined;
    }
}
