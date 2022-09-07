import { ServerGame } from '@app/game/game-logic/game/server-game';
import { MagicCard } from '@app/game/game-logic/actions/magic-card';

export class GainAPoint extends MagicCard {
    protected perform(game: ServerGame) {
        const activePlayer = game.getActivePlayer();
        activePlayer.points++;
        this.end();
    }
}
