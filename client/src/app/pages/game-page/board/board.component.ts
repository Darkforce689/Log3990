import { AfterViewInit, Component, DoCheck, ElementRef, EventEmitter, HostListener, OnDestroy, Output, ViewChild } from '@angular/core';
import { UIDragAndDrop } from '@app/game-logic/actions/ui-actions/ui-drag-and-drop';
import { UIInputControllerService } from '@app/game-logic/actions/ui-actions/ui-input-controller.service';
import { UIPlace } from '@app/game-logic/actions/ui-actions/ui-place';
import { ASCII_CODE, EMPTY_CHAR, NOT_FOUND } from '@app/game-logic/constants';
import { Board } from '@app/game-logic/game/board/board';
import { BoardService } from '@app/game-logic/game/board/board.service';
import { GameInfoService } from '@app/game-logic/game/game-info/game-info.service';
import { InputComponent, InputType, UIInput } from '@app/game-logic/interfaces/ui-input';
import { isInsideOfBoard } from '@app/game-logic/utils';
import { CanvasDrawer } from '@app/pages/game-page/board/canvas-drawer';
import { Subscription } from 'rxjs';

@Component({
    selector: 'app-board',
    templateUrl: './board.component.html',
    styleUrls: ['./board.component.scss'],
})
export class BoardComponent implements AfterViewInit, DoCheck, OnDestroy {
    @ViewChild('ScrabbleBoard') scrabbleBoard: ElementRef;
    @Output() clickTile = new EventEmitter();
    @Output() moveFeedback = new EventEmitter();
    @Output() dropFeedback = new EventEmitter();

    @ViewChild('gridCanvas') private canvas!: ElementRef<HTMLCanvasElement>;
    readonly self = InputComponent.Board;
    board: Board;
    canvasDrawer: CanvasDrawer;
    canvasContext: CanvasRenderingContext2D;
    canvasElement: HTMLElement | null;

    canDropHere: boolean = false;
    private dropEvent$$: Subscription;
    private moveEvent$$: Subscription;

    constructor(private boardService: BoardService, private inputController: UIInputControllerService, private info: GameInfoService) {
        this.board = this.boardService.board;
        this.dropEvent$$ = this.inputController.dropEvent$.subscribe((input: UIInput) => {
            this.receiveDrop(input);
        });
        this.moveEvent$$ = this.inputController.moveEvent$.subscribe((input: UIInput) => {
            this.receiveMove(input);
        });
    }

    @HostListener('window:resize', ['$event'])
    onResize() {
        this.setupCanvasDrawer();
    }

    ngOnDestroy() {
        this.dropEvent$$.unsubscribe();
        this.moveEvent$$.unsubscribe();
    }

    ngAfterViewInit() {
        this.canvasContext = this.canvas.nativeElement.getContext('2d') as CanvasRenderingContext2D;
        this.canvasElement = document.getElementById('canvas');
        if (this.canvasElement) {
            this.setupCanvasDrawer();
        }
        this.canvasDrawer.drawGrid(this.board);
    }

    ngDoCheck() {
        if (!this.canvasDrawer) {
            return;
        }
        if (this.inputController.activeAction instanceof UIPlace) {
            if (this.inputController.activeAction.pointerPosition) {
                this.canvasDrawer.setIndicator(
                    this.inputController.activeAction.pointerPosition.x,
                    this.inputController.activeAction.pointerPosition.y,
                );
                this.canvasDrawer.setDirection(this.inputController.activeAction.direction);
            }
        } else if (!(this.inputController.activeAction instanceof UIDragAndDrop)) {
            this.canvasDrawer.setIndicator(NOT_FOUND, NOT_FOUND);
        }
        this.canvasDrawer.drawGrid(this.board);
    }

    convertASCIIToChar(code: number): string {
        return String.fromCharCode(ASCII_CODE + code);
    }

    canvasClick(event: MouseEvent): void {
        const pos = this.canvasDrawer.coordToTilePosition(event.offsetX, event.offsetY);
        const input: UIInput = { from: this.self, type: InputType.LeftClick, args: { x: pos.indexI, y: pos.indexJ } };
        this.clickTile.emit(input);
    }

    receiveDrop(input: UIInput) {
        if (!input.dropPoint) return;
        const newInput: UIInput = {
            from: this.self,
            type: InputType.HoldReleased,
            args: input.args,
            dropPoint: this.canDropHere ? this.getTilePosFromDropPoint(input.dropPoint) : { x: NOT_FOUND, y: NOT_FOUND },
        };
        this.clickTile.emit(newInput);
        // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
        this.sendDropFeedBack(newInput.dropPoint!, newInput.args as number);
    }

    receiveMove(input: UIInput) {
        if (!input.dropPoint) return;
        const tilePosition = this.canvasDrawer.coordToTilePosition(
            input.dropPoint.x - this.canvas.nativeElement.offsetLeft,
            input.dropPoint.y - this.canvas.nativeElement.offsetTop,
        );

        if (!isInsideOfBoard(tilePosition.indexI, tilePosition.indexJ)) {
            this.canDropHere = false;
            this.inputController.sendContinuousSyncState([]);
        } else {
            const tile = this.board.grid[tilePosition.indexJ][tilePosition.indexI];
            this.canDropHere =
                !!tile && this.info.player === this.info.activePlayer && (!!tile.letterObject.isTemp || tile.letterObject.char === EMPTY_CHAR);
            if (this.canDropHere) this.inputController.sendContinuousSyncState([{ x: tilePosition.indexI, y: tilePosition.indexJ }]);
            else this.inputController.sendContinuousSyncState([]);
        }

        this.sendMoveFeedback(tilePosition);
    }

    private getTilePosFromDropPoint(dropPoint: { x: number; y: number }): { x: number; y: number } {
        const tilePos = this.canvasDrawer.coordToTilePosition(
            dropPoint.x - this.canvas.nativeElement.offsetLeft,
            dropPoint.y - this.canvas.nativeElement.offsetTop,
        );
        return { x: tilePos.indexI, y: tilePos.indexJ };
    }

    private sendMoveFeedback(pos: { indexI: number; indexJ: number }) {
        this.canvasDrawer.setTemp(pos.indexI, pos.indexJ, this.canDropHere);
        this.moveFeedback.emit(this.canDropHere);
    }

    private sendDropFeedBack(pos: { x: number; y: number }, index: number) {
        this.canvasDrawer.setTemp(NOT_FOUND, NOT_FOUND, false);
        const coord = this.canvasDrawer.tilePositionToCoord(pos.x, pos.y);
        this.dropFeedback.emit({
            left: this.canvas.nativeElement.offsetLeft + coord.x,
            top: this.canvas.nativeElement.offsetTop + coord.y,
            index,
            x: pos.x,
            y: pos.y,
        });
    }

    private setupCanvasDrawer() {
        if (this.canvasElement) {
            this.canvasElement.setAttribute('width', this.canvasElement.clientWidth.toString());
            this.canvasElement.setAttribute('height', this.canvasElement.clientWidth.toString());
            this.canvasDrawer = new CanvasDrawer(this.canvasContext, this.canvasElement.clientWidth, this.canvasElement.clientHeight);
        }
    }
}
