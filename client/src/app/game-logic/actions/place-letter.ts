import { Action } from '@app/game-logic/actions/action';
import { PlacementSetting } from '@app/game-logic/interfaces/placement-setting.interface';
import { Player } from '@app/game-logic/player/player';

export class PlaceLetter extends Action {
    constructor(player: Player, public word: string, public placement: PlacementSetting) {
        super(player);
    }
}
