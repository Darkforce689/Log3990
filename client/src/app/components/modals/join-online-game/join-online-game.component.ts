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
    myName: FormControl;

    constructor(
        @Inject(MAT_DIALOG_DATA) public data: OnlineGameSettings,
        private dialogRef: MatDialogRef<JoinOnlineGameComponent>,
        private dialog: MatDialog,
        private cdref: ChangeDetectorRef,
        private socketHandler: NewOnlineGameSocketHandler,
        private gameLaucherService: GameLauncherService,
    ) {}

    ngAfterContentChecked() {
        this.cdref.detectChanges();
    }

    cancel(): void {
        this.dialogRef.close();
        this.myName.reset();
    }

    sendParameter(): void {
        this.dialogRef.close();
        this.socketHandler.joinPendingGame(this.data.id);
        this.socketHandler.error$.subscribe((error: string) => {
            if (error) {
                this.dialog
                    .open(ErrorDialogComponent, { disableClose: true, autoFocus: true, data: error })
                    .afterClosed()
                    .subscribe(() => {
                        this.dialog.closeAll();
                    });
            }
        });

        this.gameLaucherService.waitForOnlineGameStart();
    }

    get randomBonusType() {
        return this.data.randomBonus ? 'est activé' : 'est désactivé';
    }
}
