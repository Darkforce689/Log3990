import { AfterContentChecked, ChangeDetectorRef, Component, Inject, OnInit } from '@angular/core';
import { AbstractControl, FormControl, ValidatorFn, Validators } from '@angular/forms';
import { MatDialog, MatDialogConfig, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { ErrorDialogComponent } from '@app/components/modals/error-dialog/error-dialog.component';
import { WaitingForOtherPlayersComponent } from '@app/components/modals/waiting-for-other-players/waiting-for-other-players.component';
import { MAX_NAME_LENGTH, MIN_NAME_LENGTH } from '@app/game-logic/constants';
import { GameManagerService } from '@app/game-logic/game/games/game-manager/game-manager.service';
import { OnlineGameSettings } from '@app/socket-handler/interfaces/game-settings-multi.interface';
import { UserAuth } from '@app/socket-handler/interfaces/user-auth.interface';
import { NewOnlineGameSocketHandler } from '@app/socket-handler/new-online-game-socket-handler/new-online-game-socket-handler.service';
import { Subscription } from 'rxjs';
import { takeWhile } from 'rxjs/operators';
const NO_WHITE_SPACE_RGX = /^\S*$/;
@Component({
    selector: 'app-join-online-game',
    templateUrl: './join-online-game.component.html',
    styleUrls: ['./join-online-game.component.scss'],
})
export class JoinOnlineGameComponent implements AfterContentChecked, OnInit {
    oppName: FormControl;
    startGame$$: Subscription;
    private playerName: string;

    constructor(
        @Inject(MAT_DIALOG_DATA) public data: OnlineGameSettings,
        private dialogRef: MatDialogRef<JoinOnlineGameComponent>,
        private dialog: MatDialog,
        private cdref: ChangeDetectorRef,
        private socketHandler: NewOnlineGameSocketHandler,
        // TODO :  REMOVE AND EXTRACT NEXT
        private gameManager: GameManagerService,
        private router: Router,
    ) {}

    ngOnInit() {
        this.playerName = this.data.playerName;
        this.oppName = new FormControl('', [
            Validators.required,
            Validators.minLength(MIN_NAME_LENGTH),
            Validators.maxLength(MAX_NAME_LENGTH),
            Validators.pattern(NO_WHITE_SPACE_RGX),
            this.forbiddenNameValidator(),
        ]);
    }

    ngAfterContentChecked() {
        this.cdref.detectChanges();
    }

    cancel(): void {
        this.dialogRef.close();
        this.oppName.reset();
    }

    sendParameter(): void {
        this.dialogRef.close(this.oppName.value);
        this.socketHandler.joinPendingGame(this.data.id, this.oppName.value);
        this.socketHandler.error$.subscribe((error: string) => {
            if (error) {
                this.dialog.open(ErrorDialogComponent, { disableClose: true, autoFocus: true, data: error });
            }
        });

        // TODO :  REMOVE AND EXTRACT NEXT
        this.startGame$$?.unsubscribe();
        const secondDialogConfig = new MatDialogConfig();
        const secondDialogRef = this.dialog.open(WaitingForOtherPlayersComponent, secondDialogConfig);
        secondDialogRef.afterOpened().subscribe(() => {
            this.socketHandler.isDisconnected$.subscribe((isDisconnected) => {
                if (isDisconnected) {
                    secondDialogRef.close();
                    this.socketHandler.disconnectSocket();
                }
            });
            this.startGame$$ = this.socketHandler.gameStarted$.pipe(takeWhile((val) => !val, true)).subscribe((gameSettings) => {
                if (!gameSettings) {
                    return;
                }
                secondDialogRef.close();
                console.log('sendParameter secondDialogRef.afterOpened');
                this.startOnlineGame(this.playerName, gameSettings);
                this.socketHandler.disconnectSocket();
            });
        });
    }

    // TODO :  REMOVE AND EXTRACT NEXT
    private startOnlineGame(userName: string, onlineGameSettings: OnlineGameSettings) {
        const gameToken = onlineGameSettings.id;
        const userAuth: UserAuth = { playerName: userName, gameToken };
        this.socketHandler.resetGameToken();
        this.gameManager.joinOnlineGame(userAuth, onlineGameSettings);
        this.router.navigate(['/game']);
    }

    private forbiddenNameValidator(): ValidatorFn {
        return (control: AbstractControl): { [key: string]: unknown } | null =>
            control.value !== this.playerName ? null : { forbidden: control.value };
    }

    get valid() {
        return this.oppName.valid;
    }

    get randomBonusType() {
        return this.data.randomBonus ? 'est activé' : 'est désactivé';
    }
}
