import { GAME_TOKEN_PREFIX, NOT_FOUND } from '@app/constants';
import { DictionaryService } from '@app/game/game-logic/validator/dictionary/dictionary.service';
import { GameManagerService } from '@app/game/game-manager/game-manager.services';
import { ServerLogger } from '@app/logger/logger';
import { OnlineGameSettings, OnlineGameSettingsUI } from '@app/new-game/online-game.interface';
import { Subject } from 'rxjs';
import { Service } from 'typedi';
import { v4 as uuidv4 } from 'uuid';

@Service()
export class NewGameManagerService {
    static gameIdCounter: number = 0;
    pendingGames: Map<string, OnlineGameSettingsUI> = new Map<string, OnlineGameSettingsUI>();
    activeGameSettingMap = new Map<string, OnlineGameSettings>();
    refreshPendingGame$ = new Subject<void>();

    constructor(private gameMaster: GameManagerService, private dictionaryService: DictionaryService) {
        this.gameMaster.gameDeleted$.subscribe((gameToken) => {
            ServerLogger.logDebug(`Game observable ${gameToken} deleted`);
            if (gameToken) {
                this.activeGameSettingMap.delete(gameToken);
                this.refreshPendingGame$.next();
            }
        });
    }

    getPendingGames(): OnlineGameSettings[] {
        const games: OnlineGameSettings[] = [];
        this.pendingGames.forEach((game, id) => {
            games.push(this.toOnlineGameSettings(id, game));
        });
        return games;
    }

    getObservableGames(): OnlineGameSettings[] {
        const games: OnlineGameSettings[] = [];
        this.gameMaster.activeGames.forEach((game, gameToken) => {
            const gameSetting = this.activeGameSettingMap.get(gameToken);
            if (!gameSetting) {
                return;
            }
            if (!gameSetting.privateGame) {
                games.push(gameSetting);
            }
        });
        return games;
    }

    createPendingGame(gameSettings: OnlineGameSettingsUI): string {
        const gameId = this.generateId();
        this.pendingGames.set(gameId, gameSettings);
        return gameId;
    }

    async launchPendingGame(id: string, gameSettings?: OnlineGameSettingsUI): Promise<string> {
        if (!gameSettings) {
            gameSettings = this.pendingGames.get(id);
        }
        const onlineGameSettings = this.toOnlineGameSettings(id, gameSettings);
        const gameToken = this.generateGameToken();
        ServerLogger.logDebug(`New game created with token ${gameToken}`);
        this.activeGameSettingMap.set(gameToken, onlineGameSettings);
        await this.startGame(gameToken, onlineGameSettings);
        return gameToken;
    }

    joinPendingGame(id: string, name: string): string | undefined {
        if (!this.isPendingGame(id)) {
            return;
        }
        const gameSettings = this.pendingGames.get(id);
        if (!gameSettings) {
            return;
        }
        if (gameSettings.privateGame) {
            if (!gameSettings.tmpPlayerNames) {
                gameSettings.tmpPlayerNames = [];
            }
            gameSettings.tmpPlayerNames.push(name);
            return id;
        }
        gameSettings.playerNames.push(name);
        return id;
    }

    acceptPlayerInPrivatePendingGame(id: string, name: string): OnlineGameSettings | undefined {
        const gameSettings = this.pendingGames.get(id);
        if (!gameSettings) {
            return;
        }
        const index = gameSettings.tmpPlayerNames.findIndex((playerName) => playerName === name);
        if (index === NOT_FOUND) {
            return;
        }
        gameSettings.playerNames.push(name);
        gameSettings.tmpPlayerNames.splice(index, 1);
        const onlineGameSetting = this.toOnlineGameSettings(id, gameSettings);
        return onlineGameSetting;
    }

    removeTmpPlayer(id: string, name: string): OnlineGameSettings | undefined {
        const gameSettings = this.pendingGames.get(id);
        if (!gameSettings) {
            return;
        }
        const index = gameSettings.tmpPlayerNames.findIndex((playerName) => playerName === name);
        if (index === NOT_FOUND) {
            return;
        }
        gameSettings.tmpPlayerNames.splice(index, 1);
        const onlineGameSetting = this.toOnlineGameSettings(id, gameSettings);
        return onlineGameSetting;
    }

    quitPendingGame(id: string, nameToRemove: string): string | undefined {
        const gameSettings = this.pendingGames.get(id);
        if (!gameSettings) {
            return;
        }
        const index = gameSettings.playerNames.findIndex((name) => name === nameToRemove);
        gameSettings.playerNames.splice(index, 1);
        return id;
    }

    deletePendingGame(id: string) {
        const gameStarted = this.gameMaster.activeGames.has(id);
        if (!gameStarted) {
            this.dictionaryService.deleteGameDictionary(id);
        }
        this.pendingGames.delete(id);
    }

    getPendingGame(id: string): OnlineGameSettings {
        if (!this.pendingGames.get(id)) {
            throw Error('This game does not exist.');
        }
        const onlineGameSetting = this.toOnlineGameSettings(id, this.pendingGames.get(id));
        return onlineGameSetting;
    }

    private isPendingGame(id: string): boolean {
        return this.pendingGames.has(id);
    }

    private async startGame(gameToken: string, gameSettings: OnlineGameSettings): Promise<OnlineGameSettings> {
        const newGame = await this.gameMaster.createGame(gameToken, gameSettings);
        gameSettings.playerNames = newGame.players.map((player) => player.name);
        return gameSettings;
    }

    private generateId(): string {
        const gameId = NewGameManagerService.gameIdCounter.toString();
        NewGameManagerService.gameIdCounter = (NewGameManagerService.gameIdCounter + 1) % Number.MAX_SAFE_INTEGER;
        return gameId;
    }

    private generateGameToken(): string {
        const uuid = uuidv4();
        return `${GAME_TOKEN_PREFIX}${uuid}`;
    }

    private toOnlineGameSettings(id: string, settings: OnlineGameSettingsUI | undefined): OnlineGameSettings {
        const gameSettings = settings as OnlineGameSettings;
        gameSettings.id = id;
        return gameSettings;
    }
}
