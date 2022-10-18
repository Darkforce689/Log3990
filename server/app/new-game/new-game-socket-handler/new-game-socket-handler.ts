import { DEFAULT_DICTIONARY_TITLE } from '@app/game/game-logic/constants';
import { isGameSettings } from '@app/game/game-logic/utils';
import { DictionaryService } from '@app/game/game-logic/validator/dictionary/dictionary.service';
import { ServerLogger } from '@app/logger/logger';
import { NewGameManagerService } from '@app/new-game/new-game-manager/new-game-manager.service';
import { OnlineGameSettings, OnlineGameSettingsUI } from '@app/new-game/online-game.interface';
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

export class NewGameSocketHandler {
    readonly ioServer: Server;

    constructor(server: http.Server, private newGameManagerService: NewGameManagerService, private dictionaryService: DictionaryService) {
        this.ioServer = new Server(server, {
            path: '/newGame',
            cors: { origin: '*', methods: ['GET', 'POST'] },
        });
    }

    newGameHandler(): void {
        this.ioServer.on('connection', (socket) => {
            let gameId: string;

            socket.emit(pendingGames, this.newGameManagerService.getPendingGames());

            socket.on(createGame, (gameSettings: OnlineGameSettingsUI) => {
                try {
                    gameId = this.createGame(gameSettings, socket);
                    this.dictionaryService.makeGameDictionary(gameId, DEFAULT_DICTIONARY_TITLE);
                    this.emitPendingGamesToAll();
                } catch (error) {
                    ServerLogger.logError(error);
                    this.sendError(error, socket);
                }
            });

            socket.on(launchGame, async (id: string) => {
                try {
                    await this.launchGame(id);
                    this.emitPendingGamesToAll();
                } catch (error) {
                    ServerLogger.logError(error);
                    this.sendError(error, socket);
                }
            });

            // TODO : REVERT/ACCEPT OTHER CHANGES (ONLY TEMPORARY WHILE WAITING FOR !54)
            socket.on(joinGame, (id: string, name?: string) => {
                try {
                    this.joinGame(id, name ?? 'helloFrom2015', this.getPendingGame(id), socket);
                    this.emitPendingGamesToAll();
                } catch (error) {
                    ServerLogger.logError(error);
                    this.sendError(error, socket);
                }
            });

            socket.on('disconnect', () => {
                this.onDisconnect(gameId);
                this.emitPendingGamesToAll();
            });
        });
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
        const gameToken = await this.newGameManagerService.launchPendingGame(id, gameSettings);
        this.sendGameStartedToPlayers(id, gameToken, gameSettings);
        this.deletePendingGame(id);
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
        socket.join(id);
        this.sendGameSettingsToPlayers(id, gameToken, gameSettings);
    }

    private getPendingGame(id: string): OnlineGameSettings {
        return this.newGameManagerService.getPendingGame(id);
    }

    private deletePendingGame(id: string): void {
        this.newGameManagerService.deletePendingGame(id);
    }

    private onDisconnect(gameId: string) {
        this.newGameManagerService.deletePendingGame(gameId);
    }

    private sendError(error: Error, socket: Socket<DefaultEventsMap, DefaultEventsMap, DefaultEventsMap>) {
        const errorMessage = error.message;
        socket.emit('error', errorMessage);
    }

    private sendGameSettingsToPlayers(gameId: string, gameToken: string, gameSettings: OnlineGameSettings) {
        gameSettings.id = gameToken;
        this.ioServer.to(gameId).emit(gameJoined, gameSettings);
    }

    private sendGameStartedToPlayers(gameId: string, gameToken: string, gameSettings: OnlineGameSettings) {
        gameSettings.id = gameToken;
        this.ioServer.to(gameId).emit(gameStarted, gameSettings);
    }

    private emitPendingGamesToAll() {
        this.ioServer.emit(pendingGames, this.newGameManagerService.getPendingGames());
    }
}
