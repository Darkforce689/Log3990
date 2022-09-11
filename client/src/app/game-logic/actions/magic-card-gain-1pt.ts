import { OfflineGame } from '@app/game-logic/game/games/offline-game/offline-game';
import { MagicCard } from '@app/game-logic/actions/magic-card';

export class GainAPoint extends MagicCard {
    protected perform(game: OfflineGame) {
        const activePlayer = game.getActivePlayer();
        activePlayer.points++;
        this.end();
    }
}
