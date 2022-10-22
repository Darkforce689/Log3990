import { ServerGame } from '@app/game/game-logic/game/server-game';
import { MagicCard } from '@app/game/game-logic/actions/magic-card/magic-card';
import { EXTRATURN_ID } from '@app/game/game-logic/actions/magic-card/magic-card-constants';
import { MagicServerGame } from '@app/game/game-logic/game/magic-server-game';

export class ExtraTurn extends MagicCard {
    id: string = EXTRATURN_ID;

    protected perform(game: ServerGame) {
        (game as MagicServerGame).activeMagicCards.push({ id: this.id });
        this.end();
    }
}
