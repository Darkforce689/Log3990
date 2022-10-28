import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { MessagesService } from '@app/chat/services/messages/messages.service';
import { ExchangeLetter } from '@app/game-logic/actions/exchange-letter';
import { DEFAULT_TIME_PER_TURN } from '@app/game-logic/constants';
import { BoardService } from '@app/game-logic/game/board/board.service';
import { Letter } from '@app/game-logic/game/board/letter.interface';
import { MockGame } from '@app/game-logic/game/games/mock-game';
import { TimerService } from '@app/game-logic/game/timer/timer.service';
import { Player } from '@app/game-logic/player/player';

describe('ExchangeLetter', () => {
    let game: MockGame;
    const player: Player = new Player('Tim');
    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
        });
        const messageService = TestBed.inject(MessagesService);
        const timerService = TestBed.inject(TimerService);
        const boardService = TestBed.inject(BoardService);
        game = new MockGame(DEFAULT_TIME_PER_TURN, timerService, boardService, messageService);
        game.players[0] = player;
        game.start();
    });

    it('should create an instance', () => {
        const letters: Letter[] = [{ char: 'A', value: 1 }];
        expect(new ExchangeLetter(new Player('Tim'), letters)).toBeTruthy();
    });
});
