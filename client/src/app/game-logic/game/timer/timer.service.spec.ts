/* eslint-disable @typescript-eslint/no-magic-numbers */
import { fakeAsync, TestBed } from '@angular/core/testing';
import { first } from 'rxjs/operators';
import { TimerService } from './timer.service';

describe('TimerService', () => {
    let service: TimerService;

    beforeEach(() => {
        TestBed.configureTestingModule({});
        service = TestBed.inject(TimerService);
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    it('should return undefined percentage when the timer is not started', (done) => {
        service.timeLeftPercentage$.pipe(first()).subscribe((val) => {
            expect(val).toBeUndefined();
            done();
        });
    });

    it('should return the correct percentage when the timer is started', fakeAsync(() => {
        const time = 4000;
        let percentage: number | undefined;
        service.timeLeftPercentage$.subscribe((val) => {
            percentage = val;
        });
        service.start(time);
        service.timeLeftSubject.next(time);

        expect(percentage).toBe(1);
        service.timeLeftSubject.next(time);

        service.timeLeftSubject.next((time / 4) * 3);
        expect(percentage).toBe(0.75);

        service.timeLeftSubject.next(time / 2);
        expect(percentage).toBe(0.5);

        service.timeLeftSubject.next(time / 4);
        expect(percentage).toBe(0.25);

        service.timeLeftSubject.next(0);
        expect(percentage).toBe(0);
    }));
});
