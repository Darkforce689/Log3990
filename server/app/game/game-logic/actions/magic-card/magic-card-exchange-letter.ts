import { MagicCard } from '@app/game/game-logic/actions/magic-card/magic-card';
import { EXCHANGEALETTER_ID } from '@app/game/game-logic/actions/magic-card/magic-card-constants';
import { Letter } from '@app/game/game-logic/board/letter.interface';
import { ServerGame } from '@app/game/game-logic/game/server-game';
import { Player } from '@app/game/game-logic/player/player';
import { ServerLogger } from '@app/logger/logger';

export class ExchangeALetter extends MagicCard {
    id: string = EXCHANGEALETTER_ID;

    constructor(player: Player, readonly letterToExchange: Letter) {
        super(player);
    }

    protected perform(game: ServerGame) {
        // TODO: add a notification that the play has exchanged a letter
        let rackLetterToExchange;
        try {
            rackLetterToExchange = this.player.getLettersFromRack([this.letterToExchange])[0];
        } catch (error) {
            ServerLogger.logError(
                `ExchangeALetter -> Error getting letters from letterRack for player ${this.player.name} in game ${game.gameToken}`,
            );
            this.end();
            return;
        }
        const letterFromBag: Letter = game.letterBag.drawGameLetters(1)[0];
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
