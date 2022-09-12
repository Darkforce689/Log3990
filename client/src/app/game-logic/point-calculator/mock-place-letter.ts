import { PlaceLetter } from '@app/game-logic/actions/place-letter';
import { OfflineGame } from '@app/game-logic/game/games/offline-game/offline-game';
import { PlacementSetting } from '@app/game-logic/interfaces/placement-setting.interface';
import { Player } from '@app/game-logic/player/player';

export class MockPlaceLetter extends PlaceLetter {
    constructor(player: Player, public word: string, public placement: PlacementSetting) {
        super(player, word, placement);
    }
    execute(game: OfflineGame) {
        return game;
    }
}
