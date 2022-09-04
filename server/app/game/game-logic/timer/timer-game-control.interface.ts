import { TimerControls } from '@app/game/game-logic/timer/timer-controls.enum';

export interface TimerGameControl {
    gameToken: string;
    control: TimerControls;
    initialTime?: number;
}
export interface TimerTimeLeft {
    gameToken: string;
    timeLeft: number;
}
