import { BotDifficulty } from '@app/database/bot-info/bot-difficulty';
import { BotInfoService } from '@app/database/bot-info/bot-info.service';
import { GameActionNotifierService } from '@app/game/game-action-notifier/game-action-notifier.service';
import { Action } from '@app/game/game-logic/actions/action';
import { ActionCreatorService } from '@app/game/game-logic/actions/action-creator/action-creator.service';
import { TIME_BEFORE_PASS, TIME_BEFORE_PICKING_ACTION, TIME_BUFFER_BEFORE_ACTION } from '@app/game/game-logic/constants';
import { ServerGame } from '@app/game/game-logic/game/server-game';
import { ValidWord } from '@app/game/game-logic/interface/valid-word';
import { BotManager } from '@app/game/game-logic/player/bot/bot-manager/bot-manager.service';
import { Player } from '@app/game/game-logic/player/player';
import { getRandomInt } from '@app/game/game-logic/utils';
import { ServerLogger } from '@app/logger/logger';
import { BehaviorSubject, takeUntil, timer } from 'rxjs';

export class BotPlayer extends Player {
    timesUp: boolean;
    validWordList: ValidWord[];
    private chosenAction$ = new BehaviorSubject<Action | undefined>(undefined);

    constructor(
        protected botInfoService: BotInfoService,
        protected botManager: BotManager,
        protected botDifficulty: BotDifficulty,
        protected gameActionNotifier: GameActionNotifierService,
        protected actionCreator: ActionCreatorService,
    ) {
        super('BotPlaceholderName');
    }

    generateAction(game: ServerGame): void {
        const bot = this.botDifficulty === BotDifficulty.Easy ? this.botManager.easyBot : this.botManager.hardBot;
        this.startTimerAction(game);
        this.timesUp = false;
        timer(TIME_BUFFER_BEFORE_ACTION).subscribe(() => {
            try {
                const action = bot.actionPicker(this, game);
                this.chooseAction(action);
            } catch (error) {
                ServerLogger.logError(error);
            }
        });
    }

    chooseAction(action: Action) {
        this.chosenAction$.next(action);
        this.chosenAction$.complete();
    }

    startTimerAction(game: ServerGame) {
        const timerPass = timer(TIME_BEFORE_PASS);
        timerPass.pipe(takeUntil(this.action$)).subscribe(() => {
            this.timesUp = true;
            const action = this.actionCreator.createPassTurn(this);
            this.playAction(action, game);
        });
        timer(TIME_BEFORE_PICKING_ACTION).subscribe(() => {
            const action = this.chosenAction$.value;
            if (action) {
                this.playAction(action, game);
                return;
            }
            this.chosenAction$.pipe(takeUntil(timerPass)).subscribe((chosenAction) => {
                if (chosenAction !== undefined) {
                    this.playAction(chosenAction, game);
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

    private playAction(action: Action, game: ServerGame) {
        const playerNames = game.players.map((player) => player.name);
        this.gameActionNotifier.notify(action, playerNames, game.gameToken);
        this.action$.next(action);
    }
}
