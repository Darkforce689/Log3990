import { Injectable } from '@angular/core';
import { TIMER_STEP } from '@app/game-logic/constants';
import { BehaviorSubject, Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
    providedIn: 'root',
})
export class TimerService {
    isStarted = false;
    timeLeftSubject = new BehaviorSubject<number | undefined>(undefined);
    readonly timePerStep: number = TIMER_STEP;
    private timePerTurn: number;

    setInitialTimePerTurn(timePerTurn: number) {
        this.timePerTurn = timePerTurn;
        this.timeLeftSubject.next(timePerTurn);
    }

    start(timePerTurn: number) {
        this.isStarted = true;
        this.timePerTurn = timePerTurn;
    }

    get timeLeft$(): Observable<number | undefined> {
        return this.timeLeftSubject;
    }

    get timeLeftPercentage$(): Observable<number | undefined> {
        return this.timeLeftSubject.pipe(
            map((timerLeft: number | undefined): number | undefined => {
                if (timerLeft === undefined || this.timePerTurn === undefined) {
                    return undefined;
                }
                return timerLeft / this.timePerTurn;
            }),
        );
    }
}
