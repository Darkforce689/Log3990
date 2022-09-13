import { ActionCreatorService } from '@app/game/game-logic/actions/action-creator/action-creator.service';
import { Direction } from '@app/game/game-logic/actions/direction.enum';
import { ExchangeLetter } from '@app/game/game-logic/actions/exchange-letter';
import { PassTurn } from '@app/game/game-logic/actions/pass-turn';
import { PlaceLetter } from '@app/game/game-logic/actions/place-letter';
import { LetterCreator } from '@app/game/game-logic/board/letter-creator';
import { Player } from '@app/game/game-logic/player/player';
import { expect } from 'chai';

describe('ActionCreatorService', () => {
    let service: ActionCreatorService;
    let player: Player;
    const letterFactory = new LetterCreator();
    beforeEach(() => {
        player = new Player('Allo');
    });

    it('should be created', () => {
        expect(service).to.be.instanceOf(ActionCreatorService);
    });

    it('should return instance of placeLetter', () => {
        expect(service.createPlaceLetter(player, 'allo', { x: 0, y: 0, direction: Direction.Horizontal })).to.be.instanceOf(PlaceLetter);
    });

    it('should return instance of ExchangeLetter', () => {
        const lettersToExchange = letterFactory.createLetters(['a', 'b', 'c']);
        expect(service.createExchange(player, lettersToExchange)).to.be.instanceOf(ExchangeLetter);
    });

    it('should return instance of PassTurn', () => {
        expect(service.createPassTurn(player)).to.be.instanceOf(PassTurn);
    });
});
