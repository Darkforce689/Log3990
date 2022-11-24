import { Component, Input, OnInit } from '@angular/core';
import { UI_MAGIC_CARD_MAP } from '@app/game-logic/actions/magic-card/magic-card-constants';
import { IMagicCard, LightPlayer } from '@app/game-logic/game/games/online-game/game-state';
import { UserCacheService } from '@app/users/services/user-cache.service';

@Component({
    selector: 'app-replay-players',
    templateUrl: './replay-players.component.html',
    styleUrls: ['./replay-players.component.scss'],
})
export class ReplayPlayersComponent implements OnInit {
    @Input() activePlayer: LightPlayer | undefined;
    @Input() isEndOfGame: boolean;
    @Input() players: LightPlayer[] = [];
    @Input() magicCards: IMagicCard[][] | undefined;
    avatars = new Map<string, string>();
    constructor(private userCacheService: UserCacheService) {}

    ngOnInit(): void {
        const names = this.players.map((player) => player.name);
        this.addPlayerIcons(names);
    }

    isActivePlayer(player: LightPlayer) {
        return player === this.activePlayer;
    }

    addPlayerIcons(playerNames: string[]) {
        playerNames.forEach((name) =>
            this.userCacheService.getUserByName(name).subscribe((user) => {
                if (!user) {
                    return;
                }
                if (!user.avatar) {
                    this.avatars.set(name, 'default');
                    return;
                }
                this.avatars.set(name, user.avatar);
            }),
        );
    }

    getAvatarIcon(name: string): string {
        return this.avatars.get(name) ?? 'default';
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
        return UI_MAGIC_CARD_MAP.get(newCard.id)?.name ?? '';
    }

    getCardIcon(newCard: IMagicCard): string {
        return UI_MAGIC_CARD_MAP.get(newCard.id)?.icon ?? '';
    }

    canSetIcon(newCard: IMagicCard) {
        return newCard.id !== '';
    }

    get isMagicGame() {
        return this.magicCards !== undefined;
    }
}
