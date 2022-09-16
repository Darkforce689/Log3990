import { Action } from '@app/game-logic/actions/action';
import { Player } from '@app/game-logic/player/player';

class TestAction extends Action {}

describe('Action', () => {
    let player: Player;

    beforeEach(() => {
        player = new Player('Paul');
    });

    it('should create instance', () => {
        expect(new TestAction(player)).toBeTruthy();
    });
});
