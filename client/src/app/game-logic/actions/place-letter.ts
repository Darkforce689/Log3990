import { Action } from '@app/game-logic/actions/action';
import { OfflineGame } from '@app/game-logic/game/games/offline-game/offline-game';
import { PlacementSetting } from '@app/game-logic/interfaces/placement-setting.interface';
import { Vec2 } from '@app/game-logic/interfaces/vec2';
import { Player } from '@app/game-logic/player/player';

// TODO GL3A22107-5 : whole class behavior methods -> to be removed / converted to be sent to server
export class PlaceLetter extends Action {
    affectedCoords: Vec2[];

    constructor(player: Player, public word: string, public placement: PlacementSetting) {
        super(player);
    }

    // eslint-disable-next-line no-unused-vars
    protected perform(game: OfflineGame) {
        // const previousGrid = copyGrid(game.board.grid);
        // const validWordList = this.wordSearcher.listOfValidWord(this);
        // const formedWords = validWordList.map((validWord) => validWord.letters);
        // this.putLettersOnBoard(game);
        // const currentGrid = game.board.grid;
        // this.player.removeLetterFromRack(this.lettersToRemoveInRack);
        // const wordValid = validWordList.length !== 0;
        // if (wordValid) {
        //     this.pointCalculator.placeLetterCalculation(this, formedWords);
        //     this.drawLettersForPlayer(game);
        //     if (game instanceof SpecialOfflineGame) {
        //         const updateObjectiveParams: ObjectiveUpdateParams = {
        //             previousGrid,
        //             currentGrid,
        //             lettersToPlace: this.lettersToRemoveInRack,
        //             formedWords,
        //             affectedCoords: this.affectedCoords,
        //         };
        //         (game as SpecialOfflineGame).updateObjectives(this, updateObjectiveParams);
        //     }
        //     this.end();
        // } else {
        //     timer(TIME_FOR_REVERT).subscribe(() => {
        //         this.revert(game);
        //         this.end();
        //     });
        // }
    }

    // private revert(game: OfflineGame) {
    //     this.removeLetterFromBoard(game);
    //     this.giveBackLettersToPlayer();
    // }

    // private removeLetterFromBoard(game: OfflineGame) {
    //     const grid = game.board.grid;
    //     for (const coord of this.affectedCoords) {
    //         const x = coord.x;
    //         const y = coord.y;
    //         grid[y][x].letterObject.char = EMPTY_CHAR;
    //     }
    // }

    // private drawLettersForPlayer(game: OfflineGame) {
    //     const drawnLetters = game.letterBag.drawGameLetters(this.lettersToRemoveInRack.length);
    //     for (const letter of drawnLetters) {
    //         this.player.letterRack.push(letter);
    //     }
    // }

    // private giveBackLettersToPlayer() {
    //     for (const letter of this.lettersToRemoveInRack) {
    //         this.player.letterRack.push(letter);
    //     }
    // }

    // private putLettersOnBoard(game: OfflineGame) {
    //     const startX = this.placement.x;
    //     const startY = this.placement.y;
    //     const direction = this.placement.direction;
    //     const grid = game.board.grid;
    //     this.lettersToRemoveInRack = [];
    //     this.affectedCoords = [];
    //     for (let wordIndex = 0; wordIndex < this.word.length; wordIndex++) {
    //         const [x, y] = direction === Direction.Horizontal ? [startX + wordIndex, startY] : [startX, startY + wordIndex];
    //         const char = grid[y][x].letterObject.char;

    //         if (char === EMPTY_CHAR) {
    //             const charToCreate = this.word[wordIndex];
    //             const letterToRemove = this.letterToRemove(charToCreate);
    //             this.lettersToRemoveInRack.push(letterToRemove);
    //             const newLetter = this.createNewLetter(charToCreate);
    //             grid[y][x].letterObject = newLetter;
    //             this.affectedCoords.push({ x, y });
    //         }
    //     }
    // }

    // private letterToRemove(char: string) {
    //     return isCharUpperCase(char) ? this.letterFactory.createLetter(JOKER_CHAR) : this.letterFactory.createLetter(char);
    // }

    // private createNewLetter(char: string) {
    //     const charToCreate = char.toLowerCase();
    //     return isCharUpperCase(char) ? this.letterFactory.createBlankLetter(charToCreate) : this.letterFactory.createLetter(charToCreate);
    // }
}
