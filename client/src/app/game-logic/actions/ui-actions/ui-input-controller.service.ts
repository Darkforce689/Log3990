import { Injectable, Output } from '@angular/core';
import { Action } from '@app/game-logic/actions/action';
import { ExchangeHorse } from '@app/game-logic/actions/magic-card/magic-card-exchange-horse';
import { ExchangeHorseAll } from '@app/game-logic/actions/magic-card/magic-card-exchange-horse-all';
import { ExchangeALetter } from '@app/game-logic/actions/magic-card/magic-card-exchange-letter';
import { ExtraTurn } from '@app/game-logic/actions/magic-card/magic-card-extra-turn';
import { PlaceBonus } from '@app/game-logic/actions/magic-card/magic-card-place-bonus';
import { ReduceTimer } from '@app/game-logic/actions/magic-card/magic-card-reduce-timer';
import { SkipNextTurn } from '@app/game-logic/actions/magic-card/magic-card-skip-next-turn';
import { SplitPoints } from '@app/game-logic/actions/magic-card/magic-card-split-points';
import { PassTurn } from '@app/game-logic/actions/pass-turn';
import { UIAction } from '@app/game-logic/actions/ui-actions/ui-action';
import { UIDragAndDrop } from '@app/game-logic/actions/ui-actions/ui-drag-and-drop';
import { UIExchange } from '@app/game-logic/actions/ui-actions/ui-exchange';
import { UIMove } from '@app/game-logic/actions/ui-actions/ui-move';
import { UIPlace } from '@app/game-logic/actions/ui-actions/ui-place';
import { LetterPlacement } from '@app/game-logic/actions/ui-actions/ui-place-interface';
import { ENTER, ESCAPE } from '@app/game-logic/constants';
import { BoardService } from '@app/game-logic/game/board/board.service';
import { GameInfoService } from '@app/game-logic/game/game-info/game-info.service';
import { SyncState } from '@app/game-logic/game/games/online-game/game-state';
import { InputComponent, InputType, UIInput, WheelRoll } from '@app/game-logic/interfaces/ui-input';
import { Player } from '@app/game-logic/player/player';
import { Observable, Subject } from 'rxjs';

@Injectable({
    providedIn: 'root',
})
export class UIInputControllerService {
    static defaultComponent = InputComponent.Horse;
    @Output() dropEventSubject = new Subject<UIInput>();
    get dropEvent$(): Observable<UIInput> {
        return this.dropEventSubject;
    }
    @Output() moveEventSubject = new Subject<UIInput>();
    get moveEvent$(): Observable<UIInput> {
        return this.moveEventSubject;
    }
    @Output() moveFeedbackSubject = new Subject<boolean>();
    get moveFeedback$(): Observable<boolean> {
        return this.moveFeedbackSubject;
    }
    @Output() dropFeedbackSubject = new Subject<{ left: number; top: number; index: number; x: number; y: number }>();
    get dropFeedback$(): Observable<{ left: number; top: number; index: number; x: number; y: number }> {
        return this.dropFeedbackSubject;
    }
    @Output() resetIndexSubject = new Subject<void>();
    get resetIndex$(): Observable<void> {
        return this.resetIndexSubject;
    }

    activeComponent = InputComponent.Chatbox;
    activeAction: UIAction | null = null;
    lastPositions: { x: number; y: number }[] = [];

    get canBeExecuted(): boolean {
        return this.activeAction ? this.activeAction.canBeCreated : false;
    }

    constructor(private info: GameInfoService, private boardService: BoardService) {
        this.info.endTurn$?.subscribe(() => {
            if (this.activeAction instanceof UIPlace || this.activeAction instanceof UIDragAndDrop) {
                this.discardAction();
            }
        });
        this.info.endOfGame?.subscribe(() => {
            this.receiveResetIndexes();
        });
        this.boardService.resetIndexEvent.subscribe(() => {
            this.receiveResetIndexes();
        });
    }

    receive(input: UIInput) {
        if (this.info.isEndOfGame) {
            return;
        }
        this.processInput(input);
    }

    receiveDrop(input: UIInput) {
        this.dropEventSubject.next(input);
    }

    receiveMove(input: UIInput) {
        this.moveEventSubject.next(input);
    }

    receiveMoveFeedback(canPlace: boolean) {
        this.moveFeedbackSubject.next(canPlace);
    }

    receiveDropFeedback(event: { left: number; top: number; index: number; x: number; y: number }) {
        this.dropFeedbackSubject.next(event);
    }

    receiveResetIndexes() {
        this.resetIndexSubject.next();
    }

    cancel() {
        this.discardAction();
        this.activeComponent = InputComponent.Outside;
    }

    confirm() {
        if (!this.activeAction || !this.canBeExecuted) {
            return;
        }
        const newAction: Action | null = this.activeAction.create();
        if (!newAction) {
            this.cancel();
            return;
        }
        this.discardAction();
        this.sendAction(newAction);
        this.activeComponent = InputComponent.Outside;
    }

    pass(player: Player) {
        this.sendAction(new PassTurn(player));
    }

    splitPoints(player: Player) {
        this.sendAction(new SplitPoints(player));
    }

    exchangeHorse(player: Player) {
        this.sendAction(new ExchangeHorse(player));
    }

    exchangeHorseAll(player: Player) {
        this.sendAction(new ExchangeHorseAll(player));
    }

    skipNextTurn(player: Player) {
        this.sendAction(new SkipNextTurn(player));
    }

    extraTurn(player: Player) {
        this.sendAction(new ExtraTurn(player));
    }

    reduceTimer(player: Player) {
        this.sendAction(new ReduceTimer(player));
    }

    exchangeLetter(player: Player) {
        if (!this.activeAction || !this.canBeExecuted) {
            return;
        }
        const newAction: Action | null = this.activeAction.create();
        if (!newAction) {
            return;
        }
        const concernedIndex: number = this.activeAction.concernedIndexes.values().next().value;
        this.discardAction();
        this.sendAction(new ExchangeALetter(player, player.letterRack[concernedIndex]));
        this.activeComponent = InputComponent.Outside;
    }

    placeBonus(player: Player) {
        if (!this.activeAction || !(this.activeAction instanceof UIPlace) || this.activeAction.pointerPosition === null) {
            return;
        }
        const pointerPosition = this.activeAction.pointerPosition;
        this.discardAction();
        this.sendAction(new PlaceBonus(player, pointerPosition));
    }

    replaceOldTempPos(letterPlacement: LetterPlacement) {
        if (!(this.activeAction instanceof UIDragAndDrop)) return;
        this.activeAction.receiveHoldReleased(letterPlacement.rackIndex, { x: letterPlacement.x, y: letterPlacement.y });
    }

    sendSyncState(positions: { x: number; y: number }[]) {
        this.lastPositions = positions;
        this.sendSync({ positions } as SyncState);
    }

    sendContinuousSyncState(positions: { x: number; y: number }[]) {
        this.sendSync({ positions: positions.concat(this.lastPositions) } as SyncState);
    }

    private processInput(input: UIInput) {
        if (
            input.from !== InputComponent.Chatbox &&
            !!this.activeAction &&
            this.activeAction instanceof UIDragAndDrop &&
            this.activeAction.concernedIndexes.size !== 0 &&
            !(input.type === InputType.HoldReleased || input.type === InputType.KeyPress)
        )
            return;
        this.processInputComponent(input);
        this.updateActiveAction(input.type);
        this.processInputType(input);
    }

    private processInputComponent(input: UIInput) {
        if (input.from) {
            this.activeComponent = input.from;
            return;
        }
        if (this.activeComponent === InputComponent.Outside) {
            this.activeComponent = UIInputControllerService.defaultComponent;
        }
    }

    private updateActiveAction(inputType: InputType): void {
        switch (this.activeComponent) {
            case InputComponent.Board:
                if (inputType === InputType.HoldReleased) {
                    if (!(this.activeAction instanceof UIDragAndDrop)) {
                        this.discardAction();
                        this.activeAction = new UIDragAndDrop(this.info, this.boardService, this);
                        return;
                    }
                } else if (inputType === InputType.LeftClick) {
                    if (!(this.activeAction instanceof UIPlace)) {
                        this.discardAction();
                        this.activeAction = new UIPlace(this.info, this.boardService, this);
                        return;
                    }
                }
                break;
            case InputComponent.Horse:
                if (inputType === InputType.RightClick) {
                    if (!(this.activeAction instanceof UIExchange)) {
                        this.discardAction();
                        this.activeAction = new UIExchange(this.info.player);
                        return;
                    }
                } else {
                    if (!(this.activeAction instanceof UIMove)) {
                        this.discardAction();
                        this.activeAction = new UIMove(this.info.player);
                        return;
                    }
                }
                break;
            case InputComponent.Chatbox:
                if (this.activeAction) {
                    this.discardAction();
                    return;
                }
                break;
            case InputComponent.Outside:
                if (this.activeAction) {
                    this.discardAction();
                    this.activeComponent = InputComponent.Outside;
                }
        }
    }

    private processInputType(input: UIInput) {
        switch (input.type) {
            case InputType.LeftClick:
                this.processLeftCLick(input.args);
                break;
            case InputType.RightClick:
                this.processRightCLick(input.args as number);
                break;
            case InputType.KeyPress:
                this.processKeyPress(input.args);
                break;
            case InputType.MouseRoll:
                this.processMouseRoll(input.args as WheelRoll);
                break;
            case InputType.HoldReleased:
                this.processHoldReleased(input.args as number, input.dropPoint as { x: number; y: number });
                break;
            default:
                throw Error('Unresolved input of type ' + input.type);
        }
    }

    private discardAction() {
        if (this.activeAction) {
            this.activeAction.destroy();
        }
        this.activeAction = null;
        this.lastPositions = [];
    }

    private processMouseRoll(args?: WheelRoll) {
        if (this.activeAction) {
            this.activeAction.receiveRoll(args);
        }
    }

    private processKeyPress(args: unknown) {
        const keyPressed = args as string;
        switch (keyPressed) {
            case ESCAPE:
                if (this.activeComponent === InputComponent.Chatbox) {
                    return;
                }
                this.discardAction();
                this.activeComponent = InputComponent.Outside;
                break;
            case ENTER:
                this.confirm();
                break;
            default:
                if (this.activeAction) {
                    this.activeAction.receiveKey(keyPressed);
                    return;
                }
        }
    }

    private processLeftCLick(args: unknown) {
        if (this.activeAction !== null) {
            this.activeAction.receiveLeftClick(args);
            return;
        }
    }

    private processRightCLick(args: number) {
        if (this.activeAction !== null) {
            this.activeAction.receiveRightClick(args);
            return;
        }
    }

    private processHoldReleased(args: number, dropPoint: { x: number; y: number }) {
        if (this.activeAction !== null) {
            this.activeAction.receiveHoldReleased(args, dropPoint);
            return;
        }
    }

    private sendAction(action: Action) {
        if (this.info.player === this.info.activePlayer) {
            this.info.player.action$.next(action);
        }
    }

    private sendSync(sync: SyncState) {
        if (this.info.player === this.info.activePlayer) {
            this.info.player.syncronisation$.next(sync);
        }
    }
}
