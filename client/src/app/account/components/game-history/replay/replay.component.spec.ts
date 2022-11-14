import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { EMPTY_CHAR } from '@app/game-logic/constants';
import { Board } from '@app/game-logic/game/board/board';
import { AppMaterialModule } from '@app/modules/material.module';

import { ReplayComponent } from './replay.component';

describe('ReplayComponent', () => {
    let component: ReplayComponent;
    let fixture: ComponentFixture<ReplayComponent>;
    const mockDialogRef = {
        close: jasmine.createSpy('close'),
    };
    const player1 = {
        name: 'P1',
        points: 345,
        letterRack: [
            { char: EMPTY_CHAR, value: 0 },
            { char: EMPTY_CHAR, value: 0 },
        ],
    };
    const drawnMagicCards = [[{ id: '' }]];
    const gameState = {
        players: [player1],
        activePlayerIndex: 0,
        grid: new Board().grid,
        lettersRemaining: 88,
        isEndOfGame: false,
        winnerIndex: [0],
        drawnMagicCards,
    };
    const gameStates = [{ gameState, date: 123456789, gameToken: 'GAMETOKEN' }];
    const data = { gameStates, userIndex: 0 };
    beforeEach(async () => {
        await TestBed.configureTestingModule({
            declarations: [ReplayComponent],
            providers: [
                { provide: MatDialogRef, useValue: mockDialogRef },
                { provide: MAT_DIALOG_DATA, useValue: data },
            ],
            imports: [AppMaterialModule],
            schemas: [CUSTOM_ELEMENTS_SCHEMA],
        }).compileComponents();
    });

    beforeEach(() => {
        fixture = TestBed.createComponent(ReplayComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
