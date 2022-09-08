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
    private interval: number;

    start(interval: number) {
        this.isStarted = true;
        this.interval = interval;

        return;
    }

    get timeLeft$(): Observable<number | undefined> {
        return this.timeLeftSubject;
    }

    get timeLeftPercentage$(): Observable<number | undefined> {
        return this.timeLeftSubject.pipe(
            map((timerLeft: number | undefined): number | undefined => {
                if (timerLeft === undefined || this.interval === undefined) {
                    return undefined;
                }
                return timerLeft / this.interval;
            }),
        );
    }
}
