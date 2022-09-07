import { BotDifficulty } from '@app/database/bot-info/bot-difficulty';
import { ActionCreatorService } from '@app/game/game-logic/actions/action-creator/action-creator.service';
import { BotCalculatorService } from '@app/game/game-logic/player/bot-calculator/bot-calculator.service';
import { EasyBotBrain } from '@app/game/game-logic/player/bot/easy-bot';
import { HardBotBrain } from '@app/game/game-logic/player/bot/hard-bot';
import { BotDictionaryService } from '@app/game/game-logic/validator/dictionary/bot-dictionnary';
import { WordSearcher } from '@app/game/game-logic/validator/word-search/word-searcher.service';
import { Service } from 'typedi';

@Service()
export class BotManager {
    hardBot: HardBotBrain;
    easyBot: EasyBotBrain;

    constructor(
        // private boardService: BoardService,
        private botDictionaryService: BotDictionaryService,
        private botCalculatorService: BotCalculatorService,
        private wordSearcher: WordSearcher,
        // private botMessage: BotMessagesService,
        // private gameInfo: GameInfoService,
        // private commandExecuter: CommandExecuterService,
        private actionFactory: ActionCreatorService, // private botHttpService: BotHttpService,
    ) {
        this.hardBot = new HardBotBrain(
            // this.boardService,
            this.botDictionaryService,
            this.botCalculatorService,
            this.wordSearcher,
            // this.botMessage,
            // this.gameInfo,
            // this.commandExecuter,
            this.actionFactory,
            // this.botHttpService,
            BotDifficulty.Expert,
        );
        this.easyBot = new EasyBotBrain(
            // this.boardService,
            this.botDictionaryService,
            this.botCalculatorService,
            this.wordSearcher,
            // this.botMessage,
            // this.gameInfo,
            // this.commandExecuter,
            this.actionFactory,
            // this.botHttpService,
            BotDifficulty.Easy,
        );
    }
}
