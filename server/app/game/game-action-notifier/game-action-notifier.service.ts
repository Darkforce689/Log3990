import { Action } from '@app/game/game-logic/actions/action';
import { ExchangeLetter } from '@app/game/game-logic/actions/exchange-letter';
import { PassTurn } from '@app/game/game-logic/actions/pass-turn';
import { PlaceLetter } from '@app/game/game-logic/actions/place-letter';
import { Player } from '@app/game/game-logic/player/player';
import { placementSettingsToString } from '@app/game/game-logic/utils';
import { Observable, Subject } from 'rxjs';
import { Service } from 'typedi';

export interface GameActionNotification {
    gameToken: string;
    content: string;
    to: string[];
}

@Service()
export class GameActionNotifierService {
    private notificationSubject: Subject<GameActionNotification> = new Subject<GameActionNotification>();
    get notification$(): Observable<GameActionNotification> {
        return this.notificationSubject;
    }

    notifyPlayerLeft(newPlayer: Player, previousName: string, playerNames: string[], gameToken: string) {
        const content = `${previousName}  a abandonné la partie et sera remplacé par le joueur virtuel ${newPlayer.name}`;
        const notification: GameActionNotification = { gameToken, content, to: playerNames };
        this.notificationSubject.next(notification);
    }

    notify(action: Action, playerNames: string[], gameToken: string): void {
        if (action instanceof ExchangeLetter) {
            this.notifyExchangeLetter(action, playerNames, gameToken);
        }

        if (action instanceof PlaceLetter) {
            this.notifyPlaceLetter(action, playerNames, gameToken);
        }

        if (action instanceof PassTurn) {
            this.notifyPassTurn(action, playerNames, gameToken);
        }
    }

    private notifyExchangeLetter(exchangeLetter: ExchangeLetter, playerNames: string[], gameToken: string) {
        const player = exchangeLetter.player;
        const lettersToExchange = exchangeLetter.lettersToExchange;
        const content = `${player.name} échange ${lettersToExchange.length} lettres`;
        const notification: GameActionNotification = { gameToken, content, to: playerNames };
        this.notificationSubject.next(notification);
    }

    private notifyPlaceLetter(placeLetter: PlaceLetter, playerNames: string[], gameToken: string) {
        const player = placeLetter.player;
        const word = placeLetter.word;
        const placement = placeLetter.placement;
        const placementString = placementSettingsToString(placement);
        const content = `${player.name} place le mot ${word} en ${placementString}`;
        const notification: GameActionNotification = { gameToken, content, to: playerNames };
        this.notificationSubject.next(notification);
    }

    private notifyPassTurn(passTurn: PassTurn, playerNames: string[], gameToken: string) {
        const player = passTurn.player;
        const content = `${player.name} passe son tour`;
        const notification: GameActionNotification = { gameToken, content, to: playerNames };
        this.notificationSubject.next(notification);
    }
}
