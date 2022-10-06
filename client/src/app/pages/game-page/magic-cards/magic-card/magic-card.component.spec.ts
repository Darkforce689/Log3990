/* eslint-disable dot-notation */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { EXCHANGEALETTER_ID, SPLITPOINTS_ID } from '@app/game-logic/actions/magic-card/magic-card-constants';
import { UIInputControllerService } from '@app/game-logic/actions/ui-actions/ui-input-controller.service';
import { GameInfoService } from '@app/game-logic/game/game-info/game-info.service';
import { IMagicCard } from '@app/game-logic/game/games/online-game/game-state';
import { MagicCardComponent } from '@app/pages/game-page/magic-cards/magic-card/magic-card.component';

describe('MagicCardComponent', () => {
    let component: MagicCardComponent;
    let fixture: ComponentFixture<MagicCardComponent>;
    let mockInfo: jasmine.SpyObj<GameInfoService>;
    class UIInputControllerServiceMock {
        splitPoints() {
            return;
        }
        exchangeLetter() {
            return;
        }
        get canBeExecuted() {
            return true;
        }
    }

    beforeEach(async () => {
        mockInfo = jasmine.createSpyObj('GameInfoService', ['getDrawnMagicCard'], ['player', 'activePlayer', 'isMagicGame', 'playerIndex']);
        await TestBed.configureTestingModule({
            declarations: [MagicCardComponent],
            providers: [
                { provide: UIInputControllerService, useClass: UIInputControllerServiceMock },
                { provide: GameInfoService, useValue: mockInfo },
            ],
        }).compileComponents();

        fixture = TestBed.createComponent(MagicCardComponent);
        component = fixture.componentInstance;
        component.index = 0;
        mockInfo.getDrawnMagicCard.and.returnValue([{ id: SPLITPOINTS_ID } as IMagicCard, { id: EXCHANGEALETTER_ID } as IMagicCard]);
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should call splitPoints', () => {
        const inputControllerSpy = spyOn(component['inputController'], 'splitPoints');
        component.splitPoints();
        expect(inputControllerSpy).toHaveBeenCalled();
    });

    it('should call exchangeLetter', () => {
        const inputControllerSpy = spyOn(component['inputController'], 'exchangeLetter');
        component.exchangeLetter();
        expect(inputControllerSpy).toHaveBeenCalled();
    });

    it('execute with SplitPointsName should call splitPoints', () => {
        component.index = 0;
        component.ngAfterContentChecked();
        const inputControllerSpy = spyOn(component['inputController'], 'splitPoints');
        component.execute();
        expect(inputControllerSpy).toHaveBeenCalled();
    });

    it('execute with ExchangeALetterName should call exchangeLetter', () => {
        component.index = 1;
        component.ngAfterContentChecked();
        const inputControllerSpy = spyOn(component['inputController'], 'exchangeLetter');
        component.execute();
        expect(inputControllerSpy).toHaveBeenCalled();
    });
});
