import { TimerControls } from '@app/game/game-logic/timer/timer-controls.enum';
import { TimerGameControl, TimerTimeLeft } from '@app/game/game-logic/timer/timer-game-control.interface';
import { Observable, Subject } from 'rxjs';
import { Service } from 'typedi';

@Service()
export class TimerController {
    private timerControlSubject = new Subject<TimerGameControl>();
    private timerTimeUpdateSubject = new Subject<TimerTimeLeft>();
    get timerControl$(): Observable<TimerGameControl> {
        return this.timerControlSubject;
    }
    get timerTimeUpdate$(): Observable<TimerTimeLeft> {
        return this.timerTimeUpdateSubject;
    }

    startClientTimers(gameToken: string, initialTime: number) {
        const timerGameControl: TimerGameControl = {
            gameToken,
            control: TimerControls.Start,
            initialTime,
        };
        this.timerControlSubject.next(timerGameControl);
    }

    updateClientTimers(gameToken: string, timeLeft: number) {
        const timerTimeLeft: TimerTimeLeft = {
            gameToken,
            timeLeft,
        };
        this.timerTimeUpdateSubject.next(timerTimeLeft);
    }

    stopClientTimers(gameToken: string) {
        const timerGameControl: TimerGameControl = {
            gameToken,
            control: TimerControls.Stop,
        };
        this.timerControlSubject.next(timerGameControl);
    }
}
