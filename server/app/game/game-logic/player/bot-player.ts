import { BotType } from '@app/database/bot-info/bot-info';
import { BotInfoService } from '@app/database/bot-info/bot-info.service';
import { ServerGame } from '@app/game/game-logic/game/server-game';
import { BotManager } from '@app/game/game-logic/player/bot/bot-manager.service';
import { Player } from '@app/game/game-logic/player/player';
import { getRandomInt } from '@app/game/game-logic/utils';

export class BotPlayer extends Player {
    constructor(
        opponentName: string,
        // private boardService: BoardService,
        // private dictionaryService: DictionaryService,
        // protected botCalculatorService: BotCalculatorService,
        // protected wordValidator: WordSearcher,
        // protected botMessage: BotMessagesService,
        // protected gameInfo: GameInfoService,
        // protected commandExecuter: CommandExecuterService,
        // protected actionCreator: ActionCreatorService,
        // protected botHttpService: BotHttpService,
        protected botInfoService: BotInfoService,
        protected botType: BotType,
        protected botManager: BotManager,
    ) {
        super('PlaceholderName');
        void this.updateBotName(opponentName);
        // this.validWordList = [];
        // this.botCrawler = new BotCrawler(this, this.dictionaryService, this.botCalculatorService, this.wordValidator);
    }

    generateAction(game: ServerGame): void {
        const bot = this.botType === BotType.Easy ? this.botManager.easyBot : this.botManager.hardBot;
        bot.generateAction(this, game);
    }

    private async updateBotName(opponentName: string): Promise<void> {
        const botNames = await this.getBotNames();
        let generatedName;
        do {
            generatedName = botNames[getRandomInt(botNames.length)];
        } while (generatedName === opponentName);
        this.name = generatedName;
    }

    private async getBotNames(): Promise<string[]> {
        const botInfos = await this.botInfoService.getBotInfoList();
        const filteredBotInfos = botInfos.filter((bot) => bot.type === this.botType);
        const filteredBotInfosNames = filteredBotInfos.map((bot) => bot.name);
        return filteredBotInfosNames;
    }
}
