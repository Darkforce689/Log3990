/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable dot-notation */
import { BotDifficulty } from '@app/database/bot-info/bot-difficulty';
import { GameCompiler } from '@app/game/game-compiler/game-compiler.service';
import {
    MAX_NUMBER_OF_MAGIC_CARD,
    NUMBER_OF_WORDS_FOR_MAGIC_CARD,
    SPLITPOINTS_ID,
} from '@app/game/game-logic/actions/magic-card/magic-card-constants';
import { SplitPoints } from '@app/game/game-logic/actions/magic-card/magic-card-split-points';
import { MagicServerGame } from '@app/game/game-logic/game/magic-server-game';
import { EndOfGame } from '@app/game/game-logic/interface/end-of-game.interface';
import { GameStateToken, IMagicCard } from '@app/game/game-logic/interface/game-state.interface';
import { Player } from '@app/game/game-logic/player/player';
import { PointCalculatorService } from '@app/game/game-logic/point-calculator/point-calculator.service';
import { TimerController } from '@app/game/game-logic/timer/timer-controller.service';
import { SystemMessagesService } from '@app/messages-service/system-messages-service/system-messages.service';
import { createSinonStubInstance } from '@app/test.util';
import { expect } from 'chai';
import { Subject } from 'rxjs';

const TIME_PER_TURN = 10;

describe('MagicServerGame', () => {
    let game: MagicServerGame;
    let p1: Player;
    let p2: Player;
    const randomBonus = false;
    const gameToken = 'defaultToken';
    const timerController = createSinonStubInstance<TimerController>(TimerController);
    const pointCalculator = createSinonStubInstance<PointCalculatorService>(PointCalculatorService);
    const gameCompiler = createSinonStubInstance<GameCompiler>(GameCompiler);
    const messagesService = createSinonStubInstance<SystemMessagesService>(SystemMessagesService);
    const newGameStateSubject = new Subject<GameStateToken>();
    const endGameSubject = new Subject<EndOfGame>();

    beforeEach(() => {
        game = new MagicServerGame(
            timerController,
            randomBonus,
            TIME_PER_TURN,
            gameToken,
            pointCalculator,
            gameCompiler,
            messagesService,
            newGameStateSubject,
            endGameSubject,
            [SPLITPOINTS_ID],
            BotDifficulty.Easy,
        );
        p1 = new Player('Tim');
        p2 = new Player('Paul');
        game.players = [p1, p2];
    });

    it('should create instance', () => {
        expect(game).to.be.instanceOf(MagicServerGame);
    });

    it('should start both player with no magic cards', () => {
        game.start();
        expect(game.drawnMagicCards[0].length).to.be.equal(0);
        expect(game.drawnMagicCards[1].length).to.be.equal(0);
    });

    it('should start both players with 0 word placed', () => {
        game.start();
        expect(game.playersNumberOfWordsPlaced[0]).to.be.equal(0);
        expect(game.playersNumberOfWordsPlaced[1]).to.be.equal(0);
    });

    it('addWordCount should add the number of placed word to the active player', () => {
        game.start();
        game.activePlayerIndex = 0;
        game.addWordCount(1);
        expect(game.playersNumberOfWordsPlaced[0]).to.be.equal(1);
        expect(game.playersNumberOfWordsPlaced[1]).to.be.equal(0);
    });

    it('addWordCount should add the right number of magic cards to the active player', () => {
        game.start();
        game.activePlayerIndex = 0;
        game.addWordCount(NUMBER_OF_WORDS_FOR_MAGIC_CARD);
        expect(game.drawnMagicCards[0].length).to.be.equal(1);

        game.activePlayerIndex = 1;
        game.addWordCount(2 * NUMBER_OF_WORDS_FOR_MAGIC_CARD);
        expect(game.drawnMagicCards[1].length).to.be.equal(2);
    });

    it('addWordCount should not add more than 3 magic cards to the active player', () => {
        game.start();
        game.activePlayerIndex = 0;
        game.addWordCount((MAX_NUMBER_OF_MAGIC_CARD + 1) * NUMBER_OF_WORDS_FOR_MAGIC_CARD);
        expect(game.drawnMagicCards[0].length).to.be.equal(MAX_NUMBER_OF_MAGIC_CARD);
    });

    it('removeMagicCard should remove the played card from the active player', () => {
        game.start();
        game.activePlayerIndex = 0;
        game.drawnMagicCards[0] = [{ id: SPLITPOINTS_ID } as IMagicCard];
        const currentPlayer = game.getActivePlayer();
        const action = new SplitPoints(currentPlayer);
        game['takeAction'](action);
        expect(game.drawnMagicCards[0].length).to.be.equal(0);
    });
});
