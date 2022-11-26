/* eslint-disable max-lines */
import { AuthService } from '@app/auth/services/auth.service';
import { SessionMiddlewareService } from '@app/auth/services/session-middleware.service';
import { Session } from '@app/auth/services/session.interface';
import { ACTIVE_STATUS, WAIT_STATUS } from '@app/game/game-logic/constants';
import { isGameSettings } from '@app/game/game-logic/utils';
import { ServerLogger } from '@app/logger/logger';
import { NewGameManagerService } from '@app/new-game/new-game-manager/new-game-manager.service';
import { OnlineGameSettings, OnlineGameSettingsUI } from '@app/new-game/online-game.interface';
import { UserService } from '@app/user/services/user.service';
import * as http from 'http';
import { Server, Socket } from 'socket.io';
import { DefaultEventsMap } from 'socket.io/dist/typed-events';

export enum JoinGameError {
    InexistantGame = 'INEXISTANT_GAME',
    InvalidPassword = 'INVALID_PASSWORD',
    NotEnoughPlace = 'PENDING_GAME_FULL',
}

export interface PrivateGameEvent {
    gameId: string;
    playerName: string;
}

type KickPlayer = PrivateGameEvent;
type AcceptPlayer = PrivateGameEvent;
type RefusePlayer = PrivateGameEvent;

const pendingGames = 'pendingGames';
const createGame = 'createGame';
const joinGame = 'joinGame';
const launchGame = 'launchGame';
const gameJoined = 'gameJoined';
const gameStarted = 'gameStarted';
const pendingGameId = 'pendingGameId';
const hostQuit = 'hostQuit';
const kickPlayer = 'kickPlayer';
const acceptPlayer = 'acceptPlayer';
const refusePlayer = 'refusePlayer';
const playerRefused = 'playerRefused';
const playerKicked = 'playerKicked';
const confirmPassword = 'confirmPassword';

export class NewGameSocketHandler {
    readonly ioServer: Server;
    socketMap: Map<string, Map<string, { socketId: string; isHost: boolean }>> = new Map();

    constructor(
        server: http.Server,
        private newGameManagerService: NewGameManagerService,
        private sessionMiddleware: SessionMiddlewareService,
        private authService: AuthService,
        private userService: UserService,
    ) {
        this.ioServer = new Server(server, {
            path: '/newGame',
            cors: { origin: '*', methods: ['GET', 'POST'] },
        });
        this.newGameManagerService.refreshPendingGame$.subscribe(() => {
            this.emitPendingGamesToAll();
        });
    }

    newGameHandler(): void {
        const sessionMiddleware = this.sessionMiddleware.getSocketSessionMiddleware(true);
        this.ioServer.use(sessionMiddleware);
        this.ioServer.use(this.authService.socketAuthGuard);

        this.ioServer.on('connection', async (socket) => {
            socket.emit(pendingGames, {
                pendingGamesSettings: this.newGameManagerService.getPendingGames(),
                observableGamesSettings: this.newGameManagerService.getObservableGames(),
            });

            socket.on(createGame, async (gameSettingsUI: OnlineGameSettingsUI) => {
                try {
                    const { userId: _id } = (socket.request as unknown as { session: Session }).session;
                    const user = await this.userService.getUser({ _id });
                    if (user === undefined) {
                        return;
                    }
                    const gameId = await this.createGame({ ...gameSettingsUI, playerNames: [user.name] }, socket);
                    this.addToSocketMap(gameId, user.name, socket, true);
                    this.emitPendingGamesToAll();
                    const gameSettings = this.newGameManagerService.getPendingGame(gameId);
                    this.sendGameSettingsToPlayers(gameId, gameSettings as OnlineGameSettings);
                } catch (error) {
                    ServerLogger.logError(error);
                    this.sendError(error, socket);
                }
            });

            socket.on(launchGame, (id: string) => {
                try {
                    this.launchGame(id);
                    this.emitPendingGamesToAll();
                } catch (error) {
                    ServerLogger.logError(error);
                    this.sendError(error, socket);
                }
            });

            socket.on(joinGame, async (joinGameParams) => {
                try {
                    const { id, password } = joinGameParams;
                    const { userId: _id } = (socket.request as unknown as { session: Session }).session;
                    const user = await this.userService.getUser({ _id });
                    if (user === undefined) {
                        return;
                    }
                    if (typeof id !== 'string') {
                        throw Error('Impossible de rejoindre la partie, les paramètres sont invalides.');
                    }
                    const gameSettings = this.getGame(id);
                    if (!gameSettings) {
                        this.sendError(Error(JoinGameError.InexistantGame), socket);
                        return;
                    }

                    const canJoin = this.canJoin(gameSettings, password);
                    this.sendPasswordConfirmation(canJoin, socket);
                    if (!canJoin) {
                        return;
                    }

                    if (gameSettings.gameStatus !== WAIT_STATUS) {
                        this.joinGameAsObserver(id, user.name, socket);
                        this.emitPendingGamesToAll();
                        return;
                    }

                    const isEnoughPlaceToJoin = this.newGameManagerService.isEnoughPlaceToJoin(id);
                    if (!isEnoughPlaceToJoin) {
                        this.sendError(Error(JoinGameError.NotEnoughPlace), socket);
                        return;
                    }
                    this.joinGame(id, user.name, gameSettings, socket);
                    this.addToSocketMap(id, user.name, socket, false);
                    this.emitPendingGamesToAll();
                } catch (error) {
                    ServerLogger.logError(error);
                    this.sendError(error, socket);
                }
            });

            socket.on(kickPlayer, async ({ gameId, playerName }: KickPlayer) => {
                try {
                    const { userId: _id } = (socket.request as unknown as { session: Session }).session;
                    const user = await this.userService.getUser({ _id });
                    if (user === undefined) {
                        return;
                    }
                    if (this.socketMap.get(gameId)?.get(user.name)?.isHost) {
                        this.kickPlayer(gameId, playerName);
                        this.emitPendingGamesToAll();
                    }
                } catch (error) {
                    ServerLogger.logError(error);
                    this.sendError(error, socket);
                }
            });

            socket.on(acceptPlayer, async ({ gameId, playerName }: AcceptPlayer) => {
                try {
                    const { userId: _id } = (socket.request as unknown as { session: Session }).session;
                    const user = await this.userService.getUser({ _id });
                    if (user === undefined) {
                        return;
                    }
                    if (this.socketMap.get(gameId)?.get(user.name)?.isHost) {
                        this.acceptPlayer(gameId, playerName);
                        this.emitPendingGamesToAll();
                    }
                } catch (error) {
                    ServerLogger.logError(error);
                    this.sendError(error, socket);
                }
            });

            socket.on(refusePlayer, async ({ gameId, playerName }: RefusePlayer) => {
                try {
                    const { userId: _id } = (socket.request as unknown as { session: Session }).session;
                    const user = await this.userService.getUser({ _id });
                    if (user === undefined) {
                        return;
                    }
                    if (this.socketMap.get(gameId)?.get(user.name)?.isHost) {
                        this.refusePlayer(gameId, playerName);
                        this.emitPendingGamesToAll();
                    }
                } catch (error) {
                    ServerLogger.logError(error);
                    this.sendError(error, socket);
                }
            });

            socket.on('disconnect', async () => {
                const { userId: _id } = (socket.request as unknown as { session: Session }).session;
                const user = await this.userService.getUser({ _id });
                if (user === undefined) {
                    return;
                }
                this.removePlayerFromGame(user.name);
                this.emitPendingGamesToAll();
            });
        });
    }

    private addToSocketMap(gameId: string, playerName: string, socket: Socket, isHost: boolean): void {
        if (gameId === undefined || playerName === undefined) {
            return;
        }
        if (!this.socketMap.has(gameId)) {
            this.socketMap.set(gameId, new Map());
        }
        const gameMap = this.socketMap.get(gameId);
        if (!gameMap) {
            return;
        }
        if (!gameMap.has(playerName)) {
            gameMap.set(playerName, { socketId: socket.id, isHost });
        }
    }

    private removeFromSocketMap(gameId: string, playerName: string): void {
        const gameMap = this.socketMap.get(gameId);
        if (!gameMap) {
            return;
        }
        gameMap.delete(playerName);
    }

    private deleteGameSocketMap(gameId: string): void {
        this.socketMap.delete(gameId);
    }

    private async createGame(
        gameSettings: OnlineGameSettingsUI,
        socket: Socket<DefaultEventsMap, DefaultEventsMap, DefaultEventsMap>,
    ): Promise<string> {
        if (!isGameSettings(gameSettings)) {
            throw Error('Impossible de rejoindre la partie, les paramètres de partie sont invalides.');
        }
        const gameId = await this.newGameManagerService.createPendingGame(gameSettings);
        socket.emit(pendingGameId, gameId);
        socket.join(gameId);
        return gameId;
    }

    private launchGame(id: string) {
        const gameSettings = this.getPendingGame(id);
        if (!gameSettings) {
            return;
        }
        gameSettings.gameStatus = ACTIVE_STATUS;
        const gameToken = this.newGameManagerService.launchPendingGame(id, gameSettings);
        this.sendGameStartedToPlayers(id, gameToken, gameSettings);
        this.deleteGameSocketMap(id);
        this.deletePendingGame(id);
        this.emitPendingGamesToAll();
    }

    private joinGame(
        id: string,
        name: string,
        gameSettings: OnlineGameSettings,
        socket: Socket<DefaultEventsMap, DefaultEventsMap, DefaultEventsMap>,
    ) {
        this.removePlayerFromGame(name);
        const gameId = this.newGameManagerService.joinPendingGame(id, name);
        if (!gameId) {
            this.sendError(Error(JoinGameError.InexistantGame), socket);
            return;
        }
        socket.join(id);
        this.sendGameSettingsToPlayers(gameId, gameSettings);
    }

    private canJoin(gameSettings: OnlineGameSettings, password: string): boolean {
        return gameSettings.password === undefined || gameSettings.password === password;
    }

    private sendPasswordConfirmation(canJoin: boolean, socket: Socket<DefaultEventsMap, DefaultEventsMap, DefaultEventsMap>) {
        socket.emit(confirmPassword, canJoin);
    }

    private joinGameAsObserver(gameToken: string, name: string, socket: Socket<DefaultEventsMap, DefaultEventsMap, DefaultEventsMap>) {
        const gameSettings = this.newGameManagerService.activeGameSettingMap.get(gameToken);
        if (!gameSettings) {
            return;
        }
        if (gameSettings.observerNames === undefined) {
            gameSettings.observerNames = [];
        }
        gameSettings.observerNames.push(name);
        socket.emit(gameStarted, gameSettings);
        this.emitPendingGamesToAll();
    }

    private getGame(id: string): OnlineGameSettings | undefined {
        if (this.newGameManagerService.isObservableGame(id)) {
            return this.getObservableGame(id);
        }
        return this.getPendingGame(id);
    }

    private getObservableGame(id: string): OnlineGameSettings | undefined {
        return this.newGameManagerService.getObservableGame(id);
    }

    private getPendingGame(id: string): OnlineGameSettings | undefined {
        return this.newGameManagerService.getPendingGame(id);
    }

    private deletePendingGame(id: string): void {
        this.newGameManagerService.deletePendingGame(id);
    }

    private kickPlayer(id: string, playerName: string) {
        const client = this.socketMap.get(id)?.get(playerName)?.socketId;
        if (!client) {
            return;
        }
        this.removePlayerFromGame(playerName);
        this.ioServer.to(client).emit(playerKicked);
        this.ioServer.to(client).disconnectSockets();
    }

    private async acceptPlayer(id: string, name: string) {
        const gameSettings = this.newGameManagerService.acceptPlayerInPrivatePendingGame(id, name);
        if (!gameSettings) {
            throw Error("Impossible de rejoindre la partie, elle n'existe pas.");
        }
        this.sendGameSettingsToPlayers(id, gameSettings);
    }

    private async refusePlayer(id: string, name: string) {
        const client = this.socketMap.get(id)?.get(name)?.socketId;
        if (!client) {
            return;
        }
        const gameSettings = this.newGameManagerService.removeTmpPlayer(id, name);
        if (!gameSettings) {
            throw Error("Impossible de rejoindre la partie, elle n'existe pas.");
        }
        this.sendGameSettingsToPlayers(id, gameSettings);
        this.ioServer.to(client).emit(playerRefused);
        this.ioServer.to(client).disconnectSockets();
    }

    private removePlayerFromGame(name: string) {
        const gameToChange = this.newGameManagerService
            .getPendingGames()
            .find((gameSettings) => gameSettings.playerNames.includes(name) || gameSettings.tmpPlayerNames.includes(name));

        if (!gameToChange) {
            return;
        }
        if (gameToChange.playerNames[0] === name) {
            if (gameToChange.gameStatus === WAIT_STATUS) {
                this.deletePendingGame(gameToChange.id);
                this.newGameManagerService.deleteGameConvo(gameToChange.id);
                this.ioServer.to(gameToChange.id).emit(hostQuit);
                this.deleteGameSocketMap(gameToChange.id);
            }
        }
        this.newGameManagerService.quitPendingGame(gameToChange.id, name);
        this.removeFromSocketMap(gameToChange.id, name);
        this.sendGameSettingsToPlayers(gameToChange.id, gameToChange);
    }

    private sendError(error: Error, socket: Socket<DefaultEventsMap, DefaultEventsMap, DefaultEventsMap>) {
        const errorMessage = error.message;
        socket.emit('error', errorMessage);
    }

    private sendGameSettingsToPlayers(gameId: string, gameSettings: OnlineGameSettings) {
        gameSettings.id = gameId;
        this.ioServer.to(gameId).emit(gameJoined, gameSettings);
    }

    private sendGameStartedToPlayers(gameId: string, gameToken: string, gameSettings: OnlineGameSettings) {
        gameSettings.id = gameToken;
        this.ioServer.to(gameId).emit(gameStarted, gameSettings);
    }

    private emitPendingGamesToAll() {
        const pendingAndObservableGames = {
            pendingGamesSettings: this.newGameManagerService.getPendingGames(),
            observableGamesSettings: this.newGameManagerService.getObservableGames(),
        };
        this.ioServer.emit(pendingGames, pendingAndObservableGames);
    }
}
