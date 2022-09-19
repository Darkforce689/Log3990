import { Player } from '@app/game-logic/player/player';
import { Observable, Subject } from 'rxjs';

export abstract class Action {
    private endSubject = new Subject<void>();
    get end$(): Observable<void> {
        return this.endSubject;
    }

    constructor(readonly player: Player) {}
}
