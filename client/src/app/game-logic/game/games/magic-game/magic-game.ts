import { MagicCard } from '@app/game-logic/actions/magic-card/magic-card';
import { OnlineActionCompilerService } from '@app/game-logic/actions/online-actions/online-action-compiler.service';
import { BoardService } from '@app/game-logic/game/board/board.service';
import { OnlineGame } from '@app/game-logic/game/games/online-game/online-game';
import { TimerService } from '@app/game-logic/game/timer/timer.service';
import { GameSocketHandlerService } from '@app/socket-handler/game-socket-handler/game-socket-handler.service';

export class MagicOnlineGame extends OnlineGame {
    drawableMagicCards: MagicCard[];

    constructor(
        public gameToken: string,
        public timePerTurn: number,
        public userName: string,
        timer: TimerService,
        onlineSocket: GameSocketHandlerService,
        boardService: BoardService,
        onlineActionCompiler: OnlineActionCompilerService,
    ) {
        super(gameToken, timePerTurn, userName, timer, onlineSocket, boardService, onlineActionCompiler);
    }
}
