import { BotDifficulty } from '@app/database/bot-info/bot-difficulty';
import { ActionCreatorService } from '@app/game/game-logic/actions/action-creator/action-creator.service';
import { BotCalculatorService } from '@app/game/game-logic/player/bot-calculator/bot-calculator.service';
import { EasyBotBrain } from '@app/game/game-logic/player/bot/bot-brain/easy-bot-brain/easy-bot-brain';
import { HardBotBrain } from '@app/game/game-logic/player/bot/bot-brain/hard-bot-brain/hard-bot';
import { BotDictionaryService } from '@app/game/game-logic/validator/dictionary/bot-dictionary/bot-dictionary';
import { WordSearcher } from '@app/game/game-logic/validator/word-search/word-searcher.service';
import { Service } from 'typedi';

@Service()
export class BotManager {
    hardBot: HardBotBrain;
    easyBot: EasyBotBrain;

    constructor(
        private botDictionaryService: BotDictionaryService,
        private botCalculatorService: BotCalculatorService,
        private wordSearcher: WordSearcher,
        // private botMessage: BotMessagesService,
        private actionFactory: ActionCreatorService,
    ) {
        this.hardBot = new HardBotBrain(
            this.botDictionaryService,
            this.botCalculatorService,
            this.wordSearcher,
            // this.botMessage,
            this.actionFactory,
            BotDifficulty.Expert,
        );
        this.easyBot = new EasyBotBrain(
            this.botDictionaryService,
            this.botCalculatorService,
            this.wordSearcher,
            // this.botMessage,
            this.actionFactory,
            BotDifficulty.Easy,
        );
    }
}
