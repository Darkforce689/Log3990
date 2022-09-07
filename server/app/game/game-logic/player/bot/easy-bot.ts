import { Action } from '@app/game/game-logic/actions/action';
import { Direction } from '@app/game/game-logic/actions/direction.enum';
import { LetterBag } from '@app/game/game-logic/board/letter-bag';
import { RACK_LETTER_COUNT } from '@app/game/game-logic/constants';
import { ServerGame } from '@app/game/game-logic/game/server-game';
import { PlacementSetting } from '@app/game/game-logic/interface/placement-setting.interface';
import { BotPlayer } from '@app/game/game-logic/player/bot-player';
import { BotBrain } from '@app/game/game-logic/player/bot/bot';
import { ValidWord } from '@app/game/game-logic/player/bot/valid-word';
import { Player } from '@app/game/game-logic/player/player';

export class EasyBotBrain extends BotBrain {
    static actionProbability = { play: 0.8, exchange: 0.1, pass: 0.1 };
    static placementProbability = { sixOrLess: 0.4, sevenToTwelve: 0.3, other: 0.3 };
    static botPointSetting = {
        sixOrLess: {
            prob: 0.4,
            value: 6,
        },
        sevenToTwelve: {
            prob: 0.3,
            value: 12,
        },
        other: {
            prob: 0.3,
            value: 18,
        },
    };

    actionPicker(player: BotPlayer, game: ServerGame): Action {
        const randomValue = Math.random();
        if (randomValue <= EasyBotBrain.actionProbability.play) {
            let action = this.playAction(player, game);
            if (!action) {
                action = this.passAction(player);
            }
            return action;
        } else if (
            randomValue <= EasyBotBrain.actionProbability.play + EasyBotBrain.actionProbability.exchange &&
            game.letterBag.lettersLeft > RACK_LETTER_COUNT
        ) {
            return this.exchangeAction(player);
        } else {
            return this.passAction(player);
        }
    }

    private randomWordPicker(validWordsList: ValidWord[]): ValidWord {
        const randomValue = Math.random();
        const validWordList: ValidWord[] = validWordsList;
        const wordP6: ValidWord[] = [];
        const wordP7to12: ValidWord[] = [];
        const wordP13To18: ValidWord[] = [];
        validWordList.forEach((word) => {
            if (word.value.totalPoints <= EasyBotBrain.botPointSetting.sixOrLess.value) {
                wordP6.push(word);
            } else if (
                word.value.totalPoints > EasyBotBrain.botPointSetting.sixOrLess.value &&
                word.value.totalPoints <= EasyBotBrain.botPointSetting.sevenToTwelve.value
            ) {
                wordP7to12.push(word);
            } else if (
                word.value.totalPoints > EasyBotBrain.botPointSetting.sevenToTwelve.value &&
                word.value.totalPoints <= EasyBotBrain.botPointSetting.other.value
            ) {
                wordP13To18.push(word);
            }
        });
        let wordPicked: ValidWord;
        if (randomValue <= EasyBotBrain.botPointSetting.sixOrLess.prob) {
            wordPicked = this.wordPicker(wordP6);
            return wordPicked;
        } else if (randomValue <= EasyBotBrain.botPointSetting.sevenToTwelve.prob + EasyBotBrain.botPointSetting.other.prob) {
            wordPicked = this.wordPicker(wordP7to12);
            return wordPicked;
        } else {
            wordPicked = this.wordPicker(wordP13To18);
            return wordPicked;
        }
    }

    private playAction(player: BotPlayer, game: ServerGame): Action {
        const validWordsList = this.bruteForceStart(game, player);
        const pickedWord: ValidWord = this.randomWordPicker(validWordsList);
        if (pickedWord) {
            const placeSetting: PlacementSetting = {
                x: pickedWord.startingTileX,
                y: pickedWord.startingTileY,
                direction: pickedWord.isVertical ? Direction.Vertical : Direction.Horizontal,
            };
            return this.actionCreator.createPlaceLetter(player, pickedWord.word, placeSetting);
        }
        return this.passAction(player);
    }

    private exchangeAction(player: Player): Action {
        const numberOfLettersToExchange = this.getRandomInt(LetterBag.playerLetterCount, 1);
        let lettersToExchangeIndex;
        const lettersToExchange = [];
        const indexArray = [];
        let randomInt;
        for (let i = 0; i < LetterBag.playerLetterCount; i++) {
            indexArray.push(i);
        }
        for (let i = 0; i < numberOfLettersToExchange; i++) {
            randomInt = this.getRandomInt(indexArray.length);
            lettersToExchangeIndex = indexArray[randomInt];
            indexArray.splice(randomInt, 1);
            lettersToExchange.push(player.letterRack[lettersToExchangeIndex]);
        }
        return this.actionCreator.createExchange(player, lettersToExchange);
    }

    private passAction(player: Player): Action {
        return this.actionCreator.createPassTurn(player);
    }

    private wordPicker(list: ValidWord[]): ValidWord {
        const randomPicker = this.getRandomInt(list.length);
        return list[randomPicker];
    }
}
