import { Action } from '@app/game-logic/actions/action';
import { PlaceLetter } from '@app/game-logic/actions/place-letter';
import { UIAction } from '@app/game-logic/actions/ui-actions/ui-action';
import { UIInputControllerService } from '@app/game-logic/actions/ui-actions/ui-input-controller.service';
import { LetterPlacement } from '@app/game-logic/actions/ui-actions/ui-place-interface';
import { WordPlacement } from '@app/game-logic/actions/ui-actions/word-placement.interface';
import { BOARD_DIMENSION, EMPTY_CHAR, JOKER_CHAR, MIN_PLACE_LETTER_ARG_SIZE, NEXT_INDEX, NOT_FOUND, PREVIOUS_INDEX } from '@app/game-logic/constants';
import { Direction } from '@app/game-logic/direction.enum';
import { BoardService } from '@app/game-logic/game/board/board.service';
import { LetterCreator } from '@app/game-logic/game/board/letter-creator';
import { GameInfoService } from '@app/game-logic/game/game-info/game-info.service';
import { isInsideOfBoard } from '@app/game-logic/utils';

export class UIDragAndDrop implements UIAction {
    concernedIndexes = new Set<number>();
    letterPlacements: LetterPlacement[] = [];
    letterCreator = new LetterCreator();
    tempLettersPosition: { x: number; y: number }[] = [];
    direction = Direction.Horizontal;

    constructor(private info: GameInfoService, private boardService: BoardService, private inputController: UIInputControllerService) {}

    get canBeCreated(): boolean {
        return this.letterPlacements.length > 0 && this.concernedIndexes.size > 0;
    }

    receiveRightClick(): void {
        return;
    }

    receiveLeftClick(): void {
        return;
    }

    receiveKey(): void {
        return;
    }

    receiveHoldReleased(args: number, dropPoint: { x: number; y: number }, selectedChar: string | undefined): void {
        this.addTempLetter({ x: dropPoint.x, y: dropPoint.y, rackIndex: args }, selectedChar ?? this.info.activePlayer.letterRack[args].char);
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
        if (!this.checkPlaceLetter(this.tempLettersPosition) || !this.verifyDirection()) {
            return null;
        }
        return createdAction;
    }

    destroy(): void {
        for (const placement of this.letterPlacements) {
            const newBlankLetter = this.letterCreator.createBlankLetter(' ');
            this.boardService.board.grid[placement.y][placement.x].letterObject = newBlankLetter;
        }
        this.boardService.resetIndexEvent.emit();
        this.concernedIndexes.clear();
        this.tempLettersPosition = [];
        this.letterPlacements = [];
        this.inputController.sendSyncState(this.tempLettersPosition);
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
        this.letterPlacements.sort((a, b) => {
            return this.direction === Direction.Horizontal ? a.x - b.x : a.y - b.y;
        });
        const lastLetterPlacement = this.letterPlacements[this.letterPlacements.length - 1];
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
        if (!isInsideOfBoard(x, y)) {
            return false;
        }
        return this.boardService.board.grid[y][x].letterObject.char !== EMPTY_CHAR;
    }

    private addTempLetter(letterPlacement: LetterPlacement, key: string) {
        if (this.concernedIndexes.has(letterPlacement.rackIndex)) {
            this.deleteOldLetterPlacement(letterPlacement);
        }
        const oldLetterPlacement = this.letterPlacements.find((value) => value.x === letterPlacement.x && value.y === letterPlacement.y);
        if (oldLetterPlacement) {
            this.deleteOldLetterPlacement(oldLetterPlacement);
        }
        if (letterPlacement.x === NOT_FOUND && letterPlacement.y === NOT_FOUND) return;

        this.concernedIndexes.add(letterPlacement.rackIndex);
        this.letterPlacements.push(letterPlacement);
        this.tempLettersPosition.push({ x: letterPlacement.x, y: letterPlacement.y });
        this.placeTempLetter(letterPlacement, key);
        this.verifyDirection();
    }

    private placeTempLetter(letterPlacement: LetterPlacement, key: string) {
        const concernedTile = this.boardService.board.grid[letterPlacement.y][letterPlacement.x];
        const usedChar = this.info.player.letterRack[letterPlacement.rackIndex].char;
        if (usedChar === JOKER_CHAR) concernedTile.letterObject = this.letterCreator.createBlankLetter(key);
        else concernedTile.letterObject = this.letterCreator.createLetter(usedChar);
        concernedTile.letterObject.isTemp = true;
        if (this.tempLettersPosition.length >= 1) {
            this.inputController.sendSyncState(this.tempLettersPosition);
        }
    }

    private verifyDirection(): boolean {
        const firstValue = this.tempLettersPosition[0];
        const mapX = this.tempLettersPosition.map((value) => value.x);
        const mapY = this.tempLettersPosition.map((value) => value.y);
        const sameX = mapX.filter((value) => value !== firstValue.x).length === 0;
        const sameY = mapY.filter((value) => value !== firstValue.y).length === 0;

        if (sameY) this.direction = Direction.Horizontal;
        else if (sameX) this.direction = Direction.Vertical;

        return sameX || sameY;
    }

    private deleteOldLetterPlacement(letterPlacement: LetterPlacement) {
        const letterPlacementToRemove = this.letterPlacements.find((value) => value.rackIndex === letterPlacement.rackIndex);
        if (!letterPlacementToRemove) return;
        this.concernedIndexes.delete(letterPlacement.rackIndex);
        this.letterPlacements.splice(this.letterPlacements.indexOf(letterPlacementToRemove), 1);
        this.tempLettersPosition.splice(
            this.tempLettersPosition.findIndex((value) => value.x === letterPlacementToRemove.x && value.y === letterPlacementToRemove.y),
            1,
        );
        const tile = this.boardService.board.grid[letterPlacementToRemove.y][letterPlacementToRemove.x];
        tile.letterObject = { char: EMPTY_CHAR, value: 0 };
        if (this.tempLettersPosition.length >= 0) {
            this.inputController.sendSyncState(this.tempLettersPosition);
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
                if (!this.isAdjacentTileEmpty(x, y, direction, PREVIOUS_INDEX)) {
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
        return !this.isAdjacentTileEmpty(x, y, adjacentDirection, PREVIOUS_INDEX) || !this.isAdjacentTileEmpty(x, y, adjacentDirection, NEXT_INDEX);
    }

    private isAddingToAWord(lettersIndex: { x: number; y: number }[], direction: Direction) {
        const firstCoord = lettersIndex[0];
        const lastIndex = lettersIndex.length - 1;
        const lastCoord = lettersIndex[lastIndex];

        if (!this.isAdjacentTileEmpty(firstCoord.x, firstCoord.y, direction, PREVIOUS_INDEX)) {
            return true;
        }
        if (!this.isAdjacentTileEmpty(lastCoord.x, lastCoord.y, direction, NEXT_INDEX)) {
            return true;
        }
        return false;
    }

    private isAdjacentTileEmpty(x: number, y: number, direction: Direction, delta: number) {
        [x, y] = direction === Direction.Horizontal ? [x + delta, y] : [x, y + delta];
        if (!isInsideOfBoard(x, y)) {
            return true;
        }
        return this.grid[y][x].letterObject.char === EMPTY_CHAR;
    }

    get grid() {
        return this.boardService.board.grid;
    }
}
