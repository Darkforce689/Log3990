import { Injectable } from '@angular/core';
import { ExchangeLetter } from '@app/game-logic/actions/exchange-letter';
import { PassTurn } from '@app/game-logic/actions/pass-turn';
import { PlaceLetter } from '@app/game-logic/actions/place-letter';
import { Letter } from '@app/game-logic/game/board/letter.interface';
import { PlacementSetting } from '@app/game-logic/interfaces/placement-setting.interface';
import { Player } from '@app/game-logic/player/player';

@Injectable({
    providedIn: 'root',
})
export class ActionCreatorService {
    createPlaceLetter(player: Player, wordToPlace: string, placementSetting: PlacementSetting): PlaceLetter {
        return new PlaceLetter(player, wordToPlace, placementSetting);
    }

    createExchange(player: Player, lettersToExchange: Letter[]): ExchangeLetter {
        return new ExchangeLetter(player, lettersToExchange);
    }

    createPassTurn(player: Player): PassTurn {
        return new PassTurn(player);
    }
}
