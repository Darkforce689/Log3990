import { Injectable } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { ErrorDialogComponent } from '@app/components/modals/error-dialog/error-dialog.component';
import { JoinOnlineGameComponent } from '@app/components/modals/join-online-game/join-online-game.component';
import { WaitingForOtherPlayersComponent } from '@app/components/modals/waiting-for-other-players/waiting-for-other-players.component';
import { GameManagerService } from '@app/game-logic/game/games/game-manager/game-manager.service';
import { OnlineGameSettings } from '@app/socket-handler/interfaces/game-settings-multi.interface';
import { NewOnlineGameSocketHandler } from '@app/socket-handler/new-online-game-socket-handler/new-online-game-socket-handler.service';
import { Subscription } from 'rxjs';
import { takeWhile } from 'rxjs/operators';

@Injectable({
    providedIn: 'root',
})
export class GameLauncherService {
    private startGame$$: Subscription;
    private errors$$: Subscription;
    private passwordConfirmation$$: Subscription;

    constructor(
        private gameManager: GameManagerService,
        private router: Router,
        private socketHandler: NewOnlineGameSocketHandler,
        private dialog: MatDialog,
    ) {}

    waitForOnlineGameStart() {
        this.startGame$$?.unsubscribe();
        this.errors$$?.unsubscribe();
        const dialogRef = this.dialog.open(WaitingForOtherPlayersComponent, {
            disableClose: true,
        });
        this.errors$$ = this.socketHandler.error$.subscribe(() => {
            this.dialog.open(ErrorDialogComponent, { disableClose: true, autoFocus: true, data: "L'hôte vous a retiré de la partie" });
            dialogRef.close();
        });
        dialogRef.afterOpened().subscribe(() => {
            this.socketHandler.isDisconnected$.subscribe((isDisconnected) => {
                if (isDisconnected) {
                    dialogRef.close();
                    this.socketHandler.disconnectSocket();
                }
            });
            this.startGame$$ = this.socketHandler.gameStarted$.pipe(takeWhile((val) => !val, true)).subscribe((gameSettings) => {
                if (!gameSettings) {
                    return;
                }
                dialogRef.close();
                this.startOnlineGame(gameSettings);
                this.socketHandler.disconnectSocket();
            });
        });
    }

    cancelWait() {
        this.dialog.closeAll();
        this.socketHandler.disconnectSocket();
    }

    joinGame(id: string, hasPassword: boolean) {
        if (!hasPassword) {
            this.socketHandler.joinPendingGame(id);
            this.waitForOnlineGameStart();
            return;
        }
        this.passwordConfirmation(id);
    }

    private passwordConfirmation(id: string) {
        this.errors$$?.unsubscribe();
        this.passwordConfirmation$$?.unsubscribe();
        const joinPendingGame = this.dialog.open(JoinOnlineGameComponent, {
            autoFocus: true,
            disableClose: true,
        });
        joinPendingGame.afterOpened().subscribe(() => {
            this.errors$$ = this.socketHandler.error$.subscribe((error) => {
                if (error) {
                    this.dialog.open(ErrorDialogComponent, { disableClose: true, autoFocus: true, data: "L'hôte a annulé la partie" });
                    joinPendingGame.close();
                }
            });
        });
        joinPendingGame.beforeClosed().subscribe((password) => {
            if (!password) {
                return;
            }
            this.passwordConfirmation$$ = this.socketHandler.confirmPassword$.subscribe((isPassworValid: boolean | undefined) => {
                if (isPassworValid === undefined) {
                    return;
                }
                if (isPassworValid) {
                    this.waitForOnlineGameStart();
                    return;
                }
                this.dialog.open(ErrorDialogComponent, { disableClose: true, autoFocus: true, data: 'Mauvais mot de passe' });
            });
            this.socketHandler.joinPendingGame(id, password);
        });
    }

    private startOnlineGame(onlineGameSettings: OnlineGameSettings) {
        const gameToken = onlineGameSettings.id;
        this.socketHandler.resetGameToken();
        this.gameManager.joinOnlineGame(gameToken, onlineGameSettings);
        this.router.navigate(['/game']);
    }
}
