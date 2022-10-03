import { Letter } from '@app/game-logic/game/board/letter.interface';
import { Player } from '@app/game-logic/player/player';
import { MagicCard } from '@app/game-logic/actions/magic-card/magic-card';

export class ExchangeALetter extends MagicCard {
    constructor(player: Player, readonly letterToExchange: Letter) {
        super(player);
    }
}
