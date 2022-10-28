import { BotDifficulty } from '@app/database/bot-info/bot-difficulty';
import { GameCompiler } from '@app/game/game-compiler/game-compiler.service';
import { DEFAULT_TIME_PER_TURN } from '@app/game/game-logic/constants';
import { ServerGame } from '@app/game/game-logic/game/server-game';
import { EndOfGame } from '@app/game/game-logic/interface/end-of-game.interface';
import { GameStateToken } from '@app/game/game-logic/interface/game-state.interface';
import { Player } from '@app/game/game-logic/player/player';
import { PointCalculatorService } from '@app/game/game-logic/point-calculator/point-calculator.service';
import { TimerController } from '@app/game/game-logic/timer/timer-controller.service';
import { SystemMessagesService } from '@app/messages-service/system-messages-service/system-messages.service';
import { createSinonStubInstance } from '@app/test.util';
import { expect } from 'chai';
import { Subject } from 'rxjs';
import { SplitPoints } from './magic-card-split-points';

describe('SplitPoints', () => {
    let game: ServerGame;
    const player1: Player = new Player('Tim');
    const player2: Player = new Player('George');
    const randomBonus = false;
    const pointCalculator = createSinonStubInstance<PointCalculatorService>(PointCalculatorService);
    const gameCompiler = createSinonStubInstance<GameCompiler>(GameCompiler);
    const mockNewGameState$ = new Subject<GameStateToken>();
    const messagesService = createSinonStubInstance<SystemMessagesService>(SystemMessagesService);
    const timerController = createSinonStubInstance<TimerController>(TimerController);
    const mockEndGame$ = new Subject<EndOfGame>();
    beforeEach(() => {
        game = new ServerGame(
            timerController,
            randomBonus,
            DEFAULT_TIME_PER_TURN,
            'default_gameToken',
            pointCalculator,
            gameCompiler,
            messagesService,
            mockNewGameState$,
            mockEndGame$,
            BotDifficulty.Easy,
        );
        game.players.push(player1);
        game.players.push(player2);
        game.start();
    });

    it('should split points from other player', () => {
        const activePlayerIndex = 0;
        const unactivePlayerIndex = 1;
        game.activePlayerIndex = activePlayerIndex;
        const unactivePlayerStartingPoints = 100;
        const nbPlayers = 2;
        const pointsToSplit = 26; // Math.ceil((100 * 0.25) / 2) * 2 = 26

        const activePlayer = game.getActivePlayer();
        const unactivePlayer = game.players[unactivePlayerIndex];
        activePlayer.points = 0;
        unactivePlayer.points = unactivePlayerStartingPoints;

        const splitPointsAction = new SplitPoints(activePlayer);
        splitPointsAction.execute(game);

        expect(activePlayer.points).to.equal(pointsToSplit / nbPlayers);
        expect(unactivePlayer.points).to.equal(unactivePlayerStartingPoints - pointsToSplit / nbPlayers);
    });

    it('should not split points from active player', () => {
        const activePlayerIndex = 0;
        const unactivePlayerIndex = 1;
        game.activePlayerIndex = activePlayerIndex;
        const activePlayerStartingPoints = 200;
        const unactivePlayerStartingPoints = 100;
        const nbPlayers = 2;
        const pointsToSplit = 26; // Math.ceil((100 * 0.25) / 2) * 2 = 26

        const activePlayer = game.getActivePlayer();
        const unactivePlayer = game.players[unactivePlayerIndex];
        activePlayer.points = activePlayerStartingPoints;
        unactivePlayer.points = unactivePlayerStartingPoints;

        const splitPointsAction = new SplitPoints(activePlayer);
        splitPointsAction.execute(game);

        expect(activePlayer.points).to.equal(activePlayerStartingPoints + pointsToSplit / nbPlayers);
        expect(unactivePlayer.points).to.equal(unactivePlayerStartingPoints - pointsToSplit / nbPlayers);
    });
});
