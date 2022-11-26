import { BotDifficulty } from '@app/game/game-logic/player/bot/bot-difficulty';
import { GameActionNotifierService } from '@app/game/game-action-notifier/game-action-notifier.service';
import { Action } from '@app/game/game-logic/actions/action';
import { ActionCreatorService } from '@app/game/game-logic/actions/action-creator/action-creator.service';
import { TIME_BEFORE_PASS, TIME_BEFORE_PICKING_ACTION, TIME_BUFFER_BEFORE_ACTION } from '@app/game/game-logic/constants';
import { ServerGame } from '@app/game/game-logic/game/server-game';
import { ValidWord } from '@app/game/game-logic/interface/valid-word';
import { Player } from '@app/game/game-logic/player/player';
import { ServerLogger } from '@app/logger/logger';
import { BehaviorSubject, takeUntil, timer } from 'rxjs';
import { BotLogic } from './bot/bot-logic/bot-logic';

export class BotPlayer extends Player {
    timesUp: boolean;
    validWordList: ValidWord[];
    private chosenAction$ = new BehaviorSubject<Action | undefined>(undefined);

    constructor(
        public name: string,
        protected botLogic: BotLogic,
        protected botDifficulty: BotDifficulty,
        protected gameActionNotifier: GameActionNotifierService,
        protected actionCreator: ActionCreatorService,
    ) {
        super(name);
    }

    generateAction(game: ServerGame): void {
        const bot = this.botLogic;
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

    private playAction(action: Action, game: ServerGame) {
        const playerNames = game.players.map((player) => player.name);
        this.gameActionNotifier.notify(action, playerNames, game.gameToken);
        this.action$.next(action);
    }
}
