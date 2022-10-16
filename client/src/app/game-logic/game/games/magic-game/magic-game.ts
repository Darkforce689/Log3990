import { OnlineActionCompilerService } from '@app/game-logic/actions/online-actions/online-action-compiler.service';
import { BoardService } from '@app/game-logic/game/board/board.service';
import { OnlineGame } from '@app/game-logic/game/games/online-game/online-game';
import { TimerService } from '@app/game-logic/game/timer/timer.service';
import { AccountService } from '@app/services/account.service';
import { GameSocketHandlerService } from '@app/socket-handler/game-socket-handler/game-socket-handler.service';
import { IMagicCard, MagicGameState } from '@app/game-logic/game/games/online-game/game-state';

export class MagicOnlineGame extends OnlineGame {
    drawableMagicCards: IMagicCard[];
    drawnMagicCards: IMagicCard[][];

    constructor(
        public gameToken: string,
        public timePerTurn: number,
        timer: TimerService,
        onlineSocket: GameSocketHandlerService,
        boardService: BoardService,
        onlineActionCompiler: OnlineActionCompilerService,
        accountService: AccountService,
    ) {
        super(gameToken, timePerTurn, timer, onlineSocket, boardService, onlineActionCompiler, accountService);
    }

    protected updateClient(gameState: MagicGameState): void {
        super.updateClient(gameState);
        this.updateDrawnMagicCards(gameState);
    }

    private updateDrawnMagicCards(gameState: MagicGameState): void {
        this.drawnMagicCards = gameState.drawnMagicCards;
    }
}
