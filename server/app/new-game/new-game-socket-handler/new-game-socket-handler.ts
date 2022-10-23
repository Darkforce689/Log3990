import { AuthService } from '@app/auth/services/auth.service';
import { SessionMiddlewareService } from '@app/auth/services/session-middleware.service';
import { Session } from '@app/auth/services/session.interface';
import { DEFAULT_DICTIONARY_TITLE } from '@app/game/game-logic/constants';
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
    // socketMap: Map<string, Map<string, Socket>>;

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
        // this.socketMap = new Map<string, Map<string, {socket: Socket, isHost: boolean>>();
    }

    newGameHandler(): void {
        const sessionMiddleware = this.sessionMiddleware.getSocketSessionMiddleware(true);
        this.ioServer.use(sessionMiddleware);
        this.ioServer.use(this.authService.socketAuthGuard);

        this.ioServer.on('connection', async (socket) => {
            let gameId: string;
            socket.emit(pendingGames, this.newGameManagerService.getPendingGames());

            socket.on(createGame, async (gameSettings: OnlineGameSettingsUI) => {
                try {
                    gameSettings.gameStatus = 'En attente';
                    const { userId: _id } = (socket.request as unknown as { session: Session }).session;
                    const user = await this.userService.getUser({ _id });
                    if (user === undefined) {
                        return;
                    }
                    gameId = this.createGame((gameSettings = { ...gameSettings, playerNames: [user.name] }), socket);
                    this.dictionaryService.makeGameDictionary(gameId, DEFAULT_DICTIONARY_TITLE);
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

            socket.on(joinGame, async (id: string, password: string) => {
                try {
                    const { userId: _id } = (socket.request as unknown as { session: Session }).session;
                    const user = await this.userService.getUser({ _id });
                    if (user === undefined) {
                        return;
                    }
                    const gameSettings = this.getPendingGame(id);
                    if (gameSettings.hasPassword && gameSettings.password !== password) {
                        throw Error('Mauvais mot de passe');
                    }
                    this.joinGame(id, user.name, gameSettings, socket);
                    this.emitPendingGamesToAll();
                } catch (error) {
                    ServerLogger.logError(error);
                    this.sendError(error, socket);
                }
            });

            socket.on(kickPlayer, (id, playerNbr) => {
                try {
                    this.kickPlayer(id, playerNbr);
                    this.emitPendingGamesToAll();
                } catch (error) {
                    ServerLogger.logError(error);
                    this.sendError(error, socket);
                }
            });

            socket.on(acceptPlayer, (id, playerNbr) => {
                try {
                    this.acceptPlayer(id, playerNbr);
                    this.emitPendingGamesToAll();
                } catch (error) {
                    ServerLogger.logError(error);
                    this.sendError(error, socket);
                }
            });

            socket.on(refusePlayer, (id, playerNbr) => {
                try {
                    this.refusePlayer(id, playerNbr);
                    this.emitPendingGamesToAll();
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

    // private addPlayerSocket(id: string, playerId: string, socket: Socket): void {
    //     if (!this.socketMap.has(id)) {
    //         this.socketMap.set(id, new Map<string, Socket>());
    //     }
    //     this.socketMap.get(id)?.set(playerId, socket);
    // }

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
        const gameToken = await this.newGameManagerService.launchPendingGame(id, gameSettings);
        this.sendGameStartedToPlayers(id, gameToken, gameSettings);
        if (gameSettings.privateGame) {
            this.deletePendingGame(id);
            return;
        }
        gameSettings.gameStatus = 'En cours';
        // this.emitPendingGamesToAll();
    }

    private joinGame(
        id: string,
        name: string,
        gameSettings: OnlineGameSettings,
        socket: Socket<DefaultEventsMap, DefaultEventsMap, DefaultEventsMap>,
    ) {
        if (typeof id !== 'string' || typeof name !== 'string') {
            throw Error('Impossible de rejoindre la partie, les paramètres sont invalides.');
        }
        const gameToken = this.newGameManagerService.joinPendingGame(id, name);
        if (!gameToken) {
            throw Error("Impossible de rejoindre la partie, elle n'existe pas.");
        }
        if (gameSettings.privateGame) {
            socket.emit('waitingRoom');
            socket.join(id + name);
        } else {
            socket.join(id);
        }
        this.sendGameSettingsToPlayers(gameToken, gameSettings);
    }

    private getPendingGame(id: string): OnlineGameSettings {
        return this.newGameManagerService.getPendingGame(id);
    }

    private deletePendingGame(id: string): void {
        this.newGameManagerService.deletePendingGame(id);
    }

    private async kickPlayer(id: string, playerNbr: number) {
        const allClients = await this.ioServer.in(id).fetchSockets();
        if (!allClients) {
            return;
        }
        allClients[playerNbr].emit('kicked');
        allClients[playerNbr].disconnect();
    }

    private async acceptPlayer(id: string, name: string) {
        const client = await this.ioServer.in(id + name).fetchSockets();
        if (!client) {
            return;
        }
        client[0].join(id);
        client[0].emit('accepted');
        client[0].leave(id + name);
        const gameSettings = this.newGameManagerService.acceptPlayerInPrivatePendingGame(id, name);
        if (!gameSettings) {
            throw Error("Impossible de rejoindre la partie, elle n'existe pas.");
        }
        this.sendGameSettingsToPlayers(id, gameSettings);
    }

    private async refusePlayer(id: string, name: string) {
        const client = await this.ioServer.in(id + name).fetchSockets();
        if (!client) {
            return;
        }
        client[0].emit('kicked');
        client[0].disconnect();
        const gameSettings = this.newGameManagerService.removeTmpPlayer(id, name);
        if (!gameSettings) {
            throw Error("Impossible de rejoindre la partie, elle n'existe pas.");
        }
        this.sendGameSettingsToPlayers(id, gameSettings);
    }

    private onDisconnect(name: string) {
        const gameToChange = this.newGameManagerService.getPendingGames().find((gameSettings) => gameSettings.playerNames.includes(name));

        if (!gameToChange) return;
        if (gameToChange.playerNames[0] === name) {
            this.ioServer.to(gameToChange.id).emit(hostQuit);
            if (gameToChange.gameStatus === 'En attente') {
                this.newGameManagerService.deletePendingGame(gameToChange.id);
            }
        }
        this.newGameManagerService.quitPendingGame(gameToChange.id, name);
        this.sendGameSettingsToPlayers(gameToChange.id, gameToChange);
    }

    private sendError(error: Error, socket: Socket<DefaultEventsMap, DefaultEventsMap, DefaultEventsMap>) {
        const errorMessage = error.message;
        socket.emit('error', errorMessage);
    }

    private sendGameSettingsToPlayers(gameToken: string, gameSettings: OnlineGameSettings) {
        gameSettings.id = gameToken;
        this.ioServer.to(gameToken).emit(gameJoined, gameSettings);
    }

    private sendGameStartedToPlayers(gameId: string, gameToken: string, gameSettings: OnlineGameSettings) {
        gameSettings.id = gameToken;
        this.ioServer.to(gameId).emit(gameStarted, gameSettings);
    }

    private emitPendingGamesToAll() {
        this.ioServer.emit(pendingGames, this.newGameManagerService.getPendingGames());
    }
}
