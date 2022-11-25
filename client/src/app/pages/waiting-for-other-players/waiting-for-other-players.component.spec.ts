import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { AppMaterialModule } from '@app/modules/material.module';
import { OnlineGameSettings } from '@app/socket-handler/interfaces/game-settings-multi.interface';
import { NewOnlineGameSocketHandler } from '@app/socket-handler/new-online-game-socket-handler/new-online-game-socket-handler.service';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { WaitingForOtherPlayersComponent } from './waiting-for-other-players.component';

const mockDialogRef = {
    close: jasmine.createSpy('close'),
};

describe('WaitingForOtherPlayersComponent', () => {
    let component: WaitingForOtherPlayersComponent;
    let fixture: ComponentFixture<WaitingForOtherPlayersComponent>;
    let onlineSocketHandlerSpy: jasmine.SpyObj<NewOnlineGameSocketHandler>;
    let matDialog: jasmine.SpyObj<MatDialog>;
    let errors$: Subject<string>;
    let gameSettings$: Subject<OnlineGameSettings>;
    let deletedGame$: Observable<boolean>;
    let isWaiting$: BehaviorSubject<boolean>;

    beforeEach(async () => {
        matDialog = jasmine.createSpyObj('MatDialog', ['open']);
        onlineSocketHandlerSpy = jasmine.createSpyObj(
            'NewOnlineGameSocketHandler',
            ['createGame', 'listenForPendingGames', 'disconnectSocket', 'joinPendingGame', 'launchGame'],
            ['error$', 'pendingGameId$', 'deletedGame$', 'gameSettings$', 'isWaiting$'],
        );
        await TestBed.configureTestingModule({
            imports: [BrowserAnimationsModule, AppMaterialModule, HttpClientTestingModule, RouterTestingModule],

            providers: [
                { provide: MatDialogRef, useValue: mockDialogRef },
                { provide: MatDialog, useValue: matDialog },
                { provide: NewOnlineGameSocketHandler, useValue: onlineSocketHandlerSpy },
            ],
            declarations: [WaitingForOtherPlayersComponent],
        }).compileComponents();

        errors$ = new Subject<string>();
        (Object.getOwnPropertyDescriptor(onlineSocketHandlerSpy, 'error$')?.get as jasmine.Spy<() => Observable<string>>).and.returnValue(errors$);
        deletedGame$ = new Observable<boolean>();
        (Object.getOwnPropertyDescriptor(onlineSocketHandlerSpy, 'deletedGame$')?.get as jasmine.Spy<() => Observable<boolean>>).and.returnValue(
            deletedGame$,
        );
        gameSettings$ = new Subject<OnlineGameSettings>();
        (
            Object.getOwnPropertyDescriptor(onlineSocketHandlerSpy, 'gameSettings$')?.get as jasmine.Spy<() => Observable<OnlineGameSettings>>
        ).and.returnValue(gameSettings$);
        isWaiting$ = new BehaviorSubject<boolean>(false);
        (Object.getOwnPropertyDescriptor(onlineSocketHandlerSpy, 'isWaiting$')?.get as jasmine.Spy<() => BehaviorSubject<boolean>>).and.returnValue(
            isWaiting$,
        );
    });

    beforeEach(() => {
        fixture = TestBed.createComponent(WaitingForOtherPlayersComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should disconnect socket on cancel', () => {
        component.cancel();
        expect(onlineSocketHandlerSpy.disconnectSocket).toHaveBeenCalled();
    });
});
