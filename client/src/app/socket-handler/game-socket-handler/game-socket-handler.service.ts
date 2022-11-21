import { Injectable } from '@angular/core';
import { GameState, PlayerInfoForfeit, SyncState } from '@app/game-logic/game/games/online-game/game-state';
import { OnlineAction } from '@app/socket-handler/interfaces/online-action.interface';
import { Observable, Subject } from 'rxjs';
import { io, Socket } from 'socket.io-client';
import { environment } from 'src/environments/environment';
export interface GameAuth {
    playerName: string;
    gameToken: string;
}

const HAVE_NOT_JOINED_GAME_ERROR = 'You havent join a game';
const SERVER_OFFLINE_ERROR = 'The game server is offline';
const GAME_ALREADY_JOINED = 'You have already joined a game';

@Injectable({
    providedIn: 'root',
})
export class GameSocketHandlerService {
    socket: Socket;

    private forfeitGameStateSubject = new Subject<PlayerInfoForfeit>();
    get forfeitGameState$(): Subject<PlayerInfoForfeit> {
        return this.forfeitGameStateSubject;
    }

    private gameStateSubject = new Subject<GameState>();
    get gameState$(): Observable<GameState> {
        return this.gameStateSubject;
    }

    private syncStateSubject = new Subject<SyncState>();
    get syncState$(): Observable<SyncState> {
        return this.syncStateSubject;
    }

    private timerStartingTimeSubject = new Subject<number>();
    get timerStartingTimes$(): Observable<number> {
        return this.timerStartingTimeSubject;
    }

    private timerTimeSubject = new Subject<number>();
    get timerTimeLeft$(): Observable<number> {
        return this.timerTimeSubject;
    }

    private disconnectedFromServerSubject = new Subject<void>();
    get disconnectedFromServer$(): Observable<void> {
        return this.disconnectedFromServerSubject;
    }

    joinGame(gameToken: string) {
        if (this.socket) {
            throw Error(GAME_ALREADY_JOINED);
        }
        this.socket = this.connectToSocket();
        this.socket.emit('joinGame', gameToken);
        this.socket.on('gameState', (gameState: GameState) => {
            this.receiveGameState(gameState);
        });

        this.socket.on('syncState', (syncState: SyncState) => {
            this.receiveSyncState(syncState);
        });

        this.socket.on('timerStartingTime', (timerStartingTime: number) => {
            this.receiveTimerStartingTime(timerStartingTime);
        });

        this.socket.on('timeUpdate', (timeLeft: number) => {
            this.receiveTimerUpdate(timeLeft);
        });

        this.socket.on('connect_error', () => {
            this.disconnectedFromServerSubject.next();
        });

        this.socket.on('disconnected', () => {
            this.disconnectedFromServerSubject.next();
        });

        this.socket.on('transitionGameState', (lastGameState: PlayerInfoForfeit) => {
            this.receiveForfeitedGameState(lastGameState);
        });
    }

    playAction(action: OnlineAction) {
        if (!this.socket) {
            throw Error(HAVE_NOT_JOINED_GAME_ERROR);
        }

        if (this.socket.disconnected) {
            throw Error(SERVER_OFFLINE_ERROR);
        }
        this.socket.emit('nextAction', action);
    }

    sendSync(sync: SyncState) {
        if (!this.socket) {
            throw Error(HAVE_NOT_JOINED_GAME_ERROR);
        }

        if (this.socket.disconnected) {
            throw Error(SERVER_OFFLINE_ERROR);
        }
        this.socket.emit('nextSync', sync);
    }

    disconnect() {
        if (!this.socket) {
            throw Error(HAVE_NOT_JOINED_GAME_ERROR);
        }
        this.socket.disconnect();
        this.socket = undefined as unknown as Socket;
    }

    private connectToSocket() {
        return io(environment.serverSocketUrl, { path: '/game', withCredentials: true, transports: ['websocket'] });
    }

    private receiveGameState(gameState: GameState) {
        this.gameStateSubject.next(gameState);
    }

    private receiveSyncState(syncState: SyncState) {
        this.syncStateSubject.next(syncState);
    }

    private receiveTimerStartingTime(timerStartingTime: number) {
        this.timerStartingTimeSubject.next(timerStartingTime);
    }

    private receiveTimerUpdate(timeLeft: number) {
        this.timerTimeSubject.next(timeLeft);
    }

    private receiveForfeitedGameState(forfeitedGameState: PlayerInfoForfeit) {
        this.forfeitGameState$.next(forfeitedGameState);
    }
}
