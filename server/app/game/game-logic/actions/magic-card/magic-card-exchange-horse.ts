import { ServerGame } from '@app/game/game-logic/game/server-game';
import { MagicCard } from '@app/game/game-logic/actions/magic-card/magic-card';
import { EXCHANGEHORSE_ID } from '@app/game/game-logic/actions/magic-card/magic-card-constants';

export class ExchangeHorse extends MagicCard {
    id: string = EXCHANGEHORSE_ID;

    protected perform(game: ServerGame) {
        // TODO: would be nice to add notification saying who exchanged with who
        const indexToExchangeWith = (game.activePlayerIndex + Math.floor(Math.random() * (game.players.length - 1)) + 1) % game.players.length;

        const tempLetterRack = game.players[indexToExchangeWith].letterRack;
        game.players[indexToExchangeWith].letterRack = game.players[game.activePlayerIndex].letterRack;
        game.players[game.activePlayerIndex].letterRack = tempLetterRack;
        this.end();
    }
}
