import { Action } from '@app/game-logic/actions/action';
import { PlaceLetter } from '@app/game-logic/actions/place-letter';
import { UIAction } from '@app/game-logic/actions/ui-actions/ui-action';
import { LetterPlacement } from '@app/game-logic/actions/ui-actions/ui-place-interface';
import { WordPlacement } from '@app/game-logic/actions/ui-actions/word-placement.interface';
import {
    BACKSPACE,
    BOARD_DIMENSION,
    BOARD_MAX_POSITION,
    BOARD_MIN_POSITION,
    EMPTY_CHAR,
    JOKER_CHAR,
    MIN_PLACE_LETTER_ARG_SIZE,
    nextTile,
    previousTile,
} from '@app/game-logic/constants';
import { Direction } from '@app/game-logic/direction.enum';
import { BoardService } from '@app/game-logic/game/board/board.service';
import { LetterCreator } from '@app/game-logic/game/board/letter-creator';
import { GameInfoService } from '@app/game-logic/game/game-info/game-info.service';
import { convertToProperLetter, isStringALowerCaseLetter, isStringAnUpperCaseLetter } from '@app/game-logic/utils';

export class UIPlace implements UIAction {
    concernedIndexes = new Set<number>();
    orderedIndexes: LetterPlacement[] = [];
    letterCreator = new LetterCreator();
    direction = Direction.Horizontal;
    pointerPosition: { x: number; y: number } | null = null;
    tempLettersPosition: { x: number; y: number }[] = [];

    constructor(private info: GameInfoService, private boardService: BoardService) {}

    get canBeCreated(): boolean {
        return this.orderedIndexes.length > 0 && this.concernedIndexes.size > 0;
    }

    receiveRightClick(): void {
        return;
    }

    receiveLeftClick(args: unknown): void {
        const clickPosition = args as { x: number; y: number };
        if (this.isPlacementInProgress()) {
            return;
        }
        if (!this.canPlaceALetterHere(clickPosition.x, clickPosition.y)) {
            return;
        }
        if (this.isSamePositionClicked(clickPosition)) {
            this.toggleDirection();
            return;
        }
        this.pointerPosition = clickPosition;
        this.direction = Direction.Horizontal;
    }

    receiveKey(key: string): void {
        let letterPlacement = null;
        if (this.info.activePlayer !== this.info.player) {
            return;
        }
        switch (key) {
            case BACKSPACE:
                this.moveBackwards();
                return;
            default:
                letterPlacement = this.canUseLetter(key);
                if (letterPlacement) {
                    this.placeTempLetter(letterPlacement, key);
                    this.moveForwards();
                }
                break;
        }
    }

    receiveRoll(): void {
        return;
    }

    create(): Action | null {
        const wordPlacement = this.getWordFromBoard();
        const createdAction = new PlaceLetter(this.info.player, wordPlacement.word, {
            direction: this.direction,
            x: wordPlacement.x,
            y: wordPlacement.y,
        });
        if (!this.checkPlaceLetter(this.tempLettersPosition)) {
            return null;
        }
        return createdAction;
    }

    destroy(): void {
        for (const placement of this.orderedIndexes) {
            const newBlankLetter = this.letterCreator.createBlankLetter(' ');
            this.boardService.board.grid[placement.y][placement.x].letterObject = newBlankLetter;
        }
        this.pointerPosition = null;
        this.tempLettersPosition = [];
    }

    private isSamePositionClicked(clickPosition: { x: number; y: number }): boolean {
        if (!this.pointerPosition) {
            return false;
        }
        return clickPosition.x === this.pointerPosition.x && clickPosition.y === this.pointerPosition.y;
    }

    private getWordFromBoard(): WordPlacement {
        let wordPlacementFound = this.getWordFromBoardCrawler();
        if (wordPlacementFound.word.length < MIN_PLACE_LETTER_ARG_SIZE - 1) {
            this.direction = this.direction === Direction.Horizontal ? Direction.Vertical : Direction.Horizontal;
            wordPlacementFound = this.getWordFromBoardCrawler();
        }
        return wordPlacementFound;
    }

    private getWordFromBoardCrawler(): WordPlacement {
        const lastLetterPlacement = this.orderedIndexes[this.orderedIndexes.length - 1];
        let x = lastLetterPlacement.x;
        let y = lastLetterPlacement.y;
        let currentTileChar;
        let word = '';
        while (this.isThereALetter(x, y)) {
            [x, y] = this.direction === Direction.Horizontal ? [++x, y] : [x, ++y];
        }
        [x, y] = this.direction === Direction.Horizontal ? [--x, y] : [x, --y];
        do {
            currentTileChar = this.boardService.board.grid[y][x].letterObject;
            word = currentTileChar.value === 0 ? currentTileChar.char.toUpperCase() + word : currentTileChar.char.toLowerCase() + word;
            [x, y] = this.direction === Direction.Horizontal ? [--x, y] : [x, --y];
        } while (this.isThereALetter(x, y));
        [x, y] = this.direction === Direction.Horizontal ? [++x, y] : [x, ++y];
        return { word, x, y };
    }

    private isThereALetter(x: number, y: number): boolean {
        if (!this.isInsideOfBoard(x, y)) {
            return false;
        }
        return this.boardService.board.grid[y][x].letterObject.char !== EMPTY_CHAR;
    }

    private canUseLetter(key: string): LetterPlacement | null {
        if (!this.pointerPosition) {
            return null;
        }
        const possibleLetterIndex = this.findLetterIndexInRack(key);
        if (possibleLetterIndex === null) {
            return null;
        }
        return { x: this.pointerPosition.x, y: this.pointerPosition.y, rackIndex: possibleLetterIndex };
    }

    private placeTempLetter(letterPlacement: LetterPlacement, key: string) {
        if (!this.pointerPosition) {
            return;
        }
        this.concernedIndexes.add(letterPlacement.rackIndex);
        this.orderedIndexes.push(letterPlacement);
        this.tempLettersPosition.push({ x: this.pointerPosition.x, y: this.pointerPosition.y });
        const concernedTile = this.boardService.board.grid[this.pointerPosition.y][this.pointerPosition.x];
        const usedChar = this.info.player.letterRack[letterPlacement.rackIndex].char;
        if (usedChar === JOKER_CHAR) {
            concernedTile.letterObject = this.letterCreator.createBlankLetter(key);
            concernedTile.letterObject.isTemp = true;
            return;
        }
        concernedTile.letterObject = this.letterCreator.createLetter(usedChar);
        concernedTile.letterObject.isTemp = true;
    }

    private moveForwards(): void {
        if (!this.pointerPosition) {
            return;
        }
        let [x, y] = [this.pointerPosition.x, this.pointerPosition.y];
        do {
            [x, y] = this.direction === Direction.Horizontal ? [x + 1, y] : [x, y + 1];
            if (this.canPlaceALetterHere(x, y)) {
                this.pointerPosition = { x, y };
                return;
            }
        } while (this.isInsideOfBoard(x, y));

        this.pointerPosition = null;
    }

    private findLetterIndexInRack(key: string): number | null {
        let letter = convertToProperLetter(key);
        if (isStringAnUpperCaseLetter(letter)) {
            letter = JOKER_CHAR;
        }
        if (isStringALowerCaseLetter(letter) || letter === JOKER_CHAR) {
            return this.getUnusedLetterIndexInRack(letter);
        }
        return null;
    }

    private getUnusedLetterIndexInRack(char: string): number | null {
        for (let index = 0; index < this.info.player.letterRack.length; index++) {
            const rackLetter = this.info.player.letterRack[index];
            if (rackLetter.char.toLowerCase() === char && !this.concernedIndexes.has(index)) {
                return index;
            }
        }
        return null;
    }

    private canPlaceALetterHere(x: number, y: number): boolean {
        if (!this.isInsideOfBoard(x, y)) {
            return false;
        }
        return this.boardService.board.grid[y][x].letterObject.char === EMPTY_CHAR;
    }

    private isInsideOfBoard(x: number, y: number) {
        return x >= BOARD_MIN_POSITION && x <= BOARD_MAX_POSITION && y >= BOARD_MIN_POSITION && y <= BOARD_MAX_POSITION;
    }

    private moveBackwards(): void {
        const lastLetter = this.orderedIndexes.pop();
        if (!lastLetter) {
            return;
        }
        const newBlankLetter = this.letterCreator.createBlankLetter(' ');
        this.boardService.board.grid[lastLetter.y][lastLetter.x].letterObject = newBlankLetter;
        this.concernedIndexes.delete(lastLetter.rackIndex);
        this.pointerPosition = { x: lastLetter.x, y: lastLetter.y };
        this.tempLettersPosition.pop();
    }

    private isPlacementInProgress(): boolean {
        return this.canBeCreated;
    }

    private toggleDirection(): void {
        if (!this.isPlacementInProgress()) {
            this.direction = this.direction === Direction.Horizontal ? Direction.Vertical : Direction.Horizontal;
        }
    }

    private checkPlaceLetter(tempPosition: { x: number; y: number }[]): boolean {
        let direction = Direction.Horizontal;
        if (tempPosition.length > 1) {
            direction = tempPosition[0].x === tempPosition[1].x ? Direction.Vertical : Direction.Horizontal;
        }
        const centerTilePosition: number = Math.floor(BOARD_DIMENSION / 2);
        const hasCenterTile = this.grid[centerTilePosition][centerTilePosition].letterObject.char !== EMPTY_CHAR;
        const firstCoord = tempPosition[0];
        let [previousX, previousY] = [firstCoord.x - 1, firstCoord.y - 1];

        for (const pos of tempPosition) {
            const { x, y } = pos;
            if (hasCenterTile && x === centerTilePosition && y === centerTilePosition) {
                return true;
            }
            if (this.hasNeighbour(x, y, direction)) {
                return true;
            }
            const indexIsAdjacent = direction === Direction.Horizontal ? x === previousX + 1 : y === previousY + 1;
            if (!indexIsAdjacent) {
                if (!this.isAdjacentTileEmpty(x, y, direction, previousTile)) {
                    return true;
                }
            }
            [previousX, previousY] = [x, y];
        }
        if (this.isAddingToAWord(tempPosition, direction)) {
            return true;
        }
        if (!hasCenterTile) {
            // TODO GL3A22107-146 : add message to chat
            // this.sendErrorMessage("Commande impossible à réaliser : Aucun mot n'est pas placé sur la tuile centrale");
        } else {
            // TODO GL3A22107-146 : add message to chat
            // this.sendErrorMessage("Commande impossible à réaliser : Le mot placé n'est pas adjacent à un autre mot");
        }
        return false;
    }

    private hasNeighbour(x: number, y: number, direction: Direction): boolean {
        const adjacentDirection = direction === Direction.Horizontal ? Direction.Vertical : Direction.Horizontal;
        return !this.isAdjacentTileEmpty(x, y, adjacentDirection, previousTile) || !this.isAdjacentTileEmpty(x, y, adjacentDirection, nextTile);
    }

    private isAddingToAWord(lettersIndex: { x: number; y: number }[], direction: Direction) {
        const firstCoord = lettersIndex[0];
        const lastIndex = lettersIndex.length - 1;
        const lastCoord = lettersIndex[lastIndex];

        if (!this.isAdjacentTileEmpty(firstCoord.x, firstCoord.y, direction, previousTile)) {
            return true;
        }
        if (!this.isAdjacentTileEmpty(lastCoord.x, lastCoord.y, direction, nextTile)) {
            return true;
        }
        return false;
    }

    private isAdjacentTileEmpty(x: number, y: number, direction: Direction, delta: number) {
        [x, y] = direction === Direction.Horizontal ? [x + delta, y] : [x, y + delta];
        if (!this.isInsideOfBoard(x, y)) {
            return true;
        }
        return this.grid[y][x].letterObject.char === EMPTY_CHAR;
    }

    get grid() {
        return this.boardService.board.grid;
    }
}
