import { AfterViewInit, Component, ElementRef, Inject, OnInit, ViewChild } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSourcePageEvent } from '@angular/material/table';
import { GameStateHistory } from '@app/account/interfaces/game-history.interface';
import { NOT_FOUND } from '@app/game-logic/constants';
import { Board } from '@app/game-logic/game/board/board';
import { GameState, LightPlayer, MagicGameState } from '@app/game-logic/game/games/online-game/game-state';
import { CanvasDrawer } from '@app/pages/game-page/board/canvas-drawer';

@Component({
    selector: 'app-replay',
    templateUrl: './replay.component.html',
    styleUrls: ['./replay.component.scss'],
})
export class ReplayComponent implements OnInit, AfterViewInit {
    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild('ScrabbleBoard') scrabbleBoard: ElementRef;
    @ViewChild('gridCanvas') private canvas!: ElementRef<HTMLCanvasElement>;
    currentState: GameState | MagicGameState;
    board: Board = new Board();
    canvasDrawer: CanvasDrawer;
    canvasContext: CanvasRenderingContext2D;
    canvasElement: HTMLElement | null;
    constructor(@Inject(MAT_DIALOG_DATA) public data: { gameStates: GameStateHistory[]; userIndex: number }) {}

    ngOnInit(): void {
        this.setState(0);
        this.currentState.activePlayerIndex = -1;
    }

    ngAfterViewInit(): void {
        this.setUpCanvas();
        this.updateCanvas();
    }

    onPageChange(page: MatTableDataSourcePageEvent) {
        const index = page.pageIndex;
        this.setState(index);
        this.updateCanvas();
    }

    setState(index: number) {
        this.currentState = this.data.gameStates[index].gameState;
    }

    setUpCanvas() {
        this.canvasContext = this.canvas.nativeElement.getContext('2d') as CanvasRenderingContext2D;
        this.canvasElement = document.getElementById('canvas');
        if (this.canvasElement) {
            if (this.canvasElement) {
                this.canvasElement.setAttribute('width', this.canvasElement.clientWidth.toString());
                this.canvasElement.setAttribute('height', this.canvasElement.clientWidth.toString());
                this.canvasDrawer = new CanvasDrawer(this.canvasContext, this.canvasElement.clientWidth, this.canvasElement.clientHeight);
            }
        }
    }

    updateCanvas() {
        this.board.grid = this.currentState.grid;
        this.canvasDrawer.drawGrid(this.board);
    }

    get isEndOfGame() {
        return this.currentState.isEndOfGame;
    }

    get numberOfLettersRemaining() {
        return this.currentState.lettersRemaining;
    }

    get playersInOrder(): LightPlayer[] {
        let orderedPlayers: LightPlayer[] = [...this.currentState.players];
        const userIndex = this.data.userIndex;
        const left = orderedPlayers.slice(0, userIndex);
        const right = orderedPlayers.slice(userIndex, orderedPlayers.length);
        orderedPlayers = right.concat(left);
        return orderedPlayers;
    }

    get leftPlayers() {
        return this.playersInOrder.slice(0, Math.ceil(this.playersInOrder.length / 2));
    }

    get rightPlayers() {
        return this.playersInOrder.slice(Math.ceil(this.playersInOrder.length / 2), this.playersInOrder.length);
    }

    get leftPlayersMagicCard() {
        return this.drawnMagicCards.slice(0, Math.ceil(this.playersInOrder.length / 2));
    }

    get rightPlayersMagicCard() {
        return this.drawnMagicCards.slice(Math.ceil(this.playersInOrder.length / 2), this.playersInOrder.length);
    }

    get activePlayer() {
        if (this.currentState.activePlayerIndex === NOT_FOUND) {
            return undefined;
        }
        const nPlayers = this.currentState.players.length;
        const index = (this.currentState.activePlayerIndex + nPlayers) % nPlayers;
        return this.playersInOrder[index];
    }

    get drawnMagicCards() {
        return (this.currentState as MagicGameState).drawnMagicCards;
    }
}
