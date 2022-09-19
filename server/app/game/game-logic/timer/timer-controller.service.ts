import { TimerStartingTime, TimerTimeLeft } from '@app/game/game-logic/timer/timer-game-control.interface';
import { Observable, Subject } from 'rxjs';
import { Service } from 'typedi';

@Service()
export class TimerController {
    private timerStartingTime = new Subject<TimerStartingTime>();
    private timerTimeUpdateSubject = new Subject<TimerTimeLeft>();

    get timerStartingTime$(): Observable<TimerStartingTime> {
        return this.timerStartingTime;
    }

    get timerTimeUpdate$(): Observable<TimerTimeLeft> {
        return this.timerTimeUpdateSubject;
    }

    startClientTimers(gameToken: string, initialTime: number) {
        const timerGameControl: TimerStartingTime = {
            gameToken,
            initialTime,
        };
        this.timerStartingTime.next(timerGameControl);
    }

    updateClientTimers(gameToken: string, timeLeft: number) {
        const timerTimeLeft: TimerTimeLeft = {
            gameToken,
            timeLeft,
        };
        this.timerTimeUpdateSubject.next(timerTimeLeft);
    }
}
