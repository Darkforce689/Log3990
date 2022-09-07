import { BotDifficulty } from '@app/database/bot-info/bot-difficulty';
import { BotInfoService } from '@app/database/bot-info/bot-info.service';
import { Action } from '@app/game/game-logic/actions/action';
import { ActionCreatorService } from '@app/game/game-logic/actions/action-creator/action-creator.service';
import { TIME_BEFORE_PASS, TIME_BEFORE_PICKING_ACTION, TIME_BUFFER_BEFORE_ACTION } from '@app/game/game-logic/constants';
import { ServerGame } from '@app/game/game-logic/game/server-game';
import { BotMessagesService } from '@app/game/game-logic/player/bot-message/bot-messages.service';
import { BotManager } from '@app/game/game-logic/player/bot/bot-manager.service';
import { Player } from '@app/game/game-logic/player/player';
import { getRandomInt } from '@app/game/game-logic/utils';
import { BehaviorSubject, takeUntil, timer } from 'rxjs';

export class BotPlayer extends Player {
    timesUp: boolean;
    private chosenAction$ = new BehaviorSubject<Action | undefined>(undefined);

    constructor(
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
        protected botDifficulty: BotDifficulty,
        protected botMessage: BotMessagesService,
        protected actionCreator: ActionCreatorService,
    ) {
        super('PlaceholderName');
        // this.validWordList = [];
        // this.botCrawler = new BotCrawler(this, this.dictionaryService, this.botCalculatorService, this.wordValidator);
    }

    generateAction(game: ServerGame): void {
        const bot = this.botDifficulty === BotDifficulty.Easy ? this.botManager.easyBot : this.botManager.hardBot;
        this.startTimerAction();
        this.timesUp = false;
        timer(TIME_BUFFER_BEFORE_ACTION).subscribe(() => {
            const action = bot.actionPicker(this, game);
            this.chooseAction(action);
        });
    }

    chooseAction(action: Action) {
        this.chosenAction$.next(action);
        this.chosenAction$.complete();
    }

    startTimerAction() {
        const timerPass = timer(TIME_BEFORE_PASS);
        timerPass.pipe(takeUntil(this.action$)).subscribe(() => {
            this.timesUp = true;
            this.botMessage.sendAction(this.actionCreator.createPassTurn(this));
        });
        timer(TIME_BEFORE_PICKING_ACTION).subscribe(() => {
            const action = this.chosenAction$.value;
            if (action) {
                this.botMessage.sendAction(action);
                return;
            }
            this.chosenAction$.pipe(takeUntil(timerPass)).subscribe((chosenAction) => {
                if (chosenAction !== undefined) {
                    this.botMessage.sendAction(chosenAction);
                }
            });
        });
    }

    async updateBotName(playerNames: string[]): Promise<void> {
        const botNames: string[] = await this.getBotNames();
        let generatedName: string;
        do {
            generatedName = botNames[getRandomInt(botNames.length)];
        } while (playerNames.find((opponentName) => opponentName === generatedName));
        this.name = generatedName;
    }

    private async getBotNames(): Promise<string[]> {
        const botInfos = await this.botInfoService.getBotInfoList();
        const filteredBotInfos = botInfos.filter((bot) => bot.type === this.botDifficulty);
        const filteredBotInfosNames = filteredBotInfos.map((bot) => bot.name);
        return filteredBotInfosNames;
    }
}
