import { ServerGame } from '@app/game/game-logic/game/server-game';
import { MagicCard } from '@app/game/game-logic/actions/magic-card/magic-card';
import { PLACERANDOMBONUS_ID } from '@app/game/game-logic/actions/magic-card/magic-card-constants';
import { Player } from '@app/game/game-logic/player/player';

export class PlaceBonus extends MagicCard {
    id: string = PLACERANDOMBONUS_ID;

    constructor(player: Player, readonly pointerPosition: { x: number; y: number }) {
        super(player);
    }

    protected perform(game: ServerGame) {
        // TODO: add a notification that the play has placed a bonus

        game.board.placeRandomBonus(this.pointerPosition);
        this.end();
    }
}
