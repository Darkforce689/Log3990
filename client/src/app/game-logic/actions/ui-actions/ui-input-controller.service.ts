import { Injectable } from '@angular/core';
import { Action } from '@app/game-logic/actions/action';
import { SplitPoints } from '@app/game-logic/actions/magic-card/magic-card-split-points';
import { ExchangeALetter } from '@app/game-logic/actions/magic-card/magic-card-exchange-letter';
import { PassTurn } from '@app/game-logic/actions/pass-turn';
import { UIAction } from '@app/game-logic/actions/ui-actions/ui-action';
import { UIExchange } from '@app/game-logic/actions/ui-actions/ui-exchange';
import { UIMove } from '@app/game-logic/actions/ui-actions/ui-move';
import { UIPlace } from '@app/game-logic/actions/ui-actions/ui-place';
import { ENTER, ESCAPE } from '@app/game-logic/constants';
import { BoardService } from '@app/game-logic/game/board/board.service';
import { GameInfoService } from '@app/game-logic/game/game-info/game-info.service';
import { InputComponent, InputType, UIInput, WheelRoll } from '@app/game-logic/interfaces/ui-input';
import { Player } from '@app/game-logic/player/player';
import { PlaceBonus } from '@app/game-logic/actions/magic-card/magic-card-place-bonus';

@Injectable({
    providedIn: 'root',
})
export class UIInputControllerService {
    static defaultComponent = InputComponent.Horse;
    activeComponent = InputComponent.Chatbox;
    activeAction: UIAction | null = null;

    get canBeExecuted(): boolean {
        return this.activeAction ? this.activeAction.canBeCreated : false;
    }

    constructor(private info: GameInfoService, private boardService: BoardService) {
        this.info.endTurn$?.subscribe(() => {
            if (this.activeAction instanceof UIPlace) {
                this.discardAction();
            }
        });
    }

    receive(input: UIInput) {
        if (this.info.isEndOfGame) {
            return;
        }
        this.processInput(input);
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

    private processInput(input: UIInput) {
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
                if (!(this.activeAction instanceof UIPlace)) {
                    this.discardAction();
                    this.activeAction = new UIPlace(this.info, this.boardService);
                    return;
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
            default:
                throw Error('Unresolved input of type ' + input.type);
        }
    }

    private discardAction() {
        if (this.activeAction) {
            this.activeAction.destroy();
        }
        this.activeAction = null;
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

    private sendAction(action: Action) {
        if (this.info.player === this.info.activePlayer) {
            this.info.player.action$.next(action);
        }
    }
}
