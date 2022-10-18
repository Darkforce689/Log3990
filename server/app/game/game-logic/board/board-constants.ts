export enum MultiType {
    Letter,
    Word,
}

export interface Bonus {
    v: number;
    type: MultiType;
}

export const X3_WORD = 'X3_WORD';
export const X2_WORD = 'X2_WORD';
export const X3_LETTER = 'X3_LETTER';
export const X2_LETTER = 'X2_LETTER';

export const BONUS_MAP = new Map<string, Bonus>([
    [X3_WORD, { v: 3, type: MultiType.Word }],
    [X2_WORD, { v: 2, type: MultiType.Word }],
    [X3_LETTER, { v: 3, type: MultiType.Letter }],
    [X2_LETTER, { v: 2, type: MultiType.Letter }],
]);

export interface BoardSettingPosition {
    x: number;
    y: string;
    bonus: Bonus;
}

/* eslint-disable @typescript-eslint/no-non-null-assertion */
export const MULTIPLICATORS: BoardSettingPosition[] = [
    { x: 1, y: 'A', bonus: BONUS_MAP.get(X3_WORD)! },
    { x: 8, y: 'A', bonus: BONUS_MAP.get(X3_WORD)! },
    { x: 15, y: 'A', bonus: BONUS_MAP.get(X3_WORD)! },

    { x: 2, y: 'B', bonus: BONUS_MAP.get(X2_WORD)! },
    { x: 14, y: 'B', bonus: BONUS_MAP.get(X2_WORD)! },

    { x: 3, y: 'C', bonus: BONUS_MAP.get(X2_WORD)! },
    { x: 13, y: 'C', bonus: BONUS_MAP.get(X2_WORD)! },

    { x: 4, y: 'D', bonus: BONUS_MAP.get(X2_WORD)! },
    { x: 12, y: 'D', bonus: BONUS_MAP.get(X2_WORD)! },

    { x: 5, y: 'E', bonus: BONUS_MAP.get(X2_WORD)! },
    { x: 11, y: 'E', bonus: BONUS_MAP.get(X2_WORD)! },

    { x: 1, y: 'H', bonus: BONUS_MAP.get(X3_WORD)! },
    { x: 8, y: 'H', bonus: BONUS_MAP.get(X2_WORD)! },
    { x: 15, y: 'H', bonus: BONUS_MAP.get(X3_WORD)! },

    { x: 5, y: 'K', bonus: BONUS_MAP.get(X2_WORD)! },
    { x: 11, y: 'K', bonus: BONUS_MAP.get(X2_WORD)! },

    { x: 4, y: 'L', bonus: BONUS_MAP.get(X2_WORD)! },
    { x: 12, y: 'L', bonus: BONUS_MAP.get(X2_WORD)! },

    { x: 3, y: 'M', bonus: BONUS_MAP.get(X2_WORD)! },
    { x: 13, y: 'M', bonus: BONUS_MAP.get(X2_WORD)! },

    { x: 2, y: 'N', bonus: BONUS_MAP.get(X2_WORD)! },
    { x: 14, y: 'N', bonus: BONUS_MAP.get(X2_WORD)! },

    { x: 1, y: 'O', bonus: BONUS_MAP.get(X3_WORD)! },
    { x: 8, y: 'O', bonus: BONUS_MAP.get(X3_WORD)! },
    { x: 15, y: 'O', bonus: BONUS_MAP.get(X3_WORD)! },

    { x: 4, y: 'A', bonus: BONUS_MAP.get(X2_LETTER)! },
    { x: 12, y: 'A', bonus: BONUS_MAP.get(X2_LETTER)! },

    { x: 6, y: 'B', bonus: BONUS_MAP.get(X3_LETTER)! },
    { x: 10, y: 'B', bonus: BONUS_MAP.get(X3_LETTER)! },

    { x: 7, y: 'C', bonus: BONUS_MAP.get(X2_LETTER)! },
    { x: 9, y: 'C', bonus: BONUS_MAP.get(X2_LETTER)! },

    { x: 1, y: 'D', bonus: BONUS_MAP.get(X2_LETTER)! },
    { x: 8, y: 'D', bonus: BONUS_MAP.get(X2_LETTER)! },
    { x: 15, y: 'D', bonus: BONUS_MAP.get(X2_LETTER)! },

    { x: 2, y: 'F', bonus: BONUS_MAP.get(X3_LETTER)! },
    { x: 6, y: 'F', bonus: BONUS_MAP.get(X3_LETTER)! },
    { x: 10, y: 'F', bonus: BONUS_MAP.get(X3_LETTER)! },
    { x: 14, y: 'F', bonus: BONUS_MAP.get(X3_LETTER)! },

    { x: 3, y: 'G', bonus: BONUS_MAP.get(X2_LETTER)! },
    { x: 7, y: 'G', bonus: BONUS_MAP.get(X2_LETTER)! },
    { x: 9, y: 'G', bonus: BONUS_MAP.get(X2_LETTER)! },
    { x: 13, y: 'G', bonus: BONUS_MAP.get(X2_LETTER)! },

    { x: 4, y: 'H', bonus: BONUS_MAP.get(X2_LETTER)! },
    { x: 12, y: 'H', bonus: BONUS_MAP.get(X2_LETTER)! },

    { x: 3, y: 'I', bonus: BONUS_MAP.get(X2_LETTER)! },
    { x: 7, y: 'I', bonus: BONUS_MAP.get(X2_LETTER)! },
    { x: 9, y: 'I', bonus: BONUS_MAP.get(X2_LETTER)! },
    { x: 13, y: 'I', bonus: BONUS_MAP.get(X2_LETTER)! },

    { x: 2, y: 'J', bonus: BONUS_MAP.get(X3_LETTER)! },
    { x: 6, y: 'J', bonus: BONUS_MAP.get(X3_LETTER)! },
    { x: 10, y: 'J', bonus: BONUS_MAP.get(X3_LETTER)! },
    { x: 14, y: 'J', bonus: BONUS_MAP.get(X3_LETTER)! },

    { x: 1, y: 'L', bonus: BONUS_MAP.get(X2_LETTER)! },
    { x: 8, y: 'L', bonus: BONUS_MAP.get(X2_LETTER)! },
    { x: 15, y: 'L', bonus: BONUS_MAP.get(X2_LETTER)! },

    { x: 7, y: 'M', bonus: BONUS_MAP.get(X2_LETTER)! },
    { x: 9, y: 'M', bonus: BONUS_MAP.get(X2_LETTER)! },

    { x: 6, y: 'N', bonus: BONUS_MAP.get(X3_LETTER)! },
    { x: 10, y: 'N', bonus: BONUS_MAP.get(X3_LETTER)! },

    { x: 4, y: 'O', bonus: BONUS_MAP.get(X2_LETTER)! },
    { x: 12, y: 'O', bonus: BONUS_MAP.get(X2_LETTER)! },
];
