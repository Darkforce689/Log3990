import { Component, ViewChild } from '@angular/core';
import { MatRipple, RippleConfig } from '@angular/material/core';
import { MatDialog, MatDialogConfig, MatDialogRef } from '@angular/material/dialog';
import { LoadingGameComponent } from '@app/components/modals/loading-game/loading-game.component';
import { NewOnlineGameFormComponent } from '@app/components/modals/new-online-game-form/new-online-game-form.component';
import { PendingGamesComponent } from '@app/components/modals/pending-games/pending-games.component';
import { GameManagerService } from '@app/game-logic/game/games/game-manager/game-manager.service';
import { GameSettings } from '@app/game-logic/game/games/game-settings.interface';
import { BotDifficulty } from '@app/services/bot-difficulty';
import { GameLauncherService } from '@app/socket-handler/game-launcher/game-laucher';
import { GameMode } from '@app/socket-handler/interfaces/game-mode.interface';
import { OnlineGameSettingsUI } from '@app/socket-handler/interfaces/game-settings-multi.interface';
import { NewOnlineGameSocketHandler } from '@app/socket-handler/new-online-game-socket-handler/new-online-game-socket-handler.service';
import { Subscription } from 'rxjs';

@Component({
    selector: 'app-new-game-page',
    templateUrl: './new-game-page.component.html',
    styleUrls: ['./new-game-page.component.scss'],
})
export class NewGamePageComponent {
    @ViewChild(MatRipple) ripple: MatRipple;

    startGame$$: Subscription;
    gameMode = GameMode.Classic;
    gameReady$$: Subscription;

    constructor(
        private dialog: MatDialog,
        private gameLaucherService: GameLauncherService,
        private socketHandler: NewOnlineGameSocketHandler,
        private gameManager: GameManagerService,
    ) {}

    triggerRipple() {
        const rippleConfig: RippleConfig = {
            centered: false,
            animation: {
                enterDuration: 500,
                exitDuration: 700,
            },
        };
        this.ripple.launch(rippleConfig);
    }

    openMultiGameForm() {
        this.openGameForm();
    }

    openGameForm() {
        const dialogConfig = new MatDialogConfig();
        dialogConfig.autoFocus = true;
        dialogConfig.disableClose = true;
        dialogConfig.minWidth = 60;
        dialogConfig.data = { gameMode: this.gameMode };

        const dialogRef = this.dialog.open(NewOnlineGameFormComponent, dialogConfig);
        dialogRef.afterClosed().subscribe((gameSettings: GameSettings) => {
            if (!gameSettings) {
                return;
            }

            const onlineGameSettings: OnlineGameSettingsUI = {
                ...gameSettings,
                gameMode: this.gameMode,
                // TODO GL3A22107-32 : Implement new game parameter :
                botDifficulty: BotDifficulty.Expert,
                playerNames: [],
            };
            this.socketHandler.createGame(onlineGameSettings);
            this.openWaitingForPlayer();
        });
    }

    openWaitingForPlayer() {
        this.startGame$$?.unsubscribe();
        const secondDialogConfig = new MatDialogConfig();
        secondDialogConfig.autoFocus = true;
        secondDialogConfig.disableClose = true;

        this.gameLaucherService.waitForOnlineGameStart();
    }

    openPendingGames() {
        this.startGame$$?.unsubscribe();
        const pendingGamesDialogConfig = new MatDialogConfig();
        pendingGamesDialogConfig.autoFocus = true;
        pendingGamesDialogConfig.disableClose = true;
        pendingGamesDialogConfig.minWidth = 550;
        pendingGamesDialogConfig.data = this.gameMode;
        this.dialog.open(PendingGamesComponent, pendingGamesDialogConfig);
    }

    openLoadingGame(): MatDialogRef<LoadingGameComponent> {
        const loadingGameDialogConfig = new MatDialogConfig();
        loadingGameDialogConfig.disableClose = true;
        loadingGameDialogConfig.width = '255px';
        const loadingGameDialog = this.dialog.open(LoadingGameComponent, loadingGameDialogConfig);
        loadingGameDialog.afterClosed().subscribe((isCanceled) => {
            if (isCanceled) {
                this.gameReady$$.unsubscribe();
                this.gameManager.stopGame();
            }
        });
        return loadingGameDialog;
    }

    get isMagicGame() {
        return this.gameMode === GameMode.Magic;
    }

    set isMagicGame(value: boolean) {
        this.gameMode = value ? GameMode.Magic : GameMode.Classic;
    }
}
