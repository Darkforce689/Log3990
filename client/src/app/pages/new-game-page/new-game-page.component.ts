import { AfterViewInit, ChangeDetectorRef, Component, ViewChild } from '@angular/core';
import { MatRipple, RippleConfig } from '@angular/material/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { LobbyGamesComponent, LobbyGameType } from '@app/components/modals/lobby-games/lobby-games.component';
import { NewOnlineGameFormComponent } from '@app/components/modals/new-online-game-form/new-online-game-form.component';
import { GameSettings } from '@app/game-logic/game/games/game-settings.interface';
import { PopChatService } from '@app/services/pop-chat.service';
import { GameLauncherService } from '@app/socket-handler/game-launcher/game-laucher';
import { GameMode } from '@app/socket-handler/interfaces/game-mode.interface';
import { OnlineGameSettings, OnlineGameSettingsUI } from '@app/socket-handler/interfaces/game-settings-multi.interface';
import { NewOnlineGameSocketHandler } from '@app/socket-handler/new-online-game-socket-handler/new-online-game-socket-handler.service';
import { Observable } from 'rxjs';

@Component({
    selector: 'app-new-game-page',
    templateUrl: './new-game-page.component.html',
    styleUrls: ['./new-game-page.component.scss'],
})
export class NewGamePageComponent implements AfterViewInit {
    @ViewChild(MatRipple) ripple: MatRipple;
    gameMode = GameMode.Classic;
    constructor(
        private dialog: MatDialog,
        private socketHandler: NewOnlineGameSocketHandler,
        private gameLauncher: GameLauncherService,
        public popOutService: PopChatService,
        private cdRef: ChangeDetectorRef,
    ) {}

    ngAfterViewInit(): void {
        this.popOutService.windowed$.subscribe(() => {
            this.cdRef.detectChanges();
        });
    }

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
        const dialogRef = this.dialog.open(NewOnlineGameFormComponent, {
            autoFocus: true,
            disableClose: true,
            data: { gameMode: this.gameMode },
        });
        dialogRef.afterClosed().subscribe((gameSettings: GameSettings) => {
            if (!gameSettings) {
                return;
            }

            const onlineGameSettings: OnlineGameSettingsUI = {
                ...gameSettings,
                gameMode: this.gameMode,
                botDifficulty: gameSettings.botDifficulty,
                playerNames: [],
                tmpPlayerNames: [],
            };
            this.socketHandler.createGame(onlineGameSettings);
            this.gameLauncher.waitForOnlineGameStart();
        });
    }

    openPendingGames() {
        const pendingGamesDialogConfig = this.setupLobbyGamesModal(this.socketHandler.pendingGames$, LobbyGameType.PendingGame);
        this.dialog.open(LobbyGamesComponent, pendingGamesDialogConfig);
    }

    openObservableGames() {
        const observableGamesDialogConfig = this.setupLobbyGamesModal(this.socketHandler.observableGames$, LobbyGameType.ObservableGame);
        this.dialog.open(LobbyGamesComponent, observableGamesDialogConfig);
    }

    get isMagicGame() {
        return this.gameMode === GameMode.Magic;
    }

    set isMagicGame(value: boolean) {
        this.gameMode = value ? GameMode.Magic : GameMode.Classic;
    }

    private setupLobbyGamesModal(lobbyGames$: Observable<OnlineGameSettings[]>, lobbyGameType: LobbyGameType, gameMode: GameMode = this.gameMode) {
        const pendingGamesDialogConfig = new MatDialogConfig();
        pendingGamesDialogConfig.autoFocus = true;
        pendingGamesDialogConfig.disableClose = true;
        pendingGamesDialogConfig.minWidth = 550;
        pendingGamesDialogConfig.data = { lobbyGameType, gameMode, lobbyGames$ };
        return pendingGamesDialogConfig;
    }
}
