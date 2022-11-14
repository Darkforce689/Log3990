/* eslint-disable max-lines */
import { AuthService } from '@app/auth/services/auth.service';
import { SessionMiddlewareService } from '@app/auth/services/session-middleware.service';
import { Session } from '@app/auth/services/session.interface';
import { ACTIVE_STATUS, DEFAULT_DICTIONARY_TITLE, WAIT_STATUS } from '@app/game/game-logic/constants';
import { isGameSettings } from '@app/game/game-logic/utils';
import { DictionaryService } from '@app/game/game-logic/validator/dictionary/dictionary.service';
import { ServerLogger } from '@app/logger/logger';
import { NewGameManagerService } from '@app/new-game/new-game-manager/new-game-manager.service';
import { OnlineGameSettings, OnlineGameSettingsUI } from '@app/new-game/online-game.interface';
import { UserService } from '@app/user/user.service';
import * as http from 'http';
import { Server, Socket } from 'socket.io';
import { DefaultEventsMap } from 'socket.io/dist/typed-events';

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

export class NewGameSocketHandler {
    readonly ioServer: Server;
    socketMap: Map<string, Map<string, { socketId: string; isHost: boolean }>> = new Map();

    constructor(
        server: http.Server,
        private newGameManagerService: NewGameManagerService,
        private dictionaryService: DictionaryService,
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
            let gameId: string;
            socket.emit(pendingGames, {
                pendingGamesSettings: this.newGameManagerService.getPendingGames(),
                observableGamesSettings: this.newGameManagerService.getObservableGames(),
            });

            socket.on(createGame, async (gameSettings: OnlineGameSettingsUI) => {
                try {
                    const { userId: _id } = (socket.request as unknown as { session: Session }).session;
                    const user = await this.userService.getUser({ _id });
                    if (user === undefined) {
                        return;
                    }
                    gameSettings.gameStatus = WAIT_STATUS;
                    gameId = this.createGame((gameSettings = { ...gameSettings, playerNames: [user.name] }), socket);
                    this.dictionaryService.makeGameDictionary(gameId, DEFAULT_DICTIONARY_TITLE);
                    this.addToSocketMap(gameId, user.name, socket, true);
                    this.emitPendingGamesToAll();
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
                    const id = joinGameParams.id;
                    const password = joinGameParams.password;
                    const { userId: _id } = (socket.request as unknown as { session: Session }).session;
                    const user = await this.userService.getUser({ _id });
                    if (user === undefined) {
                        return;
                    }
                    if (typeof id !== 'string' || typeof user.name !== 'string') {
                        throw Error('Impossible de rejoindre la partie, les paramètres sont invalides.');
                    }
                    const gameSettings = this.getPendingGame(id);
                    if (!gameSettings) {
                        throw Error("Impossible de rejoindre la partie, elle n'existe pas.");
                    }
                    if (gameSettings.password !== undefined && gameSettings.password !== password) {
                        throw Error('Mauvais mot de passe');
                    }
                    if (gameSettings.gameStatus === WAIT_STATUS) {
                        this.addToSocketMap(id, user.name, socket, false);
                        this.joinGame(id, user.name, gameSettings, socket);
                        this.emitPendingGamesToAll();
                        return;
                    }
                    this.joinGameAsObserver(id, user.name, socket);
                    this.emitPendingGamesToAll();
                } catch (error) {
                    ServerLogger.logError(error);
                    this.sendError(error, socket);
                }
            });

            socket.on(kickPlayer, async (id, playerName) => {
                try {
                    const { userId: _id } = (socket.request as unknown as { session: Session }).session;
                    const user = await this.userService.getUser({ _id });
                    if (user === undefined) {
                        return;
                    }
                    if (this.socketMap.get(id)?.get(user.name)?.isHost) {
                        this.kickPlayer(id, playerName);
                        this.emitPendingGamesToAll();
                    }
                } catch (error) {
                    ServerLogger.logError(error);
                    this.sendError(error, socket);
                }
            });

            socket.on(acceptPlayer, async (id, playerNbr) => {
                try {
                    const { userId: _id } = (socket.request as unknown as { session: Session }).session;
                    const user = await this.userService.getUser({ _id });
                    if (user === undefined) {
                        return;
                    }
                    if (this.socketMap.get(id)?.get(user.name)?.isHost) {
                        this.acceptPlayer(id, playerNbr);
                        this.emitPendingGamesToAll();
                    }
                } catch (error) {
                    ServerLogger.logError(error);
                    this.sendError(error, socket);
                }
            });

            socket.on(refusePlayer, async (id, playerNbr) => {
                try {
                    const { userId: _id } = (socket.request as unknown as { session: Session }).session;
                    const user = await this.userService.getUser({ _id });
                    if (user === undefined) {
                        return;
                    }
                    if (this.socketMap.get(id)?.get(user.name)?.isHost) {
                        this.refusePlayer(id, playerNbr);
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
                this.onDisconnect(user.name);
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

    private createGame(gameSettings: OnlineGameSettingsUI, socket: Socket<DefaultEventsMap, DefaultEventsMap, DefaultEventsMap>): string {
        if (!isGameSettings(gameSettings)) {
            throw Error('Impossible de rejoindre la partie, les paramètres de partie sont invalides.');
        }
        const gameId = this.createGameInternal(gameSettings, socket);
        return gameId;
    }

    private createGameInternal(gameSettings: OnlineGameSettingsUI, socket: Socket<DefaultEventsMap, DefaultEventsMap, DefaultEventsMap>): string {
        const gameId = this.newGameManagerService.createPendingGame(gameSettings);
        socket.emit(pendingGameId, gameId);
        socket.join(gameId);
        return gameId;
    }

    private async launchGame(id: string) {
        const gameSettings = this.getPendingGame(id);
        if (!gameSettings) {
            return;
        }
        gameSettings.gameStatus = ACTIVE_STATUS;
        const gameToken = await this.newGameManagerService.launchPendingGame(id, gameSettings);
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
        const gameToken = this.newGameManagerService.joinPendingGame(id, name);
        if (!gameToken) {
            throw Error("Impossible de rejoindre la partie, elle n'existe pas.");
        }
        socket.join(id);
        this.sendGameSettingsToPlayers(gameToken, gameSettings);
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

    private getPendingGame(id: string): OnlineGameSettings | undefined {
        const gameSettings = this.newGameManagerService.getPendingGame(id);
        if (!gameSettings) {
            return;
        }
        return gameSettings;
    }

    private deletePendingGame(id: string): void {
        this.newGameManagerService.deletePendingGame(id);
    }

    private kickPlayer(id: string, playerName: string) {
        const client = this.socketMap.get(id)?.get(playerName)?.socketId;
        if (!client) {
            return;
        }
        this.onDisconnect(playerName);
        const gameSettings = this.getPendingGame(id);
        this.ioServer.to(client).emit(gameJoined, gameSettings);
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
        this.ioServer.to(client).disconnectSockets();
    }

    private onDisconnect(name: string) {
        const gameToChange = this.newGameManagerService
            .getPendingGames()
            .find((gameSettings) => gameSettings.playerNames.includes(name) || gameSettings.tmpPlayerNames.includes(name));

        if (!gameToChange) {
            return;
        }
        if (gameToChange.playerNames[0] === name) {
            if (gameToChange.gameStatus === WAIT_STATUS) {
                this.ioServer.to(gameToChange.id).emit(hostQuit);
                this.newGameManagerService.deletePendingGame(gameToChange.id);
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
        this.newGameManagerService.activeGameSettingMap.set(gameId, gameSettings);
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
