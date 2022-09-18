import { ExchangeLetter } from '@app/game/game-logic/actions/exchange-letter';
import { PassTurn } from '@app/game/game-logic/actions/pass-turn';
import { PlaceLetter } from '@app/game/game-logic/actions/place-letter';
import { Letter } from '@app/game/game-logic/board/letter.interface';
import { PlacementSetting } from '@app/game/game-logic/interface/placement-setting.interface';
import { Player } from '@app/game/game-logic/player/player';
import { PointCalculatorService } from '@app/game/game-logic/point-calculator/point-calculator.service';
import { WordSearcher } from '@app/game/game-logic/validator/word-search/word-searcher.service';
import { Service } from 'typedi';

@Service()
export class ActionCreatorService {
    constructor(private pointCalculatorService: PointCalculatorService, private wordSearcher: WordSearcher) {}

    createPlaceLetter(player: Player, wordToPlace: string, placementSetting: PlacementSetting): PlaceLetter {
        return new PlaceLetter(player, wordToPlace, placementSetting, this.pointCalculatorService, this.wordSearcher);
    }

    createExchange(player: Player, lettersToExchange: Letter[]): ExchangeLetter {
        return new ExchangeLetter(player, lettersToExchange);
    }

    createPassTurn(player: Player): PassTurn {
        return new PassTurn(player);
    }
}
