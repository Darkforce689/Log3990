import { EventEmitter, Injectable, Output } from '@angular/core';
import { Board } from '@app/game-logic/game/board/board';

@Injectable({
    providedIn: 'root',
})
export class BoardService {
    @Output() resetIndexEvent = new EventEmitter();
    board: Board = new Board(false);
}
