// TODO GL3A22107-35 : Remove or Adapt server-side tests
import { Tile } from '@app/game/game-logic/board/tile';
import { RACK_LETTER_COUNT } from '@app/game/game-logic/constants';
import { BotCalculatorService } from '@app/game/game-logic/player/bot-calculator/bot-calculator.service';
import { MockBoard } from '@app/game/game-logic/validator/word-search/mock-board';
import { expect } from 'chai';

describe('BotCalculatorService', () => {
    let botCalculator: BotCalculatorService;
    let grid: Tile[][];
    let listOfWord: Tile[][];
    let word: Tile[];
    beforeEach(() => {
        botCalculator = new BotCalculatorService();
        listOfWord = [];
        grid = new MockBoard().grid;
    });

    it('should be created', () => {
        expect(botCalculator).to.be.instanceOf(BotCalculatorService);
    });

    it('should calculate the correct points of a word when placing all the players letters if >=7', () => {
        const totalPointsOfWord = 101;
        word = [
            { letterObject: { char: 'B', value: 3 }, letterMultiplicator: 1, wordMultiplicator: 3 },
            { letterObject: { char: 'A', value: 1 }, letterMultiplicator: 1, wordMultiplicator: 1 },
            { letterObject: { char: 'T', value: 1 }, letterMultiplicator: 1, wordMultiplicator: 1 },
            { letterObject: { char: 'E', value: 1 }, letterMultiplicator: 2, wordMultiplicator: 1 },
            { letterObject: { char: 'A', value: 1 }, letterMultiplicator: 1, wordMultiplicator: 1 },
            { letterObject: { char: 'U', value: 1 }, letterMultiplicator: 1, wordMultiplicator: 1 },
            { letterObject: { char: 'X', value: 8 }, letterMultiplicator: 1, wordMultiplicator: 1 },
        ];
        listOfWord.push(word);
        grid[0][0].letterObject = { char: 'B', value: 3 };
        grid[0][1].letterObject = { char: 'A', value: 1 };
        grid[0][2].letterObject = { char: 'T', value: 1 };
        grid[0][3].letterObject = { char: 'E', value: 1 };
        grid[0][4].letterObject = { char: 'A', value: 1 };
        grid[0][5].letterObject = { char: 'U', value: 1 };
        grid[0][6].letterObject = { char: 'X', value: 8 };

        const estimation = botCalculator.testPlaceLetterCalculation(RACK_LETTER_COUNT, listOfWord);
        expect(estimation.isBingo).to.be.equal(true);
        expect(estimation.totalPoints).to.be.equal(totalPointsOfWord);
    });

    it('should calculate the correct points when more than one word was made', () => {
        listOfWord = [];
        grid[2][0].letterObject = { char: 'B', value: 3 };
        grid[2][1].letterObject = { char: 'A', value: 1 };
        grid[2][2].letterObject = { char: 'K', value: 5 };
        grid[2][3].letterObject = { char: 'E', value: 1 };
        const wordBat = [grid[2][0], grid[3][0], grid[4][0]];
        const wordBake = [grid[2][0], grid[2][1], grid[2][2], grid[2][3]];
        const pointBat = 5;
        const pointBake = 10;
        listOfWord.push(wordBat);
        listOfWord.push(wordBake);

        const letterToPlace = RACK_LETTER_COUNT - wordBake.length;
        const estimation = botCalculator.testPlaceLetterCalculation(letterToPlace, listOfWord);
        expect(estimation.wordsPoints[0].points).to.be.equal(pointBat);
        expect(estimation.wordsPoints[1].points).to.be.equal(pointBake);
    });

    it('should estimate the correct points of every word made', () => {
        grid[2][0].letterObject = { char: 'B', value: 3 };
        grid[2][1].letterObject = { char: 'A', value: 1 };
        grid[2][2].letterObject = { char: 'K', value: 5 };
        grid[2][3].letterObject = { char: 'E', value: 1 };
        const wordBat = [grid[2][0], grid[3][0], grid[4][0]];
        const wordBake = [grid[2][0], grid[2][1], grid[2][2], grid[2][3]];
        const pointBat = 5;
        const pointBake = 10;
        listOfWord.push(wordBat);
        listOfWord.push(wordBake);
        const letterToPlace = RACK_LETTER_COUNT - wordBake.length;
        const estimation = botCalculator.testPlaceLetterCalculation(letterToPlace, listOfWord);
        expect(estimation.wordsPoints[0].points).to.be.equal(pointBat);
        expect(estimation.wordsPoints[1].points).to.be.equal(pointBake);
    });
});
