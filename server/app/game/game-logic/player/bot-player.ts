import { BotDifficulty } from '@app/database/bot-info/bot-difficulty';
import { BotInfoService } from '@app/database/bot-info/bot-info.service';
import { ServerGame } from '@app/game/game-logic/game/server-game';
import { BotManager } from '@app/game/game-logic/player/bot/bot-manager.service';
import { Player } from '@app/game/game-logic/player/player';
import { getRandomInt } from '@app/game/game-logic/utils';

export class BotPlayer extends Player {
    constructor(
        opponentNames: string[],
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
        protected botManager: BotManager,
        protected botType: BotDifficulty,
    ) {
        super('PlaceholderName');
        void this.updateBotName(opponentNames);
        // this.validWordList = [];
        // this.botCrawler = new BotCrawler(this, this.dictionaryService, this.botCalculatorService, this.wordValidator);
    }

    generateAction(game: ServerGame): void {
        const bot = this.botType === BotDifficulty.Easy ? this.botManager.easyBot : this.botManager.hardBot;
        bot.generateAction(this, game);
    }

    private async updateBotName(opponentNames: string[]): Promise<void> {
        const botNames: string[] = await this.getBotNames();
        let generatedName: string;
        do {
            generatedName = botNames[getRandomInt(botNames.length)];
        } while (opponentNames.find((opponentName) => opponentName === generatedName));
        this.name = generatedName;
    }

    private async getBotNames(): Promise<string[]> {
        const botInfos = await this.botInfoService.getBotInfoList();
        const filteredBotInfos = botInfos.filter((bot) => bot.type === this.botType);
        const filteredBotInfosNames = filteredBotInfos.map((bot) => bot.name);
        return filteredBotInfosNames;
    }
}
