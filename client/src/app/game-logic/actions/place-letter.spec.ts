import { TestBed } from '@angular/core/testing';
import { PlaceLetter } from '@app/game-logic/actions/place-letter';
import { BOARD_DIMENSION, DEFAULT_TIME_PER_TURN } from '@app/game-logic/constants';
import { Direction } from '@app/game-logic/direction.enum';
import { BoardService } from '@app/game-logic/game/board/board.service';
import { MockGame } from '@app/game-logic/game/games/mock-game';
import { TimerService } from '@app/game-logic/game/timer/timer.service';
import { MessagesService } from '@app/game-logic/messages/messages.service';
import { Player } from '@app/game-logic/player/player';

describe('PlaceLetter', () => {
    let game: MockGame;
    const player1: Player = new Player('Tim');
    const player2: Player = new Player('Bob');
    const centerPosition = Math.floor(BOARD_DIMENSION / 2);

    beforeEach(() => {
        const messageService = TestBed.inject(MessagesService);
        const timerService = TestBed.inject(TimerService);
        const boardService = TestBed.inject(BoardService);
        game = new MockGame(DEFAULT_TIME_PER_TURN, timerService, boardService, messageService);
        game.players = [player1, player2];
    });

    it('should create an instance', () => {
        const action = new PlaceLetter(player1, '', { x: centerPosition, y: centerPosition, direction: Direction.Vertical });
        expect(action).toBeTruthy();
    });
});
