import { TimerController } from '@app/game/game-logic/timer/timer-controller.service';
import { TimerStartingTime, TimerTimeLeft } from '@app/game/game-logic/timer/timer-game-control.interface';
import { expect } from 'chai';

describe('TimerController', () => {
    let service: TimerController;
    beforeEach(() => {
        service = new TimerController();
    });

    it('should start client timers with initial time', () => {
        const gameToken = '1';
        const initialTime = 300;
        const startingTime: TimerStartingTime = {
            gameToken,
            initialTime,
        };
        service.timerStartingTime$.subscribe((receivedStartingTime) => {
            expect(receivedStartingTime).to.be.deep.equal(startingTime);
        });
        service.startClientTimers(gameToken, initialTime);
    });

    it('should send time', () => {
        const gameToken = '1';
        const timeLeft = 300;
        const timerTimeLeft: TimerTimeLeft = {
            gameToken,
            timeLeft,
        };
        service.timerTimeUpdate$.subscribe((receivedTime) => {
            expect(receivedTime).to.be.deep.equal(timerTimeLeft);
        });
        service.startClientTimers(gameToken, timeLeft);
    });

    //  it('should stop client timers', () => {
    //      const gameToken = '1';
    //      const control: TimerGameControl = {
    //          gameToken,
    //          control: TimerControls.Stop,
    //      };
    //      service.timerControl$.subscribe((receivedControl) => {
    //          expect(receivedControl).to.be.deep.equal(control);
    //      });
    //      service.stopClientTimers(gameToken);
    //  });
});
