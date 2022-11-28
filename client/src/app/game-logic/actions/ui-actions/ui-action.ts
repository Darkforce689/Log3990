import { Action } from '@app/game-logic/actions/action';
import { WheelRoll } from '@app/game-logic/interfaces/ui-input';

export abstract class UIAction {
    concernedIndexes: Set<number>;
    abstract get canBeCreated(): boolean;
    abstract receiveRightClick(args?: number): void;
    abstract receiveLeftClick(args: unknown): void;
    abstract receiveKey(key: string): void;
    abstract receiveRoll(args?: WheelRoll): void;
    abstract receiveHoldReleased(args: number, dropPoint: { x: number; y: number }, selectedChar: string | undefined): void;
    abstract create(): Action | null;
    abstract destroy(): void;
}
