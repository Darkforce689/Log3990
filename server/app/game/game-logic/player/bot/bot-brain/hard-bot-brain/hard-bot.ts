import { Action } from '@app/game/game-logic/actions/action';
import { Direction } from '@app/game/game-logic/actions/direction.enum';
import { Letter } from '@app/game/game-logic/board/letter.interface';
import { RACK_LETTER_COUNT } from '@app/game/game-logic/constants';
import { ServerGame } from '@app/game/game-logic/game/server-game';
import { PlacementSetting } from '@app/game/game-logic/interface/placement-setting.interface';
import { ValidWord } from '@app/game/game-logic/interface/valid-word';
import { BotPlayer } from '@app/game/game-logic/player/bot-player';
import { BotBrain } from '@app/game/game-logic/player/bot/bot-brain/bot-brain';
import { Player } from '@app/game/game-logic/player/player';

export class HardBotBrain extends BotBrain {
    bestWordList: ValidWord[] = [];

    actionPicker(player: BotPlayer, game: ServerGame): Action {
        const validWordsList = this.bruteForceStart(game, player);
        if (validWordsList.length === 0) {
            return this.exchangeAction(player, game);
        } else {
            const pickedWord = this.bestWordPicker(validWordsList);
            return this.playAction(player, pickedWord[0]);
        }
    }

    private bestWordPicker(validWordsList: ValidWord[]): ValidWord[] {
        const numberOfWords = 4;
        const zeroValueWord = new ValidWord('');
        zeroValueWord.value.totalPoints = 0;
        this.bestWordList = [];

        for (let i = 0; i < numberOfWords; i++) {
            this.bestWordList.push(zeroValueWord);
        }

        for (const validWord of validWordsList) {
            for (let index = 0; index < numberOfWords; index++) {
                if (validWord.value.totalPoints > this.bestWordList[index].value.totalPoints) {
                    this.bestWordList.splice(index, 0, validWord);
                    this.bestWordList.pop();
                    break;
                }
            }
        }
        return this.bestWordList;
    }

    private playAction(player: Player, pickedWord: ValidWord): Action {
        const placeSetting: PlacementSetting = {
            x: pickedWord.startingTileX,
            y: pickedWord.startingTileY,
            direction: pickedWord.isVertical ? Direction.Vertical : Direction.Horizontal,
        };
        return this.actionCreator.createPlaceLetter(player, pickedWord.word, placeSetting);
    }

    private exchangeAction(player: Player, game: ServerGame): Action {
        if (game.letterBag.lettersLeft >= RACK_LETTER_COUNT) {
            return this.actionCreator.createExchange(player, player.letterRack);
        }
        if (game.letterBag.lettersLeft > 0) {
            const lettersToExchange: Letter[] = [];
            const indexStart = this.getRandomInt(player.letterRack.length - 1);
            for (let i = 0; i < game.letterBag.lettersLeft; i++) {
                lettersToExchange.push(player.letterRack[(indexStart + i) % player.letterRack.length]);
            }
            return this.actionCreator.createExchange(player, lettersToExchange);
        }
        return this.passAction(player);
    }

    private passAction(player: Player): Action {
        return this.actionCreator.createPassTurn(player);
    }
}
