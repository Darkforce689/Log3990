import { Tile } from '@app/game/game-logic/board/tile';
import { PlacementSetting } from '@app/game/game-logic/interface/placement-setting.interface';

export interface Word {
    letters: Tile[];
    index: number[];
}

export interface WordPlacement {
    word: string;
    placement: PlacementSetting;
}
