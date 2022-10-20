import { BoardSettingPosition, BONUS_MAP, MULTIPLICATORS, MultiType } from '@app/game/game-logic/board/board-constants';
import { ASCII_CODE, BOARD_DIMENSION, EMPTY_CHAR } from '@app/game/game-logic/constants';
import { Tile } from './tile';

export class Board {
    grid: Tile[][];

    constructor(public randomBonus: boolean = false) {
        this.grid = [];
        for (let i = 0; i < BOARD_DIMENSION; i++) {
            this.grid[i] = [];
            for (let j = 0; j < BOARD_DIMENSION; j++) {
                this.grid[i][j] = new Tile();
                this.grid[i][j].letterObject = { char: EMPTY_CHAR, value: 1 };
            }
        }
        this.generateMultiplicators(randomBonus);
    }

    hasNeighbour(x: number, y: number): boolean {
        if (x + 1 < BOARD_DIMENSION) {
            if (this.grid[y][x + 1].letterObject.char !== EMPTY_CHAR) {
                return true;
            }
        }
        if (x - 1 >= 0) {
            if (this.grid[y][x - 1].letterObject.char !== EMPTY_CHAR) {
                return true;
            }
        }
        if (y + 1 < BOARD_DIMENSION) {
            if (this.grid[y + 1][x].letterObject.char !== EMPTY_CHAR) {
                return true;
            }
        }
        if (y - 1 >= 0) {
            if (this.grid[y - 1][x].letterObject.char !== EMPTY_CHAR) {
                return true;
            }
        }
        return false;
    }

    placeRandomBonus(position: { x: number; y: number }) {
        const randomIndex = Math.floor(Math.random() * BONUS_MAP.size);
        const randomBonusToPlace = Array.from(BONUS_MAP.values())[randomIndex];
        if (randomBonusToPlace.type === MultiType.Letter) {
            this.grid[position.y][position.x].wordMultiplicator = 1;
            this.grid[position.y][position.x].letterMultiplicator = randomBonusToPlace.v;
            return;
        }
        this.grid[position.y][position.x].letterMultiplicator = 1;
        this.grid[position.y][position.x].wordMultiplicator = randomBonusToPlace.v;
    }

    private randomMultiplicators(): BoardSettingPosition[] {
        const newMultiplicators: BoardSettingPosition[] = [];
        const values: number[] = [];
        for (const multiplicator of MULTIPLICATORS) {
            newMultiplicators.push({ x: multiplicator.x, y: multiplicator.y, bonus: { v: multiplicator.bonus.v, type: multiplicator.bonus.type } });
            values.push(multiplicator.bonus.v);
        }
        for (const element of newMultiplicators) {
            const newValueIndex = Math.floor(Math.random() * values.length);
            element.bonus.v = values[newValueIndex];
            values.splice(newValueIndex, 1);
        }
        return newMultiplicators;
    }

    private generateMultiplicators(randomBonus: boolean): void {
        let listMultiplicator = MULTIPLICATORS;
        if (randomBonus) {
            do {
                listMultiplicator = this.randomMultiplicators();
            } while (listMultiplicator === MULTIPLICATORS);
        }
        for (const elem of listMultiplicator) {
            if (elem.bonus.type === MultiType.Letter) {
                this.grid[elem.x - 1][elem.y.charCodeAt(0) - ASCII_CODE].letterMultiplicator = elem.bonus.v;
                continue;
            }
            this.grid[elem.x - 1][elem.y.charCodeAt(0) - ASCII_CODE].wordMultiplicator = elem.bonus.v;
        }
    }
}
