import { GAME_TOKEN_PREFIX, NOT_FOUND } from '@app/constants';
import { ACTIVE_STATUS } from '@app/game/game-logic/constants';
import { DictionaryService } from '@app/game/game-logic/validator/dictionary/dictionary.service';
import { GameManagerService } from '@app/game/game-manager/game-manager.services';
import { OnlineGameSettings, OnlineGameSettingsUI } from '@app/new-game/online-game.interface';
import { Service } from 'typedi';
import { v4 as uuidv4 } from 'uuid';

@Service()
export class NewGameManagerService {
    static gameIdCounter: number = 0;
    pendingGames: Map<string, OnlineGameSettingsUI> = new Map<string, OnlineGameSettingsUI>();
    activeGameIdMap = new Map<string, string>();

    constructor(private gameMaster: GameManagerService, private dictionaryService: DictionaryService) {}

    getPendingGames(): OnlineGameSettings[] {
        const games: OnlineGameSettings[] = [];
        const activeGames: Set<string> = new Set<string>();
        this.gameMaster.activeGames.forEach((game, gameToken) => {
            activeGames.add(gameToken);
        });
        this.pendingGames.forEach((game, id) => {
            const activeToken = this.activeGameIdMap.get(id);
            if (activeToken === undefined) {
                games.push(this.toOnlineGameSettings(id, game));
                return;
            }
            if (game.gameStatus === ACTIVE_STATUS && !activeGames.has(activeToken)) {
                this.deletePendingGame(id);
                this.activeGameIdMap.delete(id);
                return;
            }
            games.push(this.toOnlineGameSettings(id, game));
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
        const onlineGameSettingsUI = this.toOnlineGameSettings(id, gameSettings);
        const gameToken = this.generateGameToken();
        this.activeGameIdMap.set(id, gameToken);
        await this.startGame(gameToken, this.toOnlineGameSettings(id, onlineGameSettingsUI));
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
