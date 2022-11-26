import { GameHistoryService } from '@app/account/user-game-history/game-history.service';
import { BotDifficulty } from '@app/game/game-logic/player/bot/bot-difficulty';
import { GameCompiler } from '@app/game/game-compiler/game-compiler.service';
import { PassTurn } from '@app/game/game-logic/actions/pass-turn';
import { DEFAULT_TIME_PER_TURN } from '@app/game/game-logic/constants';
import { ServerGame } from '@app/game/game-logic/game/server-game';
import { EndOfGame } from '@app/game/game-logic/interface/end-of-game.interface';
import { GameStateToken, SyncStateToken } from '@app/game/game-logic/interface/game-state.interface';
import { Player } from '@app/game/game-logic/player/player';
import { PointCalculatorService } from '@app/game/game-logic/point-calculator/point-calculator.service';
import { TimerController } from '@app/game/game-logic/timer/timer-controller.service';
import { SystemMessagesService } from '@app/messages-service/system-messages-service/system-messages.service';
import { createSinonStubInstance } from '@app/test.util';
import { expect } from 'chai';
import { Subject } from 'rxjs';

describe('PassTurn', () => {
    let game: ServerGame;
    const player1: Player = new Player('Tim');
    const player2: Player = new Player('George');
    const randomBonus = false;
    const pointCalculator = createSinonStubInstance<PointCalculatorService>(PointCalculatorService);
    const gameCompiler = createSinonStubInstance<GameCompiler>(GameCompiler);
    const mockNewGameState$ = new Subject<GameStateToken>();
    const mockNewSyncState$ = new Subject<SyncStateToken>();
    const messagesService = createSinonStubInstance<SystemMessagesService>(SystemMessagesService);
    const timerController = createSinonStubInstance<TimerController>(TimerController);
    const gameHistoryService = createSinonStubInstance<GameHistoryService>(GameHistoryService);
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
            mockNewSyncState$,
            mockEndGame$,
            BotDifficulty.Easy,
            gameHistoryService,
        );
        game.players.push(player1);
        game.players.push(player2);
        game.start();
    });

    it('should pass turn', () => {
        const beforePlayer: Player = game.getActivePlayer();
        const passAction = new PassTurn(beforePlayer);
        beforePlayer.play(passAction);
        const afterPlayer: Player = game.getActivePlayer();
        expect(beforePlayer.name).not.to.equal(afterPlayer.name);
    });
});
