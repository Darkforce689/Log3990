import { Injectable } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
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

    constructor(
        private gameManager: GameManagerService,
        private router: Router,
        private socketHandler: NewOnlineGameSocketHandler,
        private dialog: MatDialog,
    ) {}

    waitForOnlineGameStart() {
        this.startGame$$?.unsubscribe();
        const dialogRef = this.dialog.open(WaitingForOtherPlayersComponent, {
            disableClose: true,
            width: '500px',
            minHeight: '350px',
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

    private startOnlineGame(onlineGameSettings: OnlineGameSettings) {
        const gameToken = onlineGameSettings.id;
        this.socketHandler.resetGameToken();
        this.gameManager.joinOnlineGame(gameToken, onlineGameSettings);
        this.router.navigate(['/game']);
    }
}
