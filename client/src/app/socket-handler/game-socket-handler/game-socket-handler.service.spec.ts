/* eslint-disable dot-notation */
/* eslint-disable @typescript-eslint/no-explicit-any */
import { TestBed } from '@angular/core/testing';
import { GameState } from '@app/game-logic/game/games/online-game/game-state';
import { SocketMock } from '@app/game-logic/socket-mock';
import { GameSocketHandlerService } from '@app/socket-handler/game-socket-handler/game-socket-handler.service';
import { OnlineAction, OnlineActionType } from '@app/socket-handler/interfaces/online-action.interface';
import { take } from 'rxjs/operators';

describe('GameSocketHandlerService', () => {
    let service: GameSocketHandlerService;
    const gameToken = '1';
    beforeEach(() => {
        TestBed.configureTestingModule({});
        service = TestBed.inject(GameSocketHandlerService);
        service['connectToSocket'] = jasmine.createSpy().and.returnValue(new SocketMock());
        service['connectToSocket']();
        service.joinGame(gameToken);
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    it('joinGame should throw error if already connected', () => {
        expect(() => {
            service.joinGame(gameToken);
        }).toThrowError();
    });

    it('on gameState should call receiveGameState', () => {
        const spy = spyOn<any>(service, 'receiveGameState');
        const mockGameState = {};
        (service.socket as any).peerSideEmit('gameState', mockGameState);
        expect(spy).toHaveBeenCalled();
    });

    it('on transitionGameState should call receiveForfeitedGameState', () => {
        const spy = spyOn<any>(service, 'receiveForfeitedGameState');
        const mockGameState = {};
        (service.socket as any).peerSideEmit('transitionGameState', mockGameState);
        expect(spy).toHaveBeenCalled();
    });

    it('on timerStartingTime should call receiveTimerStartingTime', () => {
        const spy = spyOn<any>(service, 'receiveTimerStartingTime');
        const mockTimerStartingTime = 300;
        (service.socket as any).peerSideEmit('timerStartingTime', mockTimerStartingTime);
        expect(spy).toHaveBeenCalled();
    });

    it('receiveTimerStartingTime should set next subject', () => {
        const timerStartingTime = 300;
        service.timerStartingTimes$.subscribe((value: number) => {
            expect(value).toEqual(timerStartingTime);
        });
        service['receiveTimerStartingTime'](timerStartingTime);
    });

    it('receiveTimerUpdate should set next subject', () => {
        const timerUpdate = 300;
        service.timerStartingTimes$.subscribe((value: number) => {
            expect(value).toEqual(timerUpdate);
        });
        service['receiveTimerUpdate'](timerUpdate);
    });

    it('playAction should emit nextAction', () => {
        const spy = spyOn(service.socket, 'emit');
        const onlineAction: OnlineAction = { type: OnlineActionType.Pass };
        service.playAction(onlineAction);
        expect(spy).toHaveBeenCalled();
        (service.socket as unknown) = undefined;
        expect(() => {
            service.playAction(onlineAction);
        }).toThrowError();
    });

    it('playAction should throw if socket is disconected', () => {
        spyOnProperty(service.socket, 'disconnected', 'get').and.returnValue(true);
        const onlineAction: OnlineAction = { type: OnlineActionType.Pass };

        expect(() => {
            service.playAction(onlineAction);
        }).toThrowError();
    });

    it('forfeit should disconnect socket', () => {
        const spy = spyOn(service.socket, 'disconnect');
        service.disconnect();
        expect(spy).toHaveBeenCalled();
        (service.socket as unknown) = undefined;
        expect(() => {
            service.disconnect();
        }).toThrowError();
    });

    it('receiveGameState should set gameStateSubject', () => {
        let gameStateSubject: GameState = {} as GameState;
        service.gameState$.pipe(take(1)).subscribe((value) => {
            gameStateSubject = value;
        });
        const gameState = { isEndOfGame: false } as GameState;
        service['receiveGameState'](gameState);
        expect(gameStateSubject.isEndOfGame).toBeFalse();
    });

    it('should receive disconnected event and handle it properly', (done) => {
        service.disconnectedFromServer$.subscribe(() => {
            expect(true).toBeTrue();
            done();
        });
        (service.socket as any).peerSideEmit('disconnected');
    });

    it('should emit disconnected from server when receiving connect_error', (done) => {
        service.disconnectedFromServer$.subscribe(() => {
            expect(true).toBeTrue();
            done();
        });
        (service.socket as any).peerSideEmit('connect_error');
    });
});
