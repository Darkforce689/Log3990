import { Vec2 } from '@app/classes/vec2';
import { Direction } from '@app/game/game-logic/actions/direction.enum';
import { LetterCreator } from '@app/game/game-logic/board/letter-creator';
import { Tile } from '@app/game/game-logic/board/tile';
import { BOARD_MAX_POSITION, BOARD_MIN_POSITION, EMPTY_CHAR } from '@app/game/game-logic/constants';
import { PlacementSetting } from '@app/game/game-logic/interface/placement-setting.interface';
import { isCharUpperCase } from '@app/game/game-logic/utils';
import { DictionaryService } from '@app/game/game-logic/validator/dictionary/dictionary.service';
import { Word, WordPlacement } from '@app/game/game-logic/validator/word-search/word';
import { Service } from 'typedi';

@Service()
export class WordSearcher {
    letterCreator = new LetterCreator();

    constructor(private dictionaryService: DictionaryService) {}

    getListOfValidWords(wordPlacement: WordPlacement, grid: Tile[][], gameToken: string): Word[] {
        const listOfValidWord: Word[] = [];
        const direction = wordPlacement.placement.direction;
        const word = wordPlacement.word;

        if (!this.dictionaryService.isWordInDict(word, gameToken)) {
            return [];
        }
        const letters = this.stringToTile(wordPlacement, grid);
        const index = this.findIndexOfLetterToPlace(wordPlacement, grid);
        listOfValidWord.push({ letters, index });

        const coordsOfLettersToPlace = this.findCoordOfLettersToPlace(wordPlacement, grid);
        for (const coord of coordsOfLettersToPlace) {
            const adjacentWordDirection = this.getAdjacentWordsDirection(direction);
            if (!this.hasNeighbour(coord, adjacentWordDirection, grid)) {
                return listOfValidWord;
            }
            const wordToValidate = this.extractWord(wordPlacement, coord, grid);
            if (!this.isInDictionnary(wordToValidate.letters, gameToken)) {
                return [];
            }
            listOfValidWord.push(wordToValidate);
        }
        return listOfValidWord;
    }

    private findIndexOfLetterToPlace(wordPlacement: WordPlacement, grid: Tile[][]) {
        const indexOfLetterToPlace: number[] = [];
        const originaldirection = wordPlacement.placement.direction;
        const startCoord = originaldirection === Direction.Horizontal ? wordPlacement.placement.x : wordPlacement.placement.y;

        const coordsOfLettersToPlace = this.findCoordOfLettersToPlace(wordPlacement, grid);
        coordsOfLettersToPlace.forEach((coord) => {
            const indexInBoard = originaldirection === Direction.Horizontal ? coord.x : coord.y;
            const indexInWord = indexInBoard - startCoord;
            indexOfLetterToPlace.push(indexInWord);
        });

        return indexOfLetterToPlace;
    }

    private hasNeighbour(coord: Vec2, adjacentWordDirection: Direction, grid: Tile[][]): boolean {
        const nextCoord = adjacentWordDirection === Direction.Horizontal ? { x: coord.x + 1, y: coord.y } : { x: coord.x, y: coord.y + 1 };
        const previousCoord = adjacentWordDirection === Direction.Horizontal ? { x: coord.x - 1, y: coord.y } : { x: coord.x, y: coord.y - 1 };
        return this.isTileOccupied(nextCoord.x, nextCoord.y, grid) || this.isTileOccupied(previousCoord.x, previousCoord.y, grid);
    }

    private extractWord(wordPlacement: WordPlacement, letterPos: Vec2, grid: Tile[][]): Word {
        const word = wordPlacement.word;
        const originalDirection = wordPlacement.placement.direction;
        const adjacentWordDirection = this.getAdjacentWordsDirection(originalDirection);
        const originalWordCoord = { x: wordPlacement.placement.x, y: wordPlacement.placement.y };

        let [x, y] = [letterPos.x, letterPos.y];
        while (this.isPreviousTileUsed({ x, y, direction: adjacentWordDirection }, letterPos, grid)) {
            [x, y] = adjacentWordDirection === Direction.Horizontal ? [x - 1, y] : [x, y - 1];
        }

        const letters: Tile[] = [];
        const index: number[] = [];
        let indexInNeighbor = 0;
        while (this.isTileUsed({ x, y }, letterPos, grid)) {
            if (this.isTileOccupied(x, y, grid)) {
                letters.push(grid[y][x]);
            } else {
                const indexInWord = originalDirection === Direction.Horizontal ? x - originalWordCoord.x : y - originalWordCoord.y;
                letters.push(this.createTile(word[indexInWord], { x, y }, grid));
                index.push(indexInNeighbor);
            }
            [x, y] = adjacentWordDirection === Direction.Horizontal ? [x + 1, y] : [x, y + 1];
            indexInNeighbor += 1;
        }
        return { letters, index };
    }

    private isInDictionnary(word: Tile[], gameToken: string): boolean {
        const wordString = this.tileToString(word).toLowerCase();
        return this.dictionaryService.isWordInDict(wordString, gameToken);
    }

    private isPreviousTileUsed(coord: PlacementSetting, letterPosition: Vec2, grid: Tile[][]): boolean {
        const adjacentWordDirection = coord.direction;
        const [x, y] = adjacentWordDirection === Direction.Horizontal ? [coord.x - 1, coord.y] : [coord.x, coord.y - 1];
        return this.isTileUsed({ x, y }, letterPosition, grid);
    }

    private isTileUsed(coord: Vec2, letterPosition: Vec2, grid: Tile[][]): boolean {
        const [x, y] = [coord.x, coord.y];
        if (coord.x === letterPosition.x && coord.y === letterPosition.y) {
            return true;
        }
        if (this.isTileOccupied(x, y, grid)) {
            return true;
        }
        return false;
    }

    private isInsideBoard(x: number, y: number): boolean {
        return x >= BOARD_MIN_POSITION && y >= BOARD_MIN_POSITION && x <= BOARD_MAX_POSITION && y <= BOARD_MAX_POSITION;
    }

    private isTileOccupied(x: number, y: number, grid: Tile[][]): boolean {
        if (!this.isInsideBoard(x, y)) {
            return false;
        }
        const char = grid[y][x].letterObject.char;
        return char !== EMPTY_CHAR;
    }

    private getAdjacentWordsDirection(originalWordDirection: Direction): Direction {
        return originalWordDirection === Direction.Horizontal ? Direction.Vertical : Direction.Horizontal;
    }

    private findCoordOfLettersToPlace(wordPlacement: WordPlacement, grid: Tile[][]): Vec2[] {
        const listOfCoord: Vec2[] = [];
        const originalDirection = wordPlacement.placement.direction;
        const word = wordPlacement.word;

        const startCoord = originalDirection === Direction.Horizontal ? wordPlacement.placement.x : wordPlacement.placement.y;
        const wordEnd = startCoord + word.length;
        let currentPos = startCoord;

        for (startCoord; currentPos < wordEnd; currentPos++) {
            const [x, y] =
                originalDirection === Direction.Horizontal ? [currentPos, wordPlacement.placement.y] : [wordPlacement.placement.x, currentPos];
            if (!this.isTileOccupied(x, y, grid)) {
                listOfCoord.push({ x, y });
            }
        }
        return listOfCoord;
    }

    private stringToTile(wordPlacement: WordPlacement, grid: Tile[][]): Tile[] {
        let [x, y] = [wordPlacement.placement.x, wordPlacement.placement.y];
        const originalDirection = wordPlacement.placement.direction;
        const word = wordPlacement.word;
        const wordTile: Tile[] = [];
        for (const letter of word) {
            const tile = this.createTile(letter, { x, y }, grid);
            wordTile.push(tile);
            [x, y] = originalDirection === Direction.Horizontal ? [x + 1, y] : [x, y + 1];
        }
        return wordTile;
    }

    private tileToString(word: Tile[]): string {
        let wordTemp = '';
        word.forEach((tile) => {
            wordTemp = wordTemp.concat(tile.letterObject.char.valueOf());
        });
        return wordTemp;
    }

    private createTile(char: string, pos: Vec2, grid: Tile[][]): Tile {
        const tile = grid[pos.y][pos.x];
        const letterMultiplicator = tile.letterMultiplicator;
        const wordMultiplicator = tile.wordMultiplicator;
        const newTile = new Tile(letterMultiplicator, wordMultiplicator);
        newTile.letterObject = isCharUpperCase(char) ? this.letterCreator.createBlankLetter(char) : this.letterCreator.createLetter(char);
        return newTile;
    }
}
