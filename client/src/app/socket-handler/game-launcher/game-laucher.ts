import { Injectable } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { MessagesService } from '@app/chat/services/messages/messages.service';
import { ErrorDialogComponent } from '@app/components/modals/error-dialog/error-dialog.component';
import { JoinOnlineGameComponent } from '@app/components/modals/join-online-game/join-online-game.component';
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
        private messageService: MessagesService,
        private dialog: MatDialog,
    ) {}

    waitForOnlineGameStart() {
        this.startGame$$?.unsubscribe();
        this.errors$$?.unsubscribe();
        let gameSettingsReceived = false;
        this.socketHandler.gameSettings$.pipe().subscribe((gameSettings) => {
            if (!gameSettings) {
                return;
            }
            if (!gameSettingsReceived) {
                gameSettingsReceived = true;
                this.router.navigate(['/waiting-room']);
                this.messageService.joinGameConversation(gameSettings.id);
            }
        });
        this.socketHandler.isDisconnected$.subscribe(() => {
            this.socketHandler.disconnectSocket();
        });
        this.startGame$$ = this.socketHandler.gameStarted$.pipe(takeWhile((val) => !val, true)).subscribe((gameSettings) => {
            if (!gameSettings) {
                return;
            }
            this.startOnlineGame(gameSettings);
            this.socketHandler.disconnectSocket();
        });
    }

    cancelWait() {
        this.dialog.closeAll();
        this.socketHandler.disconnectSocket();
    }

    closeModals() {
        this.dialog.closeAll();
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
                    const errorDialog = this.dialog.open(ErrorDialogComponent, {
                        disableClose: true,
                        autoFocus: true,
                        data: "L'hôte a annulé la partie",
                    });
                    errorDialog.afterClosed().subscribe(() => {
                        this.router.navigate(['/home']);
                    });
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
        this.messageService.joinGameConversation(gameToken);
        this.router.navigate(['/game']);
    }
}
