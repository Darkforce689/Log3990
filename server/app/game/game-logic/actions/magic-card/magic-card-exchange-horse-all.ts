import { ServerGame } from '@app/game/game-logic/game/server-game';
import { MagicCard } from '@app/game/game-logic/actions/magic-card/magic-card';
import { EXCHANGEHORSEALL_ID } from '@app/game/game-logic/actions/magic-card/magic-card-constants';
import { Letter } from '@app/game/game-logic/board/letter.interface';

export class ExchangeHorseAll extends MagicCard {
    id: string = EXCHANGEHORSEALL_ID;

    protected perform(game: ServerGame) {
        // TODO: would be nice to add notification saying what happened

        const tempLetterRacks: Letter[][] = game.players.map((player) => player.letterRack);

        for (let i = 0; i < game.players.length; i++) {
            const index = (i + game.players.length - 1) % game.players.length;
            game.players[i].letterRack = tempLetterRacks[index];
        }
        this.end();
    }
}
