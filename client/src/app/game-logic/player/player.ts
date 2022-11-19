import { Action } from '@app/game-logic/actions/action';
import { RACK_LETTER_COUNT } from '@app/game-logic/constants';
import { Letter } from '@app/game-logic/game/board/letter.interface';
import { SyncState } from '@app/game-logic/game/games/online-game/game-state';
import { Subject } from 'rxjs';

export class Player {
    static defaultName = 'QWERTY';
    action$: Subject<Action> = new Subject();
    syncronisation$: Subject<SyncState> = new Subject();

    points: number = 0;
    isActive: boolean;
    letterRack: Letter[] = [];

    constructor(public name = Player.defaultName) {}

    play(action: Action) {
        this.action$.next(action);
    }

    syncronisation(sync: SyncState) {
        this.syncronisation$.next(sync);
    }

    get isLetterRackEmpty(): boolean {
        return this.letterRack.length === 0;
    }

    get isLetterRackFull(): boolean {
        return this.letterRack.length === RACK_LETTER_COUNT;
    }
}
