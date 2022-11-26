import { BotDifficulty } from '@app/game/game-logic/player/bot/bot-difficulty';
import { GameActionNotifierService } from '@app/game/game-action-notifier/game-action-notifier.service';
import { ActionCreatorService } from '@app/game/game-logic/actions/action-creator/action-creator.service';
import { BotCalculatorService } from '@app/game/game-logic/player/bot-calculator/bot-calculator.service';
import { EasyBotLogic } from '@app/game/game-logic/player/bot/bot-logic/easy-bot-logic/easy-bot-logic';
import { HardBotLogic } from '@app/game/game-logic/player/bot/bot-logic/hard-bot-logic/hard-bot-logic';
import { getRandomInt } from '@app/game/game-logic/utils';
import { BotDictionaryService } from '@app/game/game-logic/validator/dictionary/bot-dictionary/bot-dictionary';
import { WordSearcher } from '@app/game/game-logic/validator/word-search/word-searcher.service';
import { Service } from 'typedi';
import { BotPlayer } from '@app/game/game-logic/player/bot-player';
import { DEFAULT_BOT } from '@app/game/game-logic/player/bot/default-bot-names';

@Service()
export class BotManager {
    hardBot: HardBotLogic;
    easyBot: EasyBotLogic;

    constructor(
        private botDictionaryService: BotDictionaryService,
        private botCalculatorService: BotCalculatorService,
        private wordSearcher: WordSearcher,
        private actionFactory: ActionCreatorService,
        private actionNotifier: GameActionNotifierService,
    ) {
        this.hardBot = new HardBotLogic(
            this.botDictionaryService,
            this.botCalculatorService,
            this.wordSearcher,
            this.actionFactory,
            BotDifficulty.Expert,
        );
        this.easyBot = new EasyBotLogic(
            this.botDictionaryService,
            this.botCalculatorService,
            this.wordSearcher,
            this.actionFactory,
            BotDifficulty.Easy,
        );
    }

    getBotNames(playerNames: string[], botDifficulty: BotDifficulty, numberOfBots: number): string[] {
        const botNames: string[] = [];
        for (let i = 0; i < numberOfBots; i++) {
            const name = this.getBotName(playerNames, botNames, botDifficulty);
            botNames.push(name);
        }
        return botNames;
    }

    getBotName(playerNames: string[], botNames: string[], botDifficulty: BotDifficulty): string {
        const availableBotNames: string[] = this.getAvailableBotNames(botDifficulty);
        const names = playerNames.concat(botNames);
        let generatedName: string;
        do {
            generatedName = availableBotNames[getRandomInt(availableBotNames.length)];
        } while (names.find((opponentName) => opponentName === generatedName));
        return generatedName;
    }

    createBotPlayer(name: string, botDifficulty: BotDifficulty) {
        const botLogic = botDifficulty === BotDifficulty.Easy ? this.easyBot : this.hardBot;
        return new BotPlayer(name, botLogic, botDifficulty, this.actionNotifier, this.actionFactory);
    }

    private getAvailableBotNames(botDifficulty: BotDifficulty): string[] {
        const botInfos = DEFAULT_BOT;
        const filteredBotInfos = botInfos.filter((bot) => bot.type === botDifficulty);
        const filteredBotInfosNames = filteredBotInfos.map((bot) => bot.name);
        return filteredBotInfosNames;
    }
}
