import { GameHistoryService } from '@app/account/user-game-history/game-history.service';
import { NEW_GAME_TIMEOUT } from '@app/constants';
import { BotDifficulty } from '@app/database/bot-info/bot-difficulty';
import { LeaderboardService } from '@app/database/leaderboard-service/leaderboard.service';
import { GameActionNotifierService } from '@app/game/game-action-notifier/game-action-notifier.service';
import { GameCompiler } from '@app/game/game-compiler/game-compiler.service';
import { GameCreator } from '@app/game/game-creator/game-creator';
import { Action } from '@app/game/game-logic/actions/action';
import { ActionCompilerService } from '@app/game/game-logic/actions/action-compiler.service';
import { ActionCreatorService } from '@app/game/game-logic/actions/action-creator/action-creator.service';
import { DEFAULT_DICTIONARY_TITLE } from '@app/game/game-logic/constants';
import { MagicServerGame } from '@app/game/game-logic/game/magic-server-game';
import { ServerGame } from '@app/game/game-logic/game/server-game';
import { EndOfGame, EndOfGameReason } from '@app/game/game-logic/interface/end-of-game.interface';
import { GameStateToken, PlayerInfoToken, SyncState, SyncStateToken } from '@app/game/game-logic/interface/game-state.interface';
import { BotPlayer } from '@app/game/game-logic/player/bot-player';
import { BotManager } from '@app/game/game-logic/player/bot/bot-manager/bot-manager.service';
import { Player } from '@app/game/game-logic/player/player';
import { PointCalculatorService } from '@app/game/game-logic/point-calculator/point-calculator.service';
import { TimerController } from '@app/game/game-logic/timer/timer-controller.service';
import { TimerStartingTime, TimerTimeLeft } from '@app/game/game-logic/timer/timer-game-control.interface';
import { DictionaryService } from '@app/game/game-logic/validator/dictionary/dictionary.service';
import { BindedSocket } from '@app/game/game-manager/binded-client.interface';
import { GameMode } from '@app/game/game-mode.enum';
import { NameAndToken } from '@app/game/game-socket-handler/game-socket-handler.service';
import { UserAuth } from '@app/game/game-socket-handler/user-auth.interface';
import { OnlineAction } from '@app/game/online-action.interface';
import { ServerLogger } from '@app/logger/logger';
import { ConversationService } from '@app/messages-service/services/conversation.service';
import { SystemMessagesService } from '@app/messages-service/system-messages-service/system-messages.service';
import { OnlineGameSettings } from '@app/new-game/online-game.interface';
import { GameStats } from '@app/user/interfaces/game-stats.interface';
import { UserService } from '@app/user/services/user.service';
import { Observable, Subject } from 'rxjs';
import { Service } from 'typedi';

export interface PlayerRef {
    gameToken: string;
    player: Player;
}

export interface PlayersAndToken {
    gameToken: string;
    players: Player[];
}

@Service()
export class GameManagerService {
    activeGames = new Map<string, ServerGame>();
    activePlayers = new Map<string, PlayerRef>(); // socketId => PlayerRef[]
    linkedClients = new Map<string, BindedSocket[]>(); // gameToken => BindedSocket[]
    forfeitedPlayers = new Map<string, string[]>(); // gameToken => Forfeited players
    gameDeleted$ = new Subject<string>();
    playerLeft$ = new Subject<PlayersAndToken>();
    observerLeft$ = new Subject<NameAndToken>();

    private endGame$ = new Subject<EndOfGame>(); // gameToken

    private gameCreator: GameCreator;
    private newGameStateSubject = new Subject<GameStateToken>();
    private newSyncStateSubject = new Subject<SyncStateToken>();
    private forfeitedGameStateSubject = new Subject<PlayerInfoToken>();

    get forfeitedGameState$(): Observable<PlayerInfoToken> {
        return this.forfeitedGameStateSubject;
    }

    get newGameState$(): Observable<GameStateToken> {
        return this.newGameStateSubject;
    }

    get newSyncState$(): Observable<SyncStateToken> {
        return this.newSyncStateSubject;
    }

    get timerStartingTime$(): Observable<TimerStartingTime> {
        return this.timerController.timerStartingTime$;
    }

    get timeUpdate$(): Observable<TimerTimeLeft> {
        return this.timerController.timerTimeUpdate$;
    }

    constructor(
        private pointCalculator: PointCalculatorService,
        private messagesService: SystemMessagesService,
        private actionCompiler: ActionCompilerService,
        private gameCompiler: GameCompiler,
        private timerController: TimerController,
        private gameActionNotifier: GameActionNotifierService,
        private leaderboardService: LeaderboardService,
        private dictionaryService: DictionaryService,
        private botManager: BotManager,
        private conversationService: ConversationService,
        protected actionNotifier: GameActionNotifierService,
        protected actionCreator: ActionCreatorService,
        protected userService: UserService,
        protected gameHistoryService: GameHistoryService,
    ) {
        this.gameCreator = new GameCreator(
            this.pointCalculator,
            this.gameCompiler,
            this.messagesService,
            this.newGameStateSubject,
            this.newSyncStateSubject,
            this.endGame$,
            this.timerController,
            this.botManager,
            this.actionNotifier,
            this.actionCreator,
            this.gameHistoryService,
        );

        this.endGame$.subscribe((endOfGame: EndOfGame) => {
            const gameToken = endOfGame.gameToken;
            const hasEnded = true;
            if (endOfGame.reason === EndOfGameReason.GameEnded) {
                this.updateLeaderboard(endOfGame.players, gameToken);
            }
            this.insertGameInHistory(endOfGame.gameToken, hasEnded);
            this.updateGameStatistics(endOfGame.stats);
            this.deleteGame(gameToken);
        });
        this.observerLeft$.subscribe((nameAndToken: NameAndToken) => {
            const bindedSocket = this.linkedClients.get(nameAndToken.gameToken)?.filter((client) => client.name !== nameAndToken.name);
            if (bindedSocket) {
                this.linkedClients.set(nameAndToken.gameToken, bindedSocket);
            }
        });
    }

    async createGame(gameToken: string, onlineGameSettings: OnlineGameSettings): Promise<ServerGame> {
        const newServerGame = await this.gameCreator.createGame(onlineGameSettings, gameToken);
        this.activeGames.set(gameToken, newServerGame);
        this.linkedClients.set(gameToken, []);
        this.dictionaryService.makeGameDictionary(gameToken, DEFAULT_DICTIONARY_TITLE);
        await this.conversationService.createGameConversation(gameToken);
        this.startInactiveGameDestructionTimer(gameToken);
        return newServerGame;
    }

    addPlayerToGame(playerId: string, userAuth: UserAuth) {
        const gameToken = userAuth.gameToken;
        const game = this.activeGames.get(gameToken);
        if (!game) {
            throw Error(`GameToken ${gameToken} is not in active game`);
        }

        const playerName = userAuth.playerName;
        const linkedClientsInGame = this.linkedClients.get(gameToken);
        if (!linkedClientsInGame) {
            throw Error(`Can't add player, GameToken ${gameToken} is not in active game`);
        }
        const clientFound = linkedClientsInGame.find((client: BindedSocket) => client.name === playerName);
        if (clientFound) {
            throw Error(`Can't add player, someone else is already linked to ${gameToken} with ${playerName}`);
        }

        const bindedSocket: BindedSocket = { socketID: playerId, name: playerName };
        linkedClientsInGame.push(bindedSocket);

        const user = game.players.find((player: Player) => player.name === playerName);
        if (user) {
            const playerRef = { gameToken, player: user };
            this.activePlayers.set(playerId, playerRef);
        }
        const expectedClientCount = this.getExpectedNumberOfClients(game);
        if (linkedClientsInGame.length === expectedClientCount) {
            game.start();
        }
    }

    receivePlayerAction(playerId: string, action: OnlineAction) {
        const playerRef = this.activePlayers.get(playerId);
        if (!playerRef) {
            throw Error(`Player ${playerId} is not active anymore`);
        }
        const player = playerRef.player;
        try {
            const compiledAction = this.actionCompiler.translate(action, player);
            const gameToken = playerRef.gameToken;
            this.notifyAction(compiledAction, gameToken);
            player.play(compiledAction);
        } catch (error) {
            ServerLogger.logError(error);
            return;
        }
    }

    receiveSync(playerId: string, sync: SyncState) {
        const playerRef = this.activePlayers.get(playerId);
        if (!playerRef) {
            throw Error(`Player ${playerId} is not active anymore`);
        }
        const player = playerRef.player;
        try {
            player.syncronisation(sync);
        } catch (error) {
            ServerLogger.logError(error);
            return;
        }
    }

    async removePlayerFromGame(playerId: string) {
        const playerRef = this.activePlayers.get(playerId);
        if (!playerRef) {
            return;
        }
        const gameToken = playerRef.gameToken;
        const game = this.activeGames.get(gameToken);
        if (!game) {
            return;
        }
        const players = game.players.filter((player: Player) => player.name !== playerRef.player.name && !(player instanceof BotPlayer));
        const playerNames = players.map((player) => player.name);
        this.activePlayers.delete(playerId);
        this.addForfeitedPlayer(playerRef);
        if (playerNames.length <= 0) {
            game.forceEndturn();
            const isForfeited = true;
            this.insertGameInHistory(gameToken, !isForfeited);
            this.deleteGame(gameToken);
            return;
        }
        const newPlayer = await this.createNewBotPlayer(playerRef, playerNames, game.botDifficulty);
        const index = game.players.findIndex((player) => player.name === playerRef.player.name);
        game.players[index] = newPlayer;
        this.playerLeft$.next({ gameToken, players: game.players });
        this.sendForfeitPlayerInfo(gameToken, newPlayer, playerRef.player.name);
        if (game.activePlayerIndex === index) {
            game.forceEndturn();
            game.forcePlay();
        }
    }

    private async createNewBotPlayer(playerRef: PlayerRef, playerNames: string[], botDifficulty: BotDifficulty) {
        const newPlayer = await this.gameCreator.createBotPlayer(botDifficulty, playerNames);
        newPlayer.letterRack = playerRef.player.letterRack;
        newPlayer.points = playerRef.player.points;
        return newPlayer;
    }

    private getExpectedNumberOfClients(game: ServerGame) {
        const playerCount = game.players.length;
        let botCount = 0;
        for (const player of game.players) {
            if (player instanceof BotPlayer) {
                botCount++;
            }
        }
        return playerCount - botCount;
    }

    private startInactiveGameDestructionTimer(gameToken: string) {
        setTimeout(() => {
            const currentLinkedClient = this.linkedClients.get(gameToken);
            if (!currentLinkedClient) {
                this.deleteInactiveGame(gameToken);
                return;
            }
        }, NEW_GAME_TIMEOUT);
    }

    private notifyAction(action: Action, gameToken: string) {
        const playerNames = this.getPlayerNamesFromGame(gameToken);
        this.gameActionNotifier.notify(action, playerNames, gameToken);
    }

    private getPlayerNamesFromGame(gameToken: string) {
        const clientsInGame = this.linkedClients.get(gameToken);
        if (!clientsInGame) {
            throw Error(`GameToken ${gameToken} is not in active game`);
        }
        const clientNames = clientsInGame.map((linkedClient) => linkedClient.name);
        return clientNames;
    }

    private endGame(game: ServerGame) {
        game.stop();
    }

    private sendForfeitPlayerInfo(gameToken: string, newPlayer: Player, previousName: string) {
        const playerInfo = this.gameCompiler.compilePlayerInfo(newPlayer, previousName);
        const playerInfoToken: PlayerInfoToken = { playerInfo, gameToken };
        this.forfeitedGameStateSubject.next(playerInfoToken);
        const remainingPlayerNames = this.getPlayerNamesFromGame(gameToken);
        this.gameActionNotifier.notifyPlayerLeft(newPlayer, previousName, remainingPlayerNames, gameToken);
    }

    private deleteInactiveGame(gameToken: string) {
        const serverGame = this.activeGames.get(gameToken);
        if (serverGame) {
            this.endGame(serverGame);
        }
        this.deleteGame(gameToken);
    }

    private deleteGame(gameToken: string) {
        const game = this.activeGames.get(gameToken);
        if (game) {
            game.stop();
            this.gameDeleted$.next(gameToken);
        }
        this.activeGames.delete(gameToken);
        this.linkedClients.delete(gameToken);
        this.dictionaryService.deleteGameDictionary(gameToken);
        this.conversationService.deleteGameConversation(gameToken);
    }

    private updateLeaderboard(players: Player[], gameToken: string) {
        const isMagic = this.activeGames.get(gameToken) instanceof MagicServerGame;
        const gameMode = isMagic ? GameMode.Magic : GameMode.Classic;
        players
            .filter((player) => !(player instanceof BotPlayer))
            .forEach((player) => {
                const score = { name: player.name, point: player.points };
                this.leaderboardService.updateLeaderboard(score, gameMode);
            });
    }

    private updateGameStatistics(stats: Map<string, GameStats>) {
        stats.forEach(async (gameStats, name) => this.userService.updateStatistics(gameStats, name));
    }

    private insertGameInHistory(gameToken: string, hasEnded: boolean) {
        const game = this.activeGames.get(gameToken);
        if (!game) {
            return;
        }
        const startTime = game.startTime;
        const userNames = game.players.map((player) => player.name);

        const winnerNames = hasEnded ? game.getWinnerIndexes().map((index) => userNames[index]) : [];
        const gameMode = game instanceof MagicServerGame ? GameMode.Magic : GameMode.Classic;
        const forfeitedPlayers = (this.forfeitedPlayers.get(game.gameToken) ? this.forfeitedPlayers.get(game.gameToken) : []) as string[];
        this.gameHistoryService.insertGame(gameToken, gameMode, userNames, winnerNames, startTime, forfeitedPlayers);
    }

    private addForfeitedPlayer(playerRef: PlayerRef) {
        const { gameToken, player } = playerRef;
        const players = this.forfeitedPlayers.get(gameToken);
        if (!players) {
            this.forfeitedPlayers.set(gameToken, [player.name]);
            return;
        }
        players.push(player.name);
        this.forfeitedPlayers.set(gameToken, players);
    }
}
