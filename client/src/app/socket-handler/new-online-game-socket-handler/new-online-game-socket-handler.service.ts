import { Injectable } from '@angular/core';
import { isGameSettings } from '@app/game-logic/utils';
import { OnlineGameSettings, OnlineGameSettingsUI } from '@app/socket-handler/interfaces/game-settings-multi.interface';
import { BehaviorSubject, Subject } from 'rxjs';
import { io, Socket } from 'socket.io-client';
import { environment } from 'src/environments/environment';

@Injectable({
    providedIn: 'root',
})
export class NewOnlineGameSocketHandler {
    pendingGameId$ = new BehaviorSubject<string | undefined>(undefined);
    pendingGames$ = new BehaviorSubject<OnlineGameSettings[]>([]);
    gameSettings$ = new BehaviorSubject<OnlineGameSettings | undefined>(undefined);
    gameStarted$ = new BehaviorSubject<OnlineGameSettings | undefined>(undefined);
    isGameOwner: boolean = false;
    isDisconnected$ = new Subject<boolean>();
    error$ = new Subject<string>();
    socket: Socket;

    resetGameToken() {
        this.gameStarted$.next(undefined);
        this.isGameOwner = false;
    }

    createGame(gameSettings: OnlineGameSettingsUI) {
        this.connect();
        if (!isGameSettings(gameSettings)) {
            throw Error('Games Settings are not valid. Cannot create a game.');
        }
        this.socket.emit('createGame', gameSettings);
        this.isGameOwner = true;
        this.waitForOtherPlayers();
    }

    listenForPendingGames() {
        this.connect();
        this.socket.on('pendingGames', (pendingGames: OnlineGameSettings[]) => {
            this.pendingGames$.next(pendingGames);
        });
    }

    joinPendingGame(id: string, playerName: string) {
        if (!this.socket.connected) {
            throw Error("Can't join game, not connected to server");
        }
        this.socket.emit('joinGame', id, playerName);
        this.listenForUpdatedGameSettings();
        this.listenErrorMessage();
        this.listenForGameStart();
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

    disconnectSocket() {
        if (!this.socket) {
            return;
        }
        this.socket.disconnect();
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
    }

    private listenForUpdatedGameSettings() {
        this.socket.on('gameJoined', (gameSettings: OnlineGameSettings) => {
            this.gameSettings$.next(gameSettings);
            // this.disconnectSocket();
        });
    }

    private listenForGameStart() {
        this.socket.on('gameStarted', (gameSettings: OnlineGameSettings) => {
            this.gameStarted$.next(gameSettings);
            this.disconnectSocket();
        });
    }

    private connectToSocket() {
        return io(environment.serverSocketUrl, { path: '/newGame' });
    }
}
