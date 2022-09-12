import { BotDifficulty } from '@app/database/bot-info/bot-difficulty';
import { BotInfoService } from '@app/database/bot-info/bot-info.service';
import { GameCompiler } from '@app/game/game-compiler/game-compiler.service';
import { ActionCreatorService } from '@app/game/game-logic/actions/action-creator/action-creator.service';
import { ServerGame } from '@app/game/game-logic/game/server-game';
import { SpecialServerGame } from '@app/game/game-logic/game/special-server-game';
import { EndOfGame } from '@app/game/game-logic/interface/end-of-game.interface';
import { GameStateToken } from '@app/game/game-logic/interface/game-state.interface';
import { ObjectiveCreator } from '@app/game/game-logic/objectives/objective-creator/objective-creator.service';
import { BotMessagesService } from '@app/game/game-logic/player/bot-message/bot-messages.service';
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
        private endGameSubject: Subject<EndOfGame>,
        private timerController: TimerController,
        private objectiveCreator: ObjectiveCreator,
        private botInfoService: BotInfoService,
        private botManager: BotManager,
        protected botMessage: BotMessagesService,
        protected actionCreator: ActionCreatorService,
    ) {}

    async createGame(onlineGameSettings: OnlineGameSettings, gameToken: string): Promise<ServerGame> {
        const newServerGame = this.createNewGame(onlineGameSettings, gameToken);
        const players = await this.createPlayers(
            onlineGameSettings.numberOfPlayers,
            onlineGameSettings.playerNames,
            onlineGameSettings.botDifficulty,
        );
        newServerGame.players = players;
        return newServerGame;
    }

    private createNewGame(gameSettings: OnlineGameSettings, gameToken: string) {
        const gameMode = gameSettings.gameMode;
        if (gameMode === GameMode.Special) {
            return this.createSpecialServerGame(gameSettings, gameToken);
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
            this.endGameSubject,
        );
    }

    private createSpecialServerGame(gameSettings: OnlineGameSettings, gameToken: string): ServerGame {
        return new SpecialServerGame(
            this.timerController,
            gameSettings.randomBonus,
            gameSettings.timePerTurn,
            gameToken,
            this.pointCalculator,
            this.gameCompiler,
            this.messagesService,
            this.newGameStateSubject,
            this.endGameSubject,
            this.objectiveCreator,
        );
    }

    /**
     * Creates N player instances from M real players names.
     * When N > M, creates N-M bots players
     *
     * @param numberOfPlayers total number of players (N)
     * @param playerNames real players names (array of length M)
     * @param botDifficulty uniform difficulty of the bots created
     * @returns created players, including bots
     */
    private async createPlayers(numberOfPlayers: number, playerNames: string[], botDifficulty: BotDifficulty): Promise<Player[]> {
        const players = playerNames.map((name) => new Player(name));
        const numberOfBots = numberOfPlayers - players.length;
        for (let i = 0; i < numberOfBots; i++) {
            const newBot = new BotPlayer(this.botInfoService, this.botManager, botDifficulty, this.botMessage, this.actionCreator);
            await newBot.updateBotName(playerNames);
            players.push(newBot);
            playerNames.push(newBot.name);
        }
        return players;
    }
}
