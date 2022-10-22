import { ServerGame } from '@app/game/game-logic/game/server-game';
import { MagicCard } from '@app/game/game-logic/actions/magic-card/magic-card';
import { REDUCETIMER_ID } from '@app/game/game-logic/actions/magic-card/magic-card-constants';
import { MagicServerGame } from '@app/game/game-logic/game/magic-server-game';

export class ReduceTimer extends MagicCard {
    id: string = REDUCETIMER_ID;

    protected perform(game: ServerGame) {
        const magicGame = game as MagicServerGame;
        magicGame.activeMagicCards = magicGame.activeMagicCards.filter((card) => card.id !== this.id);
        for (let i = 0; i < magicGame.players.length - 1; i++) magicGame.activeMagicCards.push({ id: this.id });
        this.end();
    }
}
