import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { OnlineGameCreationParams } from '@app/game-logic/game/games/game-creator/game-creation-params';
import { MagicOnlineGame } from '@app/game-logic/game/games/magic-game/magic-game';
import { OnlineGame } from '@app/game-logic/game/games/online-game/online-game';
import { AccountService } from '@app/services/account.service';
import { GameCreatorService } from './game-creator.service';

describe('GameCreatorService', () => {
    let service: GameCreatorService;
    const timePerTurn = 10000;
    const id = 'id';
    const accountServiceMock = jasmine.createSpyObj('AccountService', ['actualizeAccount'], {
        account: {
            name: 'p1',
            _id: '1',
            email: 'a@b.c',
        },
    });

    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [{ provide: AccountService, useValue: accountServiceMock }],
            imports: [HttpClientTestingModule],
        });
        service = TestBed.inject(GameCreatorService);
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    it('should create online game', () => {
        const gameCreationParams: OnlineGameCreationParams = { id, timePerTurn };
        const newGame = service.createOnlineGame(gameCreationParams);
        expect(newGame).toBeInstanceOf(OnlineGame);
    });

    it('should create magic game', () => {
        const gameCreationParams: OnlineGameCreationParams = { id, timePerTurn };
        const newGame = service.createMagicOnlineGame(gameCreationParams);
        expect(newGame).toBeInstanceOf(MagicOnlineGame);
    });
});
