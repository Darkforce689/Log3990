import { DatePipe } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { JoinOnlineGameComponent } from '@app/components/modals/join-online-game/join-online-game.component';
import { AppMaterialModule } from '@app/modules/material.module';
import { AccountService } from '@app/services/account.service';
import { GameLauncherService } from '@app/socket-handler/game-launcher/game-laucher';
import { OnlineGameSettings } from '@app/socket-handler/interfaces/game-settings-multi.interface';
import { NewOnlineGameSocketHandler } from '@app/socket-handler/new-online-game-socket-handler/new-online-game-socket-handler.service';
import { BehaviorSubject, of, Subject } from 'rxjs';
import { first } from 'rxjs/operators';

describe('JoinOnlineGameComponent', () => {
    let component: JoinOnlineGameComponent;
    let fixture: ComponentFixture<JoinOnlineGameComponent>;
    const mockError$ = new Subject<string>();
    const mockPendingGames$ = new BehaviorSubject<OnlineGameSettings[]>([]);

    const mockDialogRef = {
        close: jasmine.createSpy('close').and.returnValue(() => {
            return;
        }),
    };
    const mockDialog = {
        open: jasmine.createSpy('open').and.returnValue({ afterClosed: () => of(true) }),
    };

    const mockOnlineGameService = {
        // eslint-disable-next-line @typescript-eslint/no-empty-function
        joinPendingGame: jasmine.createSpy('onlineService').and.returnValue(() => {
            return;
        }),
        error$: mockError$,
        pendingGames$: mockPendingGames$,
        observableGames$: mockPendingGames$,
    };

    const mockGameLauncher = jasmine.createSpyObj('GameLauncherService', ['waitForOnlineGameStart']);
    const accountServiceMock = jasmine.createSpyObj('AccountService', ['actualizeAccount'], ['account']);

    beforeEach(
        waitForAsync(() => {
            TestBed.configureTestingModule({
                imports: [FormsModule, ReactiveFormsModule, BrowserAnimationsModule, AppMaterialModule, HttpClientTestingModule, RouterTestingModule],

                providers: [
                    { provide: MAT_DIALOG_DATA, useValue: {} },
                    { provide: MatDialogRef, useValue: mockDialogRef },
                    { provide: NewOnlineGameSocketHandler, useValue: mockOnlineGameService },
                    { provide: MatDialog, useValue: mockDialog },
                    { provide: GameLauncherService, useValue: mockGameLauncher },
                    { provide: AccountService, useValue: accountServiceMock },
                ],
                declarations: [JoinOnlineGameComponent, DatePipe],
                schemas: [CUSTOM_ELEMENTS_SCHEMA],
            }).compileComponents();
        }),
    );

    beforeEach(() => {
        fixture = TestBed.createComponent(JoinOnlineGameComponent);
        component = fixture.componentInstance;
        component.deleted = false;

        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should display data in correct form of bonus', () => {
        const dom = fixture.nativeElement as HTMLElement;
        const bonus = dom.querySelectorAll('mat-label')[1];
        expect(bonus?.innerHTML).toBe('Bonus aléatoire ');
        component.data.randomBonus = true;
        expect(component.randomBonusType).toBe('est activé');
    });

    it('cancel', () => {
        const dom = fixture.nativeElement as HTMLElement;
        const cancelButton = dom.querySelectorAll('button')[0];
        spyOn(component, 'cancel');
        cancelButton.click();
        expect(component.cancel).toHaveBeenCalled();
    });

    it('startGame should call sendParameter', () => {
        const dom = fixture.nativeElement as HTMLElement;
        const startGameButton = dom.querySelectorAll('button')[1];

        fixture.detectChanges();
        spyOn(component, 'sendParameter');
        startGameButton.click();
        fixture.detectChanges();
        expect(component.sendParameter).toHaveBeenCalled();
    });

    it('startGame should close the dialog', () => {
        component.sendParameter();
        expect(mockDialogRef.close).toHaveBeenCalled();
    });

    it('should open Error dialog if cant connect to game', (done) => {
        component.sendParameter();
        mockError$.pipe(first()).subscribe(() => {
            expect(mockDialog.open).toHaveBeenCalled();
            done();
        });
        mockError$.next('Error');
    });

    it('should not open Error dialog if no error', (done) => {
        component.sendParameter();
        mockError$.pipe(first()).subscribe((value: string) => {
            expect(value).toBeUndefined();
            done();
        });
        mockError$.next(undefined);
    });
});
