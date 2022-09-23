/* eslint-disable no-unused-expressions */
/* eslint-disable @typescript-eslint/no-unused-expressions */
import { GameActionNotifierService } from '@app/game/game-action-notifier/game-action-notifier.service';
import { Direction } from '@app/game/game-logic/actions/direction.enum';
import { ExchangeLetter } from '@app/game/game-logic/actions/exchange-letter';
import { PassTurn } from '@app/game/game-logic/actions/pass-turn';
import { PlaceLetter } from '@app/game/game-logic/actions/place-letter';
import { Player } from '@app/game/game-logic/player/player';
import { PointCalculatorService } from '@app/game/game-logic/point-calculator/point-calculator.service';
import { WordSearcher } from '@app/game/game-logic/validator/word-search/word-searcher.service';
import { createSinonStubInstance } from '@app/test.util';
import { expect } from 'chai';

describe('GameActionNotifier', () => {
    let gameActionNotifierService: GameActionNotifierService;
    let pointCalculatorService: PointCalculatorService;
    let wordSearcher: WordSearcher;
    const lettersToExchange = [
        { char: 'm', value: 3 },
        { char: 'a', value: 1 },
    ];

    const playerNames = ['Joueur1', 'Joueur2'];

    beforeEach(() => {
        gameActionNotifierService = new GameActionNotifierService();
        pointCalculatorService = createSinonStubInstance<PointCalculatorService>(PointCalculatorService);
        wordSearcher = createSinonStubInstance<WordSearcher>(WordSearcher);
    });

    it('should send notification with exchangeLetter action', () => {
        const exchangeAction = new ExchangeLetter(new Player('allo'), lettersToExchange);
        const gameToken = '1';
        gameActionNotifierService.notify(exchangeAction, playerNames, gameToken);
        gameActionNotifierService.notification$.subscribe((message) => {
            expect(message.gameToken).to.equal(gameToken);
            expect(message.content).to.equal('allo échange 2 lettres');
        });
    });

    it('should throw Error if notification with exchangeLetter action', () => {
        const exchangeAction = new ExchangeLetter(new Player('Joueur1'), lettersToExchange);
        const gameToken = '1';
        try {
            gameActionNotifierService.notify(exchangeAction, playerNames, gameToken);
            gameActionNotifierService.notification$.subscribe((message) => {
                expect(message).to.be.undefined;
            });
        } catch (e) {
            expect(e.message).to.equal('No opponent found for Joueur1');
        }
    });

    it('should send notification with passTurn action', () => {
        const passTurn = new PassTurn(new Player('allo'));
        const gameToken = '1';
        gameActionNotifierService.notify(passTurn, playerNames, gameToken);
        gameActionNotifierService.notification$.subscribe((message) => {
            expect(message.gameToken).to.equal(gameToken);
            expect(message.content).to.equal('allo passe son tour');
        });
    });

    it('should send notification with placeLetter action', () => {
        const placeLetter = new PlaceLetter(
            new Player('allo'),
            'bonjour',
            { x: 1, y: 1, direction: Direction.Horizontal },
            pointCalculatorService,
            wordSearcher,
        );
        const gameToken = '1';
        gameActionNotifierService.notify(placeLetter, playerNames, gameToken);
        gameActionNotifierService.notification$.subscribe((message) => {
            expect(message.gameToken).to.equal(gameToken);
            expect(message.content).to.equal('allo place le mot bonjour en a1h');
        });
    });
});
