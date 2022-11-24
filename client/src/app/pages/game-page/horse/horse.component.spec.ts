/* eslint-disable max-classes-per-file */
import { CommonModule } from '@angular/common';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UIExchange } from '@app/game-logic/actions/ui-actions/ui-exchange';
import { UIInputControllerService } from '@app/game-logic/actions/ui-actions/ui-input-controller.service';
import { UIMove } from '@app/game-logic/actions/ui-actions/ui-move';
import { UIPlace } from '@app/game-logic/actions/ui-actions/ui-place';
import { RACK_LETTER_COUNT } from '@app/game-logic/constants';
import { BoardService } from '@app/game-logic/game/board/board.service';
import { GameInfoService } from '@app/game-logic/game/game-info/game-info.service';
import { GameManagerService } from '@app/game-logic/game/games/game-manager/game-manager.service';
import { InputComponent, InputType } from '@app/game-logic/interfaces/ui-input';
import { Player } from '@app/game-logic/player/player';
import { getRandomInt } from '@app/game-logic/utils';
import { AppMaterialModule } from '@app/modules/material.module';
import { Observable, Subject } from 'rxjs';
import { HorseComponent } from './horse.component';

const mockPlayers: Player[] = [new Player('Tim'), new Player('George')];
mockPlayers[0].letterRack = [{ char: 'A', value: 3 }];

class MockGameManagerService {
    game = {
        players: mockPlayers,
    };
}

class MockGameInfoService {
    player = mockPlayers[0];
    players = mockPlayers;
}

describe('HorseComponent', () => {
    let component: HorseComponent;
    let fixture: ComponentFixture<HorseComponent>;
    let mockUIInputControllerService: UIInputControllerService;
    let mockObservableMoveFeedback: Subject<boolean>;
    let mockObservableDropFeedback: Subject<{ left: number; top: number; index: number }>;
    let mockObservableResetIndex: Subject<void>;
    beforeEach(async () => {
        mockUIInputControllerService = jasmine.createSpyObj('UIInputControllerService', [], ['moveFeedback$', 'dropFeedback$', 'resetIndex$']);
        mockObservableMoveFeedback = new Subject<boolean>();
        mockObservableDropFeedback = new Subject<{ left: number; top: number; index: number }>();
        mockObservableResetIndex = new Subject<void>();
        await TestBed.configureTestingModule({
            imports: [AppMaterialModule, CommonModule],
            declarations: [HorseComponent],
            providers: [
                { provide: GameManagerService, useClass: MockGameManagerService },
                { provide: UIInputControllerService, useValue: mockUIInputControllerService },
                { provide: GameInfoService, useClass: MockGameInfoService },
            ],
            schemas: [CUSTOM_ELEMENTS_SCHEMA],
        }).compileComponents();
        (
            Object.getOwnPropertyDescriptor(mockUIInputControllerService, 'moveFeedback$')?.get as jasmine.Spy<() => Observable<boolean>>
        ).and.returnValue(mockObservableMoveFeedback);

        (
            Object.getOwnPropertyDescriptor(mockUIInputControllerService, 'dropFeedback$')?.get as jasmine.Spy<
                () => Observable<{ left: number; top: number; index: number }>
            >
        ).and.returnValue(mockObservableDropFeedback);

        (Object.getOwnPropertyDescriptor(mockUIInputControllerService, 'resetIndex$')?.get as jasmine.Spy<() => Observable<void>>).and.returnValue(
            mockObservableResetIndex,
        );

        fixture = TestBed.createComponent(HorseComponent);
        component = fixture.componentInstance;
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should initialise player rack', () => {
        component.ngAfterContentChecked();
        expect(component.playerRack).toBeDefined();
    });

    it('should detect click on horse letters', () => {
        spyOn(component.clickLetter, 'emit');
        const args = getRandomInt(RACK_LETTER_COUNT);
        const type = InputType.LeftClick;
        const input = { from: InputComponent.Horse, type, args };
        const mouseEvent = { button: 0 } as MouseEvent;
        component.click(mouseEvent, args);
        expect(component.clickLetter.emit).toHaveBeenCalledWith(input);
    });

    it('should return the correct boolean for a rackLetter selection depending on the current UIAction', () => {
        component.ngAfterContentChecked();
        const index = getRandomInt(RACK_LETTER_COUNT);
        mockUIInputControllerService.activeAction = new UIMove(mockPlayers[0]);
        mockUIInputControllerService.activeAction.concernedIndexes.add(index);
        expect(component.isLetterSelectedForMove(index)).toBeTruthy();
        expect(component.isLetterSelectedForExchange(index)).toBeFalsy();
        expect(component.isLetterSelectedForPlace(index)).toBeFalsy();

        mockUIInputControllerService.activeAction = new UIExchange(mockPlayers[0]);
        mockUIInputControllerService.activeAction.concernedIndexes.add(index);
        expect(component.isLetterSelectedForMove(index)).toBeFalsy();
        expect(component.isLetterSelectedForExchange(index)).toBeTruthy();
        expect(component.isLetterSelectedForPlace(index)).toBeFalsy();

        mockUIInputControllerService.activeAction = new UIPlace(
            TestBed.inject(GameInfoService),
            TestBed.inject(BoardService),
            mockUIInputControllerService as UIInputControllerService,
        );
        mockUIInputControllerService.activeAction.concernedIndexes.add(index);
        expect(component.isLetterSelectedForMove(index)).toBeFalsy();
        expect(component.isLetterSelectedForExchange(index)).toBeFalsy();
        expect(component.isLetterSelectedForPlace(index)).toBeTruthy();
    });

    it('should return the correct boolean for a rackLetter selection (UIMove)', () => {
        component.ngAfterContentChecked();
        mockUIInputControllerService.activeAction = new UIMove(mockPlayers[0]);
        for (let index = 0; index < RACK_LETTER_COUNT; index++) {
            mockUIInputControllerService.activeAction.concernedIndexes.add(index);
            expect(component.isLetterSelectedForMove(index)).toBeTruthy();
            mockUIInputControllerService.activeAction.concernedIndexes.delete(index);
        }
        for (let index = 0; index < RACK_LETTER_COUNT; index++) {
            expect(component.isLetterSelectedForMove(index)).toBeFalsy();
        }
    });

    it('should return the correct boolean for a rackLetter selection (UIExchange)', () => {
        component.ngAfterContentChecked();
        mockUIInputControllerService.activeAction = new UIExchange(mockPlayers[0]);
        for (let index = 0; index < RACK_LETTER_COUNT; index++) {
            if (index % 2) {
                mockUIInputControllerService.activeAction.concernedIndexes.add(index);
            }
        }
        for (let index = 0; index < RACK_LETTER_COUNT; index++) {
            if (index % 2) {
                expect(component.isLetterSelectedForExchange(index)).toBeTruthy();
            } else {
                expect(component.isLetterSelectedForExchange(index)).toBeFalsy();
            }
        }
    });

    it('should return the correct boolean for a rackLetter selection (UIPlace)', () => {
        component.ngAfterContentChecked();
        mockUIInputControllerService.activeAction = new UIPlace(
            TestBed.inject(GameInfoService),
            TestBed.inject(BoardService),
            mockUIInputControllerService as UIInputControllerService,
        );
        for (let index = 0; index < RACK_LETTER_COUNT; index++) {
            if (index % 2) {
                mockUIInputControllerService.activeAction.concernedIndexes.add(index);
            }
        }
        for (let index = 0; index < RACK_LETTER_COUNT; index++) {
            if (index % 2) {
                expect(component.isLetterSelectedForPlace(index)).toBeTruthy();
            } else {
                expect(component.isLetterSelectedForPlace(index)).toBeFalsy();
            }
        }
    });
});
