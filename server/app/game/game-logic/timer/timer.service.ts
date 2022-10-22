import { TIMER_STEP } from '@app/game/game-logic/constants';
import { TimerController } from '@app/game/game-logic/timer/timer-controller.service';
import { BehaviorSubject, Observable, Subject, Subscription, timer } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

export class Timer {
    source: Observable<number>;
    readonly timePerStep: number = TIMER_STEP;
    private end$$: Subscription;
    private timeLeftSubject = new BehaviorSubject<number | undefined>(undefined);

    constructor(private gameToken: string, private timerController: TimerController) {}

    start(interval: number, halfTime: boolean = false) {
        this.emitStartControl(interval);
        const end$: Subject<void> = new Subject();
        const newInterval = halfTime ? interval / 2 : interval;
        const numberOfStep = Math.ceil(newInterval / TIMER_STEP);

        this.timeLeftSubject.next(newInterval);
        this.source = timer(TIMER_STEP, TIMER_STEP);
        this.end$$ = this.source.pipe(takeUntil(end$)).subscribe((step) => {
            const timeLeft = newInterval - (step + 1) * this.timePerStep;
            this.timeLeftSubject.next(timeLeft);
            this.emitTimeUpdate(timeLeft);
            if (step >= numberOfStep - 1) {
                end$.next();
                end$.complete();
            }
        });
        return end$;
    }

    stop() {
        this.end$$.unsubscribe();
        this.source = new Subject();
    }

    private emitStartControl(initialTime: number) {
        this.timerController.startClientTimers(this.gameToken, initialTime);
    }

    private emitTimeUpdate(timeLeft: number) {
        this.timerController.updateClientTimers(this.gameToken, timeLeft);
    }

    get timeLeft$(): Observable<number | undefined> {
        return this.timeLeftSubject;
    }
}
