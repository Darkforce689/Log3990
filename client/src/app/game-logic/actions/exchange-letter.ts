import { Action } from '@app/game-logic/actions/action';
import { Letter } from '@app/game-logic/game/board/letter.interface';
import { Player } from '@app/game-logic/player/player';

export class ExchangeLetter extends Action {
    constructor(player: Player, readonly lettersToExchange: Letter[]) {
        super(player);
    }
}
