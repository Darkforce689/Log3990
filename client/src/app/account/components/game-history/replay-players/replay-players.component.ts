import { Component, Input } from '@angular/core';
import { UI_MAGIC_CARD_ARRAY_REPLAY } from '@app/account/constants';
import { IMagicCard, LightPlayer } from '@app/game-logic/game/games/online-game/game-state';

@Component({
    selector: 'app-replay-players',
    templateUrl: './replay-players.component.html',
    styleUrls: ['./replay-players.component.scss'],
})
export class ReplayPlayersComponent {
    @Input() activePlayer: LightPlayer | undefined;
    @Input() isEndOfGame: boolean;
    @Input() players: LightPlayer[];
    @Input() magicCards: IMagicCard[][] | undefined;

    isActivePlayer(player: LightPlayer) {
        return player === this.activePlayer;
    }

    get isMagicGame() {
        return this.magicCards !== undefined;
    }

    getPlayerIndex(playerToFind: LightPlayer) {
        return this.players.findIndex((player) => player.name === playerToFind.name);
    }

    getMagicCards(player: LightPlayer): IMagicCard[] | undefined {
        if (this.magicCards === undefined) {
            return;
        }
        const index = this.getPlayerIndex(player);
        const magicCards = this.magicCards[index];
        while (magicCards.length < 3) {
            magicCards.push({ id: '' } as IMagicCard);
        }
        return magicCards;
    }

    getCardName(newCard: IMagicCard): string {
        return newCard ? (UI_MAGIC_CARD_ARRAY_REPLAY.find((card) => card.info.id === newCard.id)?.info.name as string) : '';
    }

    getCardIcon(newCard: IMagicCard): string {
        return this.canSetIcon(newCard) ? (UI_MAGIC_CARD_ARRAY_REPLAY.find((card) => card.info.id === newCard.id)?.icon as string) : '';
    }

    canSetIcon(newCard: IMagicCard) {
        return newCard.id !== '';
    }
}
