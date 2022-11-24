import { GameHistoryService } from '@app/account/user-game-history/game-history.service';
import { BotDifficulty } from '@app/database/bot-info/bot-difficulty';
import { GameActionNotifierService } from '@app/game/game-action-notifier/game-action-notifier.service';
import { GameCompiler } from '@app/game/game-compiler/game-compiler.service';
import { ActionCreatorService } from '@app/game/game-logic/actions/action-creator/action-creator.service';
import { MagicServerGame } from '@app/game/game-logic/game/magic-server-game';
import { ServerGame } from '@app/game/game-logic/game/server-game';
import { EndOfGame } from '@app/game/game-logic/interface/end-of-game.interface';
import { GameStateToken, SyncStateToken } from '@app/game/game-logic/interface/game-state.interface';
import { BotPlayer } from '@app/game/game-logic/player/bot-player';
import { BotManager } from '@app/game/game-logic/player/bot/bot-manager/bot-manager.service';
import { Player } from '@app/game/game-logic/player/player';
import { PointCalculatorService } from '@app/game/game-logic/point-calculator/point-calculator.service';
import { TimerController } from '@app/game/game-logic/timer/timer-controller.service';
import { GameMode } from '@app/game/game-mode.enum';
import { SystemMessagesService } from '@app/messages-service/system-messages-service/system-messages.service';
import { OnlineGameSettings } from '@app/new-game/online-game.interface';
import { Subject } from 'rxjs';

export class GameCreator {
    static defaultOpponentName = 'AZERTY';

    constructor(
        private pointCalculator: PointCalculatorService,
        private gameCompiler: GameCompiler,
        private messagesService: SystemMessagesService,
        private newGameStateSubject: Subject<GameStateToken>,
        private newSyncStateSubject: Subject<SyncStateToken>,
        private endGameSubject: Subject<EndOfGame>,
        private timerController: TimerController,
        private botManager: BotManager,
        protected gameActionNotifier: GameActionNotifierService,
        protected actionCreator: ActionCreatorService,
        private gameHistoryService: GameHistoryService,
    ) {}

    createGame(onlineGameSettings: OnlineGameSettings, gameToken: string): ServerGame {
        const newServerGame = this.createNewGame(onlineGameSettings, gameToken);
        const players = this.createPlayers(onlineGameSettings.numberOfPlayers, onlineGameSettings.playerNames, onlineGameSettings.botDifficulty);
        newServerGame.players = players;
        return newServerGame;
    }

    createBotPlayer(botDifficulty: BotDifficulty, playerNames: string[]) {
        const botPlayer = new BotPlayer(this.botManager, botDifficulty, this.gameActionNotifier, this.actionCreator);
        botPlayer.updateBotName(playerNames);
        return botPlayer;
    }

    private createNewGame(gameSettings: OnlineGameSettings, gameToken: string) {
        const gameMode = gameSettings.gameMode;
        if (gameMode === GameMode.Magic) {
            return this.createMagicServerGame(gameSettings, gameToken);
        }
        return this.createClassicServerGame(gameSettings, gameToken);
    }

    private createClassicServerGame(gameSettings: OnlineGameSettings, gameToken: string): ServerGame {
        return new ServerGame(
            this.timerController,
            gameSettings.randomBonus,
            gameSettings.timePerTurn,
            gameToken,
            this.pointCalculator,
            this.gameCompiler,
            this.messagesService,
            this.newGameStateSubject,
            this.newSyncStateSubject,
            this.endGameSubject,
            gameSettings.botDifficulty,
            this.gameHistoryService,
        );
    }

    private createMagicServerGame(gameSettings: OnlineGameSettings, gameToken: string): ServerGame {
        return new MagicServerGame(
            this.timerController,
            gameSettings.randomBonus,
            gameSettings.timePerTurn,
            gameToken,
            this.pointCalculator,
            this.gameCompiler,
            this.messagesService,
            this.newGameStateSubject,
            this.newSyncStateSubject,
            this.endGameSubject,
            gameSettings.magicCardIds,
            gameSettings.botDifficulty,
            this.gameHistoryService,
        );
    }

    private createPlayers(numberOfPlayers: number, playerNames: string[], botDifficulty: BotDifficulty): Player[] {
        const players = playerNames.map((name) => new Player(name));
        const numberOfBots = numberOfPlayers - players.length;
        for (let i = 0; i < numberOfBots; i++) {
            const newBot = this.createBotPlayer(botDifficulty, playerNames);
            players.push(newBot);
            playerNames.push(newBot.name);
        }
        return players;
    }
}
