import { BotDifficulty } from '@app/database/bot-info/bot-difficulty';
import { ActionCreatorService } from '@app/game/game-logic/actions/action-creator/action-creator.service';
import { BotCalculatorService } from '@app/game/game-logic/player/bot-calculator/bot-calculator.service';
import { EasyBotLogic } from '@app/game/game-logic/player/bot/bot-logic/easy-bot-logic/easy-bot-logic';
import { HardBotLogic } from '@app/game/game-logic/player/bot/bot-logic/hard-bot-logic/hard-bot-logic';
import { BotDictionaryService } from '@app/game/game-logic/validator/dictionary/bot-dictionary/bot-dictionary';
import { WordSearcher } from '@app/game/game-logic/validator/word-search/word-searcher.service';
import { Service } from 'typedi';

@Service()
export class BotManager {
    hardBot: HardBotLogic;
    easyBot: EasyBotLogic;

    constructor(
        private botDictionaryService: BotDictionaryService,
        private botCalculatorService: BotCalculatorService,
        private wordSearcher: WordSearcher,
        private actionFactory: ActionCreatorService,
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
}
