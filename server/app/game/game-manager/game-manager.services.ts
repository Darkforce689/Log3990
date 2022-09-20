import { NEW_GAME_TIMEOUT } from '@app/constants';
import { BotDifficulty } from '@app/database/bot-info/bot-difficulty';
import { BotInfoService } from '@app/database/bot-info/bot-info.service';
import { LeaderboardService } from '@app/database/leaderboard-service/leaderboard.service';
import { GameActionNotifierService } from '@app/game/game-action-notifier/game-action-notifier.service';
import { GameCompiler } from '@app/game/game-compiler/game-compiler.service';
import { GameCreator } from '@app/game/game-creator/game-creator';
import { Action } from '@app/game/game-logic/actions/action';
import { ActionCompilerService } from '@app/game/game-logic/actions/action-compiler.service';
import { ActionCreatorService } from '@app/game/game-logic/actions/action-creator/action-creator.service';
import { ServerGame } from '@app/game/game-logic/game/server-game';
import { SpecialServerGame } from '@app/game/game-logic/game/special-server-game';
import { EndOfGame, EndOfGameReason } from '@app/game/game-logic/interface/end-of-game.interface';
import { GameStateToken, PlayerInfoToken } from '@app/game/game-logic/interface/game-state.interface';
import { ObjectiveCreator } from '@app/game/game-logic/objectives/objective-creator/objective-creator.service';
import { BotPlayer } from '@app/game/game-logic/player/bot-player';
import { BotManager } from '@app/game/game-logic/player/bot/bot-manager/bot-manager.service';
import { Player } from '@app/game/game-logic/player/player';
import { PointCalculatorService } from '@app/game/game-logic/point-calculator/point-calculator.service';
import { TimerController } from '@app/game/game-logic/timer/timer-controller.service';
import { TimerStartingTime, TimerTimeLeft } from '@app/game/game-logic/timer/timer-game-control.interface';
import { DictionaryService } from '@app/game/game-logic/validator/dictionary/dictionary.service';
import { BindedSocket } from '@app/game/game-manager/binded-client.interface';
import { GameMode } from '@app/game/game-mode.enum';
import { UserAuth } from '@app/game/game-socket-handler/user-auth.interface';
import { OnlineAction } from '@app/game/online-action.interface';
import { ServerLogger } from '@app/logger/logger';
import { SystemMessagesService } from '@app/messages-service/system-messages-service/system-messages.service';
import { OnlineGameSettings } from '@app/new-game/online-game.interface';
import { Observable, Subject } from 'rxjs';
import { Service } from 'typedi';

export interface PlayerRef {
    gameToken: string;
    player: Player;
}

@Service()
export class GameManagerService {
    activeGames = new Map<string, ServerGame>();
    activePlayers = new Map<string, PlayerRef>(); // gameToken => PlayerRef[]
    linkedClients = new Map<string, BindedSocket[]>(); // gameToken => BindedSocket[]

    private endGame$ = new Subject<EndOfGame>(); // gameToken

    private gameCreator: GameCreator;
    private newGameStateSubject = new Subject<GameStateToken>();
    private forfeitedGameStateSubject = new Subject<PlayerInfoToken>();

    get forfeitedGameState$(): Observable<PlayerInfoToken> {
        return this.forfeitedGameStateSubject;
    }

    get newGameState$(): Observable<GameStateToken> {
        return this.newGameStateSubject;
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
        private objectiveCreator: ObjectiveCreator,
        private leaderboardService: LeaderboardService,
        private dictionaryService: DictionaryService,
        private botInfoService: BotInfoService,
        private botManager: BotManager,
        protected actionCreator: ActionCreatorService,
    ) {
        this.gameCreator = new GameCreator(
            this.pointCalculator,
            this.gameCompiler,
            this.messagesService,
            this.newGameStateSubject,
            this.endGame$,
            this.timerController,
            this.objectiveCreator,
            this.botInfoService,
            this.botManager,
            this.actionCreator,
        );

        this.endGame$.subscribe((endOfGame: EndOfGame) => {
            const gameToken = endOfGame.gameToken;
            if (endOfGame.reason === EndOfGameReason.GameEnded) {
                this.updateLeaderboard(endOfGame.players, gameToken);
            }
            this.deleteGame(gameToken);
        });
    }

    async createGame(gameToken: string, onlineGameSettings: OnlineGameSettings): Promise<ServerGame> {
        const newServerGame = await this.gameCreator.createGame(onlineGameSettings, gameToken);
        this.activeGames.set(gameToken, newServerGame);
        this.linkedClients.set(gameToken, []);
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
        const user = game.players.find((player: Player) => player.name === playerName);
        if (!user) {
            throw Error(`Player ${playerName} not created in ${gameToken}`);
        }

        const linkedClientsInGame = this.linkedClients.get(gameToken);
        if (!linkedClientsInGame) {
            throw Error(`Can't add player, GameToken ${gameToken} is not in active game`);
        }
        const clientFound = linkedClientsInGame.find((client: BindedSocket) => client.name === playerName);
        if (clientFound) {
            throw Error(`Can't add player, someone else is already linked to ${gameToken} with ${playerName}`);
        }

        const playerRef = { gameToken, player: user };
        this.activePlayers.set(playerId, playerRef);
        const bindedSocket: BindedSocket = { socketID: playerId, name: playerName };
        linkedClientsInGame.push(bindedSocket);

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
        const playerNames: string[] = [];
        this.activePlayers.forEach((playerReference) => playerNames.push(playerReference.player.name));
        this.activePlayers.delete(playerId);
        if (this.activePlayers.size <= 0) {
            this.deleteGame(gameToken);
            return;
        }
        const newPlayer = await this.createNewBotPlayer(playerRef, playerNames);
        const index = game.players.findIndex((player) => player.name === playerRef.player.name);
        game.players[index] = newPlayer;
        this.sendForfeitPlayerInfo(gameToken, newPlayer, playerRef.player.name);
        if (game.activePlayerIndex === index) {
            game.forceEndturn();
            game.forcePlay();
        }
    }
    // TODO GL3A22107-32 : Update bot difficulty to what is specified when game created by player.
    private async createNewBotPlayer(playerRef: PlayerRef, playerNames: string[]) {
        const newPlayer = await this.gameCreator.createBotPlayer(BotDifficulty.Easy, playerNames);
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
        const clientsInGame = this.linkedClients.get(gameToken);
        if (!clientsInGame) {
            throw Error(`GameToken ${gameToken} is not in active game`);
        }
        this.gameActionNotifier.notify(action, clientsInGame, gameToken);
    }

    private endGame(game: ServerGame) {
        game.stop();
    }

    private sendForfeitPlayerInfo(gameToken: string, newPlayer: Player, previousName: string) {
        const playerInfo = this.gameCompiler.compilePlayerInfo(newPlayer, previousName);
        const playerInfoToken: PlayerInfoToken = { playerInfo, gameToken };
        this.forfeitedGameStateSubject.next(playerInfoToken);
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
        game?.stop();
        this.activeGames.delete(gameToken);
        this.linkedClients.delete(gameToken);
        this.dictionaryService.deleteGameDictionary(gameToken);
    }

    private updateLeaderboard(players: Player[], gameToken: string) {
        const isSpecial = this.activeGames.get(gameToken) instanceof SpecialServerGame;
        const gameMode = isSpecial ? GameMode.Special : GameMode.Classic;
        players
            .filter((player) => !(player instanceof BotPlayer))
            .forEach((player) => {
                const score = { name: player.name, point: player.points };
                this.leaderboardService.updateLeaderboard(score, gameMode);
            });
    }
}
