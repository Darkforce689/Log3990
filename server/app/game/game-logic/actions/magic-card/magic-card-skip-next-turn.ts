import { ServerGame } from '@app/game/game-logic/game/server-game';
import { MagicCard } from '@app/game/game-logic/actions/magic-card/magic-card';
import { SKIPNEXTTURN_ID } from '@app/game/game-logic/actions/magic-card/magic-card-constants';
import { MagicServerGame } from '@app/game/game-logic/game/magic-server-game';

export class SkipNextTurn extends MagicCard {
    id: string = SKIPNEXTTURN_ID;

    protected perform(game: ServerGame) {
        // TODO: would be nice to add notification saying who exchanged with who

        (game as MagicServerGame).activeMagicCards.push({ id: this.id });

        this.end();
    }
}
