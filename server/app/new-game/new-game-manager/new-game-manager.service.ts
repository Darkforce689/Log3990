import { GAME_TOKEN_PREFIX, NOT_FOUND } from '@app/constants';
import { GameManagerService, PlayersAndToken } from '@app/game/game-manager/game-manager.services';
import { NameAndToken } from '@app/game/game-socket-handler/game-socket-handler.service';
import { ConversationService } from '@app/messages-service/services/conversation.service';
import { OnlineGameSettings, OnlineGameSettingsUI } from '@app/new-game/online-game.interface';
import { Subject } from 'rxjs';
import { Service } from 'typedi';
import { v4 as uuidv4 } from 'uuid';

@Service()
export class NewGameManagerService {
    pendingGames: Map<string, OnlineGameSettingsUI> = new Map<string, OnlineGameSettingsUI>();
    activeGameSettingMap = new Map<string, OnlineGameSettings>();
    refreshPendingGame$ = new Subject<void>();

    constructor(private gameMaster: GameManagerService, private conversationService: ConversationService) {
        this.gameMaster.gameDeleted$.subscribe((gameToken) => {
            if (gameToken) {
                this.activeGameSettingMap.delete(gameToken);
                this.refreshPendingGame$.next();
            }
        });
        this.gameMaster.observerLeft$.subscribe((nameAndToken: NameAndToken) => {
            const { gameToken, name } = nameAndToken;
            const gameSetting = this.activeGameSettingMap.get(gameToken);
            if (gameSetting) {
                gameSetting.observerNames = gameSetting?.observerNames?.filter((observerName) => observerName !== name);
                this.activeGameSettingMap.set(gameToken, gameSetting);
            }
            this.refreshPendingGame$.next();
        });
        this.gameMaster.playerLeft$.subscribe((playersAndToken: PlayersAndToken) => {
            const { gameToken, players } = playersAndToken;
            const gameSetting = this.activeGameSettingMap.get(gameToken);
            if (gameSetting) {
                gameSetting.playerNames = players.map((player) => player.name);
                if (gameSetting.numberOfBots) {
                    gameSetting.numberOfBots = gameSetting.numberOfBots + 1;
                }
                this.activeGameSettingMap.set(gameToken, gameSetting);
            }
            this.refreshPendingGame$.next();
        });
    }

    isEnoughPlaceToJoin(pendingGameId: string) {
        const pendingGame = this.pendingGames.get(pendingGameId);
        if (!pendingGame) {
            return false;
        }
        return pendingGame.playerNames.length !== pendingGame.numberOfPlayers || pendingGame.privateGame;
    }

    getPendingGames(): OnlineGameSettings[] {
        const games: OnlineGameSettings[] = [];
        this.pendingGames.forEach((game, id) => {
            games.push(this.toOnlineGameSettings(id, game));
        });
        return games;
    }

    getObservableGames(): OnlineGameSettings[] {
        const gameTokens = [...this.gameMaster.activeGames.keys()];
        const games: OnlineGameSettings[] = gameTokens
            .map((gameToken) => this.activeGameSettingMap.get(gameToken))
            .filter((gameSettings: OnlineGameSettings) => gameSettings !== undefined && !gameSettings.privateGame) as OnlineGameSettings[];
        return games;
    }

    async createPendingGame(gameSettings: OnlineGameSettingsUI): Promise<string> {
        const gameToken = this.generateGameToken();
        const gameId = gameToken;
        this.pendingGames.set(gameId, gameSettings);
        await this.conversationService.createGameConversation(gameToken);
        return gameId;
    }

    launchPendingGame(id: string, gameSettings?: OnlineGameSettingsUI): string {
        if (!gameSettings) {
            gameSettings = this.pendingGames.get(id);
        }
        const onlineGameSettings = this.toOnlineGameSettings(id, gameSettings);
        const gameToken = id;
        onlineGameSettings.numberOfBots = onlineGameSettings.numberOfPlayers - onlineGameSettings.playerNames.length;
        this.pendingGames.delete(id);
        this.activeGameSettingMap.set(gameToken, onlineGameSettings);
        this.startGame(gameToken, onlineGameSettings);
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
        if (!gameSettings.privateGame) {
            gameSettings.playerNames.push(name);
            return id;
        }
        if (!gameSettings.tmpPlayerNames) {
            gameSettings.tmpPlayerNames = [];
        }
        gameSettings.tmpPlayerNames.push(name);
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
        const playerToRemoveIndex = gameSettings.playerNames.findIndex((name) => name === nameToRemove);
        if (playerToRemoveIndex !== NOT_FOUND) {
            gameSettings.playerNames.splice(playerToRemoveIndex, 1);
            return id;
        }
        const tmpPlayerToRemoveIndex = gameSettings.tmpPlayerNames.findIndex((name) => name === nameToRemove);
        if (tmpPlayerToRemoveIndex !== NOT_FOUND) {
            gameSettings.tmpPlayerNames.splice(tmpPlayerToRemoveIndex, 1);
            return id;
        }
        return id;
    }

    deletePendingGame(id: string) {
        this.pendingGames.delete(id);
    }

    async deleteGameConvo(id: string) {
        await this.conversationService.deleteGameConversation(id);
    }

    getPendingGame(id: string): OnlineGameSettings | undefined {
        const gameSettings = this.pendingGames.get(id);
        return !gameSettings ? undefined : this.toOnlineGameSettings(id, gameSettings);
    }

    getObservableGame(id: string): OnlineGameSettings | undefined {
        return this.activeGameSettingMap.get(id);
    }

    isObservableGame(gameToken: string): boolean {
        return this.getObservableGame(gameToken) !== undefined;
    }

    private isPendingGame(id: string): boolean {
        return this.pendingGames.has(id);
    }

    private startGame(gameToken: string, gameSettings: OnlineGameSettings): OnlineGameSettings {
        const newGame = this.gameMaster.createGame(gameToken, gameSettings);
        gameSettings.playerNames = newGame.players.map((player) => player.name);
        return gameSettings;
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
