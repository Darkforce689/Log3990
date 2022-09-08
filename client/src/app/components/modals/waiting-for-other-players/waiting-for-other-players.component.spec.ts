import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ConvertToSoloFormComponent } from '@app/components/modals/convert-to-solo-form/convert-to-solo-form.component';
import { AppMaterialModule } from '@app/modules/material.module';
import { NewOnlineGameSocketHandler } from '@app/socket-handler/new-online-game-socket-handler/new-online-game-socket-handler.service';
import { Observable, of, Subject } from 'rxjs';
import { WaitingForOtherPlayersComponent } from './waiting-for-other-players.component';

const mockDialogRef = {
    close: jasmine.createSpy('close'),
};

describe('WaitingForOtherPlayersComponent', () => {
    let component: WaitingForOtherPlayersComponent;
    let fixture: ComponentFixture<WaitingForOtherPlayersComponent>;
    let onlineSocketHandlerSpy: jasmine.SpyObj<NewOnlineGameSocketHandler>;
    let matDialog: jasmine.SpyObj<MatDialog>;
    let pendingGameId$: Subject<string>;

    beforeEach(async () => {
        matDialog = jasmine.createSpyObj('MatDialog', ['open']);
        onlineSocketHandlerSpy = jasmine.createSpyObj(
            'NewOnlineGameSocketHandler',
            ['createGame', 'listenForPendingGames', 'disconnectSocket', 'joinPendingGame', 'launchGame'],
            ['pendingGames$', 'pendingGameId$'],
        );
        await TestBed.configureTestingModule({
            imports: [BrowserAnimationsModule, AppMaterialModule],

            providers: [
                { provide: MatDialogRef, useValue: mockDialogRef },
                { provide: MatDialog, useValue: matDialog },
                { provide: NewOnlineGameSocketHandler, useValue: onlineSocketHandlerSpy },
            ],
            declarations: [WaitingForOtherPlayersComponent],
        }).compileComponents();

        pendingGameId$ = new Subject<string>();
        (Object.getOwnPropertyDescriptor(onlineSocketHandlerSpy, 'pendingGameId$')?.get as jasmine.Spy<() => Observable<string>>).and.returnValue(
            pendingGameId$,
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

    it('cancel', () => {
        const dom = fixture.nativeElement as HTMLElement;
        const cancelButton = dom.querySelectorAll('button')[0];
        spyOn(component, 'cancel');
        cancelButton.click();
        expect(component.cancel).toHaveBeenCalled();
    });
    it('should disconnect socket on cancel', () => {
        component.cancel();
        expect(onlineSocketHandlerSpy.disconnectSocket).toHaveBeenCalled();
    });

    it('launchGame should call launchGame', () => {
        const dom = fixture.nativeElement as HTMLElement;
        const launchButton = dom.querySelectorAll('button')[1];
        spyOn(component, 'launchGame');
        launchButton.click();
        expect(component.launchGame).toHaveBeenCalled();
    });

    // TODO GL3A22107-5 : Should be changed/removed
    // it('converToSolo should open convertToSolo dialog and get bot difficulty', () => {
    //     matDialog.open.and.returnValue({
    //         afterClosed: () => {
    //             return of('easy');
    //         },
    //         close: () => {
    //             mockDialogRef.close();
    //             return;
    //         },
    //     } as MatDialogRef<ConvertToSoloFormComponent>);

    //     component.launchGame();
    //     expect(component.botDifficulty).toEqual('easy');
    //     expect(component.isSoloStarted).toBeTrue();
    //     expect(mockDialogRef.close).toHaveBeenCalledWith('easy');
    // });

    it('converToSolo with no bot difficulty', () => {
        matDialog.open.and.returnValue({
            afterClosed: () => {
                return of(undefined);
            },
            close: () => {
                mockDialogRef.close();
                return;
            },
        } as MatDialogRef<ConvertToSoloFormComponent>);
        component.launchGame();
        expect(component.botDifficulty).toBeUndefined();
        expect(component.isSoloStarted).toBeFalse();
    });
});