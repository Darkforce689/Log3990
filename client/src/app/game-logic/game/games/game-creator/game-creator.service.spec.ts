import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { OnlineGameCreationParams } from '@app/game-logic/game/games/game-creator/game-creation-params';
import { MagicOnlineGame } from '@app/game-logic/game/games/magic-game/magic-game';
import { OnlineGame } from '@app/game-logic/game/games/online-game/online-game';
import { GameCreatorService } from './game-creator.service';

describe('GameCreatorService', () => {
    let service: GameCreatorService;
    const timePerTurn = 10000;
    const id = 'id';

    beforeEach(() => {
        TestBed.configureTestingModule({
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
