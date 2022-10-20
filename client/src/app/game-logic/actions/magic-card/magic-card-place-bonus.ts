import { Player } from '@app/game-logic/player/player';
import { MagicCard } from '@app/game-logic/actions/magic-card/magic-card';

export class PlaceBonus extends MagicCard {
    constructor(player: Player, readonly pointerPosition: { x: number; y: number }) {
        super(player);
    }
}
