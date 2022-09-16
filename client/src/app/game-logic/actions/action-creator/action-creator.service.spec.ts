import { TestBed } from '@angular/core/testing';
import { ActionCreatorService } from '@app/game-logic/actions/action-creator/action-creator.service';
import { ExchangeLetter } from '@app/game-logic/actions/exchange-letter';
import { PassTurn } from '@app/game-logic/actions/pass-turn';
import { PlaceLetter } from '@app/game-logic/actions/place-letter';
import { Direction } from '@app/game-logic/direction.enum';
import { LetterCreator } from '@app/game-logic/game/board/letter-creator';
import { Player } from '@app/game-logic/player/player';

describe('ActionCreatorService', () => {
    let service: ActionCreatorService;
    let player: Player;
    const letterFactory = new LetterCreator();
    beforeEach(() => {
        service = TestBed.inject(ActionCreatorService);
        player = new Player('Allo');
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    it('should return instance of placeLetter', () => {
        expect(service.createPlaceLetter(player, 'allo', { x: 0, y: 0, direction: Direction.Horizontal })).toBeInstanceOf(PlaceLetter);
    });

    it('should return instance of ExchangeLetter', () => {
        const lettersToExchange = letterFactory.createLetters(['a', 'b', 'c']);
        expect(service.createExchange(player, lettersToExchange)).toBeInstanceOf(ExchangeLetter);
    });

    it('should return instance of PassTurn', () => {
        expect(service.createPassTurn(player)).toBeInstanceOf(PassTurn);
    });
});
