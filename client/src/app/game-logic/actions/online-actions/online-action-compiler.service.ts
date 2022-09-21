import { Injectable } from '@angular/core';
import { Action } from '@app/game-logic/actions/action';
import { ExchangeLetter } from '@app/game-logic/actions/exchange-letter';
import { MagicCard } from '@app/game-logic/actions/magic-card';
import { SplitPoints } from '@app/game-logic/actions/magic-card-split-points';
import { ExchangeALetter } from '@app/game-logic/actions/magic-card-exchange-letter';
import { PassTurn } from '@app/game-logic/actions/pass-turn';
import { PlaceLetter } from '@app/game-logic/actions/place-letter';
import { OnlineAction, OnlineActionType, OnlineMagicCardActionType } from '@app/socket-handler/interfaces/online-action.interface';

@Injectable({
    providedIn: 'root',
})
export class OnlineActionCompilerService {
    compileActionOnline(action: Action): OnlineAction | undefined {
        if (action instanceof PlaceLetter) {
            return this.compilePlaceLetterOnline(action);
        }

        if (action instanceof ExchangeLetter) {
            return this.compileExchangeLetterOnline(action);
        }

        if (action instanceof PassTurn) {
            return this.compilePassTurnOnline(action);
        }

        if (action instanceof MagicCard) {
            return this.compileMagicCardOnline(action);
        }
        return undefined;
    }

    private compilePlaceLetterOnline(action: PlaceLetter): OnlineAction {
        const onlinePlaceLetter: OnlineAction = {
            type: OnlineActionType.Place,
            placementSettings: action.placement,
            letters: action.word,
            letterRack: action.player.letterRack,
        };
        return onlinePlaceLetter;
    }

    private compileExchangeLetterOnline(action: ExchangeLetter): OnlineAction {
        let lettersToExchange = '';
        action.lettersToExchange.forEach((letter) => {
            lettersToExchange += letter.char;
        });
        const onlineExchangeLetter: OnlineAction = {
            type: OnlineActionType.Exchange,
            letters: lettersToExchange,
            letterRack: action.player.letterRack,
        };
        return onlineExchangeLetter;
    }

    private compilePassTurnOnline(action: PassTurn): OnlineAction {
        const passTurn: OnlineAction = {
            type: OnlineActionType.Pass,
            letterRack: action.player.letterRack,
        };
        return passTurn;
    }

    private compileMagicCardOnline(action: MagicCard): OnlineAction | undefined {
        if (action instanceof SplitPoints) {
            return this.compileSplitPointsOnline(action);
        }

        if (action instanceof ExchangeALetter) {
            return this.compileExchangeALetterOnline(action);
        }
        return undefined;
    }

    private compileExchangeALetterOnline(action: ExchangeALetter): OnlineAction {
        const exchangeALetter: OnlineAction = {
            type: OnlineMagicCardActionType.ExchangeALetter,
            letterRack: action.player.letterRack,
            letters: `${action.letterToExchange.char}`,
        };
        return exchangeALetter;
    }

    private compileSplitPointsOnline(action: SplitPoints): OnlineAction {
        const passTurn: OnlineAction = {
            type: OnlineMagicCardActionType.SplitPoints,
            letterRack: action.player.letterRack,
        };
        return passTurn;
    }
}
