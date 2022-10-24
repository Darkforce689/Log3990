import { ServerGame } from '@app/game/game-logic/game/server-game';
import { MagicCard } from '@app/game/game-logic/actions/magic-card/magic-card';
import { SPLITPOINTS_ID } from '@app/game/game-logic/actions/magic-card/magic-card-constants';
import { PERCENTAGE_OF_POINTS_TO_SPLIT } from '@app/constants';

export class SplitPoints extends MagicCard {
    id: string = SPLITPOINTS_ID;

    protected perform(game: ServerGame) {
        const leader = game.getNonActiveTopPlayer();
        const nbPlayers = game.players.length;
        if (leader.points < nbPlayers) {
            this.end();
            return;
        }
        const pointsToSplit = Math.ceil((leader.points * PERCENTAGE_OF_POINTS_TO_SPLIT) / nbPlayers) * nbPlayers;
        leader.points -= pointsToSplit;
        game.players.forEach((player) => {
            player.points += pointsToSplit / nbPlayers;
        });
        this.end();
    }
}
