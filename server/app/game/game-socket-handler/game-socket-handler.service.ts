import { AuthService } from '@app/auth/services/auth.service';
import { SessionMiddlewareService } from '@app/auth/services/session-middleware.service';
import { Session } from '@app/auth/services/session.interface';
import {
    ForfeitPlayerInfo,
    GameState,
    GameStateToken,
    PlayerInfoToken,
    SyncState,
    SyncStateToken,
} from '@app/game/game-logic/interface/game-state.interface';
import { TimerStartingTime, TimerTimeLeft } from '@app/game/game-logic/timer/timer-game-control.interface';
import { GameManagerService } from '@app/game/game-manager/game-manager.services';
import { OnlineAction } from '@app/game/online-action.interface';
import { ServerLogger } from '@app/logger/logger';
import { UserService } from '@app/user/services/user.service';
import * as http from 'http';
import * as io from 'socket.io';
import { DefaultEventsMap } from 'socket.io/dist/typed-events';
import { UserAuth } from './user-auth.interface';

export interface NameAndToken {
    name: string;
    gameToken: string;
}

export class GameSocketsHandler {
    readonly sio: io.Server;
    observerSocketMap = new Map<string, string>();
    constructor(
        server: http.Server,
        private gameManager: GameManagerService,
        private sessionMiddleware: SessionMiddlewareService,
        private authService: AuthService,
        private userService: UserService,
        private lastGameStateMap: Map<string, GameState> = new Map<string, GameState>(),
    ) {
        this.sio = new io.Server(server, {
            path: '/game',
            cors: { origin: '*', methods: ['GET', 'POST'] },
            pingTimeout: 5000,
        });

        this.gameManager.newGameState$.subscribe((gameStateToken: GameStateToken) => {
            const gameToken = gameStateToken.gameToken;
            const gameState = gameStateToken.gameState;
            this.emitGameState(gameState, gameToken);
        });

        this.gameManager.newSyncState$.subscribe((syncStateToken: SyncStateToken) => {
            const gameToken = syncStateToken.gameToken;
            const syncState = syncStateToken.syncState;
            this.emitSyncState(syncState, gameToken);
        });

        this.gameManager.timerStartingTime$.subscribe((timerGameControl: TimerStartingTime) => {
            const gameToken = timerGameControl.gameToken;
            const timerStartingTime = timerGameControl.initialTime;
            this.emitTimerStartingTime(timerStartingTime, gameToken);
        });

        this.gameManager.timeUpdate$.subscribe((timerTimeUpdate: TimerTimeLeft) => {
            const gameToken = timerTimeUpdate.gameToken;
            const timeLeft = timerTimeUpdate.timeLeft;
            this.emitTimeUpdate(timeLeft, gameToken);
        });

        this.gameManager.forfeitedGameState$.subscribe((forfeitedGameState: PlayerInfoToken) => {
            const gameToken = forfeitedGameState.gameToken;
            const playerInfo = forfeitedGameState.playerInfo;
            this.emitTransitionGameState(playerInfo, gameToken);
        });

        this.gameManager.gameDeleted$.subscribe((gameToken: string) => {
            const gameState = this.lastGameStateMap.get(gameToken);
            if (gameState) {
                gameState.isEndOfGame = true;
                this.emitGameState(gameState, gameToken);
            }
            this.lastGameStateMap.delete(gameToken);
        });
    }

    handleSockets() {
        const sessionMiddleware = this.sessionMiddleware.getSocketSessionMiddleware(true);
        this.sio.use(sessionMiddleware);
        this.sio.use(this.authService.socketAuthGuard);

        this.sio.on('connection', async (socket) => {
            socket.on('joinGame', async (gameToken: string) => {
                try {
                    const { userId: _id } = (socket.request as unknown as { session: Session }).session;
                    const user = await this.userService.getUser({ _id });
                    if (user === undefined) {
                        throw Error(`No user found with userId ${_id}`);
                    }
                    const playerName = user.name;
                    const userAuth: UserAuth = { playerName, gameToken };
                    socket.join(gameToken);
                    this.addPlayerToGame(socket.id, userAuth);
                    const lastGameState = this.lastGameStateMap.get(gameToken);
                    if (lastGameState) {
                        socket.emit('gameState', lastGameState);
                    }
                } catch (error) {
                    ServerLogger.logError(error);
                    socket.disconnect();
                }
            });

            socket.on('nextAction', (action: OnlineAction) => {
                try {
                    this.sendPlayerAction(socket.id, action);
                } catch (error) {
                    ServerLogger.logError(error);
                    socket.disconnect();
                }
            });

            socket.on('nextSync', (sync: SyncState) => {
                try {
                    this.sendSyncronisation(socket.id, sync);
                } catch (error) {
                    ServerLogger.logError(error);
                    socket.disconnect();
                }
            });

            socket.on('disconnect', () => {
                this.removeObserver(socket);
                this.removePlayer(socket.id);
            });
        });
    }

    private emitTimerStartingTime(timerStartingTime: number, gameToken: string) {
        this.sio.to(gameToken).emit('timerStartingTime', timerStartingTime);
    }

    private emitTimeUpdate(timeLeft: number, gameToken: string) {
        this.sio.to(gameToken).emit('timeUpdate', timeLeft);
    }

    private emitGameState(gameState: GameState, gameToken: string) {
        this.lastGameStateMap.set(gameToken, gameState);
        if (gameState.isEndOfGame) {
            this.lastGameStateMap.delete(gameToken);
        }
        this.sio.to(gameToken).emit('gameState', gameState);
    }

    private emitSyncState(syncState: SyncState, gameToken: string) {
        this.sio.to(gameToken).emit('syncState', syncState);
    }

    private emitTransitionGameState(playerInfo: ForfeitPlayerInfo, gameToken: string) {
        this.sio.to(gameToken).emit('transitionGameState', playerInfo);
    }

    private addPlayerToGame(socketId: string, userAuth: UserAuth) {
        const playerId = socketId;
        const gameToken = userAuth.gameToken;
        this.observerSocketMap.set(playerId, gameToken);
        this.gameManager.addPlayerToGame(playerId, userAuth);
    }

    private sendPlayerAction(socketId: string, action: OnlineAction) {
        const playerId = socketId;
        this.gameManager.receivePlayerAction(playerId, action);
    }

    private sendSyncronisation(socketId: string, sync: SyncState) {
        const playerId = socketId;
        this.gameManager.receiveSync(playerId, sync);
    }

    private removePlayer(playerId: string) {
        this.gameManager.removePlayerFromGame(playerId);
    }

    private async removeObserver(socket: io.Socket<DefaultEventsMap, DefaultEventsMap, DefaultEventsMap>) {
        const { userId: _id } = (socket.request as unknown as { session: Session }).session;
        const user = await this.userService.getUser({ _id });
        if (user === undefined) {
            throw Error(`No user found with userId ${_id}`);
        }
        const name = user.name;
        const gameToken = this.observerSocketMap.get(socket.id);
        if (gameToken) {
            const nameAndToken: NameAndToken = { name, gameToken };
            this.gameManager.observerLeft$.next(nameAndToken);
        }
        this.observerSocketMap.delete(socket.id);
    }
}
