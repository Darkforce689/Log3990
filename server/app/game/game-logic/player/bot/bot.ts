import { Vec2 } from '@app/classes/vec2';
import { BotDifficulty } from '@app/database/bot-info/bot-difficulty';
import { Action } from '@app/game/game-logic/actions/action';
import { ActionCreatorService } from '@app/game/game-logic/actions/action-creator/action-creator.service';
import { Tile } from '@app/game/game-logic/board/tile';
import { MIDDLE_OF_BOARD } from '@app/game/game-logic/constants';
import { ServerGame } from '@app/game/game-logic/game/server-game';
import { BotCalculatorService } from '@app/game/game-logic/player/bot-calculator/bot-calculator.service';
import { BotPlayer } from '@app/game/game-logic/player/bot-player';
import { BotCrawler } from '@app/game/game-logic/player/bot/bot-crawler';
import { HORIZONTAL, ValidWord } from '@app/game/game-logic/player/bot/valid-word';
import { Player } from '@app/game/game-logic/player/player';
import { BotDictionaryService } from '@app/game/game-logic/validator/dictionary/bot-dictionnary';
import { WordSearcher } from '@app/game/game-logic/validator/word-search/word-searcher.service';

export abstract class BotBrain {
    private validWordList: ValidWord[];
    private botCrawler: BotCrawler;
    // private chosenAction$ = new BehaviorSubject<Action | undefined>(undefined);

    constructor(
        // private boardService: BoardService,
        private botDictionaryService: BotDictionaryService,
        protected botCalculatorService: BotCalculatorService,
        protected wordValidator: WordSearcher,
        protected actionCreator: ActionCreatorService,
        // protected gameInfo: GameInfoService,
        // protected commandExecuter: CommandExecuterService,
        // protected botHttpService: BotHttpService,
        protected botType: BotDifficulty,
    ) {
        this.validWordList = [];
        this.botCrawler = new BotCrawler(this.botDictionaryService, this.botCalculatorService, this.wordValidator);
    }

    getRandomInt(max: number, min: number = 0) {
        return Math.floor(Math.random() * (max - min) + min);
    }

    protected bruteForceStart(game: ServerGame, player: BotPlayer): ValidWord[] {
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

    abstract actionPicker(player: Player, game: ServerGame): Action;
}
