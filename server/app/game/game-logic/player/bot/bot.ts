import { Vec2 } from '@app/classes/vec2';
import { BotDifficulty } from '@app/database/bot-info/bot-difficulty';
import { Action } from '@app/game/game-logic/actions/action';
import { ActionCreatorService } from '@app/game/game-logic/actions/action-creator/action-creator.service';
import { LetterCreator } from '@app/game/game-logic/board/letter-creator';
import { Tile } from '@app/game/game-logic/board/tile';
import { MIDDLE_OF_BOARD, TIME_BEFORE_PASS, TIME_BEFORE_PICKING_ACTION, TIME_BUFFER_BEFORE_ACTION } from '@app/game/game-logic/constants';
import { ServerGame } from '@app/game/game-logic/game/server-game';
import { BotCalculatorService } from '@app/game/game-logic/player/bot-calculator/bot-calculator.service';
import { BotMessagesService } from '@app/game/game-logic/player/bot-message/bot-messages.service';
import { BotCrawler } from '@app/game/game-logic/player/bot/bot-crawler';
import { HORIZONTAL, ValidWord } from '@app/game/game-logic/player/bot/valid-word';
import { Player } from '@app/game/game-logic/player/player';
import { DictionaryService } from '@app/game/game-logic/validator/dictionary/dictionary.service';
import { WordSearcher } from '@app/game/game-logic/validator/word-search/word-searcher.service';
import { BehaviorSubject, takeUntil, timer } from 'rxjs';

export abstract class BotBrain {
    botNames: string[] = [];
    letterCreator = new LetterCreator();
    validWordList: ValidWord[];
    botCrawler: BotCrawler;
    timesUp: boolean;
    private chosenAction$ = new BehaviorSubject<Action | undefined>(undefined);

    constructor(
        // private boardService: BoardService,
        private dictionaryService: DictionaryService,
        protected botCalculatorService: BotCalculatorService,
        protected wordValidator: WordSearcher,
        protected botMessage: BotMessagesService,
        // protected gameInfo: GameInfoService,
        // protected commandExecuter: CommandExecuterService,
        protected actionCreator: ActionCreatorService,
        // protected botHttpService: BotHttpService,
        protected botType: BotDifficulty,
    ) {
        this.validWordList = [];
        this.botCrawler = new BotCrawler(this, this.dictionaryService, this.botCalculatorService, this.wordValidator);
    }

    chooseAction(action: Action) {
        this.chosenAction$.next(action);
        this.chosenAction$.complete();
    }

    startTimerAction(player: Player) {
        const timerPass = timer(TIME_BEFORE_PASS);
        timerPass.pipe(takeUntil(player.action$)).subscribe(() => {
            this.timesUp = true;
            this.botMessage.sendAction(this.actionCreator.createPassTurn(player));
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

    getRandomInt(max: number, min: number = 0) {
        return Math.floor(Math.random() * (max - min) + min);
    }

    generateAction(player: Player, game: ServerGame) {
        this.startTimerAction(player);
        this.timesUp = false;
        timer(TIME_BUFFER_BEFORE_ACTION).subscribe(() => {
            const action = this.actionPicker(player, game);
            this.chooseAction(action);
        });
    }

    protected bruteForceStart(game: ServerGame, player: Player): ValidWord[] {
        const grid: Tile[][] = game.board.grid;
        const startingX = 0;
        const startingY = 0;
        const startingPosition: Vec2 = { x: startingX, y: startingY };
        const startingDirection = HORIZONTAL;
        this.validWordList = [];
        const letterInMiddleBox = grid[MIDDLE_OF_BOARD][MIDDLE_OF_BOARD].letterObject.char;

        if (letterInMiddleBox === ' ') {
            this.botCrawler.botFirstTurn(player, game);
            return this.validWordList;
        }
        this.botCrawler.boardCrawler(startingPosition, game, player, startingDirection);
        return this.validWordList;
    }

    protected abstract actionPicker(player: Player, game: ServerGame): Action;
}
