import { Injectable } from '@angular/core';
import { isGameSettings } from '@app/game-logic/utils';
import { AccountService } from '@app/services/account.service';
import { OnlineGameSettings, OnlineGameSettingsUI } from '@app/socket-handler/interfaces/game-settings-multi.interface';
import { BehaviorSubject, Subject } from 'rxjs';
import { io, Socket } from 'socket.io-client';
import { environment } from 'src/environments/environment';

@Injectable({
    providedIn: 'root',
})
export class NewOnlineGameSocketHandler {
    pendingGameId$ = new BehaviorSubject<string | undefined>(undefined);
    deletedGame$ = new BehaviorSubject<boolean>(false);
    isWaiting$ = new BehaviorSubject<boolean>(false);
    pendingGames$ = new BehaviorSubject<OnlineGameSettings[]>([]);
    observableGames$ = new BehaviorSubject<OnlineGameSettings[]>([]);
    gameSettings$ = new BehaviorSubject<OnlineGameSettings | undefined>(undefined);
    gameStarted$ = new BehaviorSubject<OnlineGameSettings | undefined>(undefined);
    isGameOwner: boolean = false;
    isDisconnected$ = new Subject<boolean>();
    error$ = new Subject<string>();
    name: string;
    socket: Socket;
    kickedErrorMsg = "L'hôte vous à retirer de la partie";

    constructor(private account: AccountService) {}

    resetGameToken() {
        this.gameStarted$.next(undefined);
        this.isGameOwner = false;
        this.deletedGame$.next(false);
    }

    createGame(gameSettings: OnlineGameSettingsUI) {
        this.connect();
        if (!isGameSettings(gameSettings)) {
            throw Error('Games Settings are not valid. Cannot create a game.');
        }
        this.socket.emit('createGame', gameSettings);
        this.isGameOwner = true;
        this.deletedGame$.next(false);
        this.waitForOtherPlayers();
    }

    listenForPendingGames() {
        this.connect();
        this.socket.on('pendingGames', (pendingGames: OnlineGameSettings[], observableGames: OnlineGameSettings[]) => {
            this.pendingGames$.next(pendingGames);
            this.observableGames$.next(observableGames);
        });
        this.deletedGame$.next(false);
    }

    joinPendingGame(id: string, password: string) {
        if (!this.socket.connected) {
            throw Error("Can't join game, not connected to server");
        }
        this.socket.emit('joinGame', id, password);
        this.listenForUpdatedGameSettings();
        this.listenErrorMessage();
        this.listenForGameStart();
        this.listenForHostQuit();
    }

    listenForHostQuit() {
        this.socket.on('hostQuit', () => this.deletedGame$.next(true));
    }

    launchGame() {
        if (!this.socket.connected) {
            throw Error("Can't launch game, not connected to server");
        }
        if (this.pendingGameId$.value === undefined) {
            throw Error("Can't launch game, no pending game id");
        }
        this.socket.emit('launchGame', this.pendingGameId$.value);
        this.listenForGameStart();
    }

    kickPlayer(playerId: string) {
        if (!this.socket.connected) {
            throw Error("Can't kick player, not connected to server");
        }
        if (this.pendingGameId$.value === undefined) {
            throw Error("Can't kick player, no pending game id");
        }
        this.socket.emit('kickPlayer', this.pendingGameId$.value, playerId);
    }

    acceptPlayer(playerId: string) {
        if (!this.socket.connected) {
            throw Error("Can't accept player, not connected to server");
        }
        if (this.pendingGameId$.value === undefined) {
            throw Error("Can't accept player, no pending game id");
        }
        this.socket.emit('acceptPlayer', this.pendingGameId$.value, playerId);
    }

    refusePlayer(playerId: string) {
        if (!this.socket.connected) {
            throw Error("Can't refuse player, not connected to server");
        }
        if (this.pendingGameId$.value === undefined) {
            throw Error("Can't refuse player, no pending game id");
        }
        this.socket.emit('refusePlayer', this.pendingGameId$.value, playerId);
    }

    disconnectSocket() {
        if (!this.socket) {
            return;
        }
        this.socket.disconnect();
        this.gameSettings$.next(undefined);
    }

    private connect() {
        this.socket = this.connectToSocket();
        this.socket.on('connect_error', () => {
            this.isDisconnected$.next(true);
        });
    }

    private listenErrorMessage() {
        this.socket.on('error', (errorContent: string) => {
            this.error$.next(errorContent);
        });
    }

    private waitForOtherPlayers() {
        this.socket.on('pendingGameId', (pendingGameid: string) => {
            this.pendingGameId$.next(pendingGameid);
        });
        this.listenForUpdatedGameSettings();
        this.listenForHostQuit();
    }

    private listenForUpdatedGameSettings() {
        this.socket.on('gameJoined', (gameSettings: OnlineGameSettings) => {
            this.gameSettings$.next(gameSettings);
            const clientName = this.account.account?.name;
            if (this.gameSettings$.value === undefined) {
                this.error$.next(this.kickedErrorMsg);
                return;
            }
            if (clientName === undefined) {
                return;
            }
            if (this.gameSettings$.value.playerNames.includes(clientName)) {
                this.isWaiting$.next(false);
                return;
            }
            if (this.gameSettings$.value.tmpPlayerNames.includes(clientName)) {
                this.isWaiting$.next(true);
                return;
            }
            this.error$.next(this.kickedErrorMsg);
        });
    }

    private listenForGameStart() {
        this.socket.on('gameStarted', (gameSettings: OnlineGameSettings) => {
            this.gameStarted$.next(gameSettings);
            this.disconnectSocket();
        });
    }

    private connectToSocket() {
        return io(environment.serverSocketUrl, { path: '/newGame', withCredentials: true, transports: ['websocket'] });
    }
}
