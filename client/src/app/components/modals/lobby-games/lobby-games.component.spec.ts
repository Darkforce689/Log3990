import { LiveAnnouncer } from '@angular/cdk/a11y';
import { CommonModule } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Sort } from '@angular/material/sort';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { JoinOnlineGameComponent } from '@app/components/modals/join-online-game/join-online-game.component';
import { WAIT_STATUS } from '@app/game-logic/constants';
import { AppMaterialModule } from '@app/modules/material.module';
import { BotDifficulty } from '@app/services/bot-difficulty';
import { GameLauncherService } from '@app/socket-handler/game-launcher/game-laucher';
import { GameMode } from '@app/socket-handler/interfaces/game-mode.interface';
import { OnlineGameSettings } from '@app/socket-handler/interfaces/game-settings-multi.interface';
import { NewOnlineGameSocketHandler } from '@app/socket-handler/new-online-game-socket-handler/new-online-game-socket-handler.service';
import { Observable, of, Subject } from 'rxjs';
import { LobbyGamesComponent, LobbyGameType } from './lobby-games.component';

const mockDialogRef = {
    close: jasmine.createSpy('close').and.returnValue(() => {
        return;
    }),
};

const mockLiveAnnouncer = {
    announce: jasmine.createSpy('announce'),
};

describe('LobbyGamesComponent', () => {
    let component: LobbyGamesComponent;
    let fixture: ComponentFixture<LobbyGamesComponent>;
    let onlineSocketHandlerSpy: jasmine.SpyObj<'NewOnlineGameSocketHandler'>;
    const testPendingGames$ = new Subject<OnlineGameSettings[]>();
    const testObservableGames$ = new Subject<OnlineGameSettings[]>();
    let matDialog: jasmine.SpyObj<MatDialog>;
    const gameLauncherServiceMock = jasmine.createSpyObj('GameLauncherService', ['waitForOnlineGameStart', 'leaveGameConversation', 'joinGame']);

    const pendingGames = [
        {
            id: '4',
            playerNames: ['Jerry'],
            randomBonus: false,
            privateGame: false,
            gameStatus: WAIT_STATUS,
            timePerTurn: 65000,
            gameMode: GameMode.Classic,
            botDifficulty: BotDifficulty.Expert,
            numberOfPlayers: 2,
            magicCardIds: [],
            tmpPlayerNames: [],
            password: undefined,
        },
        {
            id: '1',
            playerNames: ['Tom'],
            privateGame: false,
            gameStatus: WAIT_STATUS,
            randomBonus: true,
            timePerTurn: 60000,
            gameMode: GameMode.Classic,
            botDifficulty: BotDifficulty.Easy,
            numberOfPlayers: 2,
            magicCardIds: [],
            tmpPlayerNames: [],
            password: undefined,
        },
    ];

    beforeEach(
        waitForAsync(() => {
            matDialog = jasmine.createSpyObj('MatDialog', ['open']);
            onlineSocketHandlerSpy = jasmine.createSpyObj(
                'NewOnlineGameSocketHandler',
                ['createGameMulti', 'listenForPendingGames', 'disconnect', 'joinPendingGame'],
                ['pendingGames$', 'observableGames$'],
            );

            TestBed.configureTestingModule({
                imports: [AppMaterialModule, BrowserAnimationsModule, CommonModule, HttpClientTestingModule, RouterTestingModule],

                providers: [
                    { provide: MAT_DIALOG_DATA, useValue: GameMode.Classic },
                    { provide: MatDialogRef, useValue: mockDialogRef },
                    { provide: MatDialog, useValue: matDialog },
                    { provide: NewOnlineGameSocketHandler, useValue: onlineSocketHandlerSpy },
                    { provide: LiveAnnouncer, useValue: mockLiveAnnouncer },
                    { provide: GameLauncherService, useValue: gameLauncherServiceMock },
                ],
                declarations: [LobbyGamesComponent],
            }).compileComponents();
            (
                Object.getOwnPropertyDescriptor(onlineSocketHandlerSpy, 'pendingGames$')?.get as jasmine.Spy<() => Observable<OnlineGameSettings[]>>
            ).and.returnValue(testPendingGames$);
            (
                Object.getOwnPropertyDescriptor(onlineSocketHandlerSpy, 'observableGames$')?.get as jasmine.Spy<
                    () => Observable<OnlineGameSettings[]>
                >
            ).and.returnValue(testObservableGames$);
        }),
    );

    beforeEach(async () => {
        fixture = TestBed.createComponent(LobbyGamesComponent);
        component = fixture.componentInstance;
        component.data = {
            lobbyGameType: LobbyGameType.PendingGame,
            gameMode: GameMode.Classic,
            lobbyGames$: testPendingGames$,
        };
        component.ngOnInit();
        component.ngAfterViewInit();
        await fixture.whenStable();
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should set data in table to gameSettings', () => {
        testPendingGames$.next(pendingGames);
        expect(component.lobbyGamesDataSource.data).toEqual(pendingGames);
    });

    it('should set selected row to row', () => {
        testPendingGames$.next(pendingGames);
        component.setSelectedRow(pendingGames[0]);
        expect(component.selectedRow).toBe(pendingGames[0]);

        component.setSelectedRow(pendingGames[0]);
        expect(component.selectedRow).toBeUndefined();

        expect(component.isSelectedRow(pendingGames[0])).toBeFalse();
    });

    it('cancel should close the dialog', () => {
        component.cancel();
        expect(mockDialogRef.close).toHaveBeenCalled();
    });

    it('JoinGame should not be responsive if game not selected', () => {
        const dom = fixture.nativeElement as HTMLElement;
        const buttons = dom.querySelectorAll('button');
        const spy = spyOn(component, 'joinGame');
        buttons[1].click();
        expect(spy.calls.count()).toBe(0);
    });

    it('JoinGame should open JoinOnline dialog and get name value', () => {
        matDialog.open.and.returnValue({
            beforeClosed: () => {
                return of('name');
            },
            close: () => {
                return;
            },
        } as MatDialogRef<JoinOnlineGameComponent>);
        component.setSelectedRow(pendingGames[0]);
        component.joinGame();
        expect(mockDialogRef.close).toHaveBeenCalled();
    });

    it('should be an empty table ', () => {
        const dom = fixture.nativeElement as HTMLElement;
        const tables = dom.querySelectorAll('tr');
        const numberTable = 2; // Header + Visible empty list element
        expect(tables.length).toBe(numberTable);

        const numberHeaders = 6;
        const tableGames = tables[0];
        expect(tableGames.cells.length).toBe(numberHeaders);

        const tableAucunePartie = tables[1];
        expect(tableAucunePartie.cells[0].innerHTML).toBe('Aucune partie en attente');
    });

    it('should be a full table ', () => {
        testPendingGames$.next(pendingGames);
        const tableLength = 4; // Header + 2 games + Hidden empty list element
        const dom = fixture.nativeElement as HTMLElement;
        const tables = dom.querySelectorAll('tr');
        expect(tables.length).toBe(tableLength);
    });

    it('should sort table ', () => {
        testPendingGames$.next(pendingGames);
        fixture.detectChanges();
        const dom = fixture.debugElement.nativeElement;
        const tableNotSort = dom.querySelectorAll('tr');
        expect(tableNotSort[1].cells[0].innerHTML).toBe(' Jerry ');

        component.lobbyGamesDataSource.sort = component.tableSort;
        const sortState: Sort = { active: 'Id', direction: 'asc' };
        component.tableSort.active = sortState.active;
        component.tableSort.direction = sortState.direction;
        component.tableSort.sortChange.emit(sortState);
        component.announceSortChange(sortState);
        expect(mockLiveAnnouncer.announce).toHaveBeenCalled();
    });
});
