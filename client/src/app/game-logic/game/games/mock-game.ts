import { MAX_CONSECUTIVE_PASS } from '@app/game-logic/constants';
import { Board } from '@app/game-logic/game/board/board';
import { BoardService } from '@app/game-logic/game/board/board.service';
import { Game } from '@app/game-logic/game/games/game';
import { TimerService } from '@app/game-logic/game/timer/timer.service';
import { MessagesService } from '@app/game-logic/messages/messages.service';
import { Player } from '@app/game-logic/player/player';
import { Observable, Subject } from 'rxjs';

export class MockGame extends Game {
    static readonly maxConsecutivePass = MAX_CONSECUTIVE_PASS;
    otherPlayer: Player = new Player('otherPlayer');
    activePlayer: Player = new Player('ActivePlayer');
    players: Player[];
    board: Board;
    timePerTurn: number;
    timer: TimerService;
    message: MessagesService;

    gameToken: string = '';
    activePlayerIndex: number = 0;
    lettersRemaining: number = 0;
    hasGameEnded: boolean = false;

    protected isEndOfGameSubject = new Subject<void>();
    protected endTurnSubject = new Subject<void>();

    get endTurn$(): Observable<void> {
        return this.endTurnSubject;
    }
    get isEndOfGame$(): Observable<void> {
        return this.isEndOfGameSubject;
    }

    constructor(timePerTurn: number, timer: TimerService, boardService: BoardService, message: MessagesService) {
        super();
        this.players = [this.activePlayer, this.otherPlayer];
        this.board = boardService.board;
        this.timePerTurn = timePerTurn;
        this.timer = timer;
        this.message = message;
    }

    start() {
        return;
    }

    stop() {
        return;
    }

    isEndOfGame(): boolean {
        return this.hasGameEnded;
    }
    getNumberOfLettersRemaining(): number {
        return this.lettersRemaining;
    }
    getWinner(): Player[] {
        return [this.otherPlayer];
    }
    getActivePlayer() {
        return this.activePlayer;
    }
}
