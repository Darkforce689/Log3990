import { ServerGame } from '@app/game/game-logic/game/server-game';
import { MagicCard } from '@app/game/game-logic/actions/magic-card';
import { Letter } from '@app/game/game-logic/board/letter.interface';
import { Player } from '@app/game/game-logic/player/player';

export class ExchangeALetter extends MagicCard {
    constructor(player: Player, readonly letterToExchange: Letter) {
        super(player);
    }

    protected perform(game: ServerGame) {
        const letterFromBag: Letter = game.letterBag.drawGameLetters(1)[0];
        // TODO: add a notification that the play has exchanged a letter

        const rackLetterToExchange = this.player.getLettersFromRack([this.letterToExchange])[0];
        const nLettersInRack = this.player.letterRack.length;
        for (let letterIndex = 0; letterIndex < nLettersInRack; letterIndex++) {
            const letter = this.player.letterRack[letterIndex];
            if (rackLetterToExchange === letter) {
                const newLetter = letterFromBag;
                this.player.letterRack[letterIndex] = newLetter;
                break;
            }
        }
        game.letterBag.addLetter({ ...this.letterToExchange });
        this.end();
    }
}
