import { BotType } from '@app/database/bot-info/bot-info';
import { ActionCreatorService } from '@app/game/game-logic/actions/action-creator/action-creator.service';
import { BotCalculatorService } from '@app/game/game-logic/player/bot-calculator/bot-calculator.service';
import { BotMessagesService } from '@app/game/game-logic/player/bot-message/bot-messages.service';
import { EasyBotBrain } from '@app/game/game-logic/player/bot/easy-bot';
import { HardBotBrain } from '@app/game/game-logic/player/bot/hard-bot';
import { DictionaryService } from '@app/game/game-logic/validator/dictionary/dictionary.service';
import { WordSearcher } from '@app/game/game-logic/validator/word-search/word-searcher.service';
import { Service } from 'typedi';

@Service()
export class BotManager {
    hardBot: HardBotBrain;
    easyBot: EasyBotBrain;

    constructor(
        // private boardService: BoardService,
        private dictionaryService: DictionaryService,
        private botCalculatorService: BotCalculatorService,
        private wordSearcher: WordSearcher,
        private botMessage: BotMessagesService,
        // private gameInfo: GameInfoService,
        // private commandExecuter: CommandExecuterService,
        private actionFactory: ActionCreatorService, // private botHttpService: BotHttpService,
    ) {
        this.hardBot = new HardBotBrain(
            // this.boardService,
            this.dictionaryService,
            this.botCalculatorService,
            this.wordSearcher,
            this.botMessage,
            // this.gameInfo,
            // this.commandExecuter,
            this.actionFactory,
            // this.botHttpService,
            BotType.Expert,
        );
        this.easyBot = new EasyBotBrain(
            // this.boardService,
            this.dictionaryService,
            this.botCalculatorService,
            this.wordSearcher,
            this.botMessage,
            // this.gameInfo,
            // this.commandExecuter,
            this.actionFactory,
            // this.botHttpService,
            BotType.Easy,
        );
    }
}
