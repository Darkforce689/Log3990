import { Component, OnInit } from '@angular/core';
import { BOT_NAMES } from '@app/game-logic/constants';
import { GameInfoService } from '@app/game-logic/game/game-info/game-info.service';
import { Player } from '@app/game-logic/player/player';
import { getBotAvatar } from '@app/game-logic/utils';
import { AccountService } from '@app/services/account.service';
import { UserCacheService } from '@app/users/services/user-cache.service';

@Component({
    selector: 'app-player-info',
    templateUrl: './player-info.component.html',
    styleUrls: ['./player-info.component.scss'],
})
export class PlayerInfoComponent implements OnInit {
    avatars = new Map<string, string>();
    constructor(private info: GameInfoService, private account: AccountService, private userCacheService: UserCacheService) {}

    ngOnInit(): void {
        const names = this.info.players.map((player) => player.name);
        this.addPlayerIcons(names);
    }

    addPlayerIcons(playerNames: string[]) {
        playerNames.forEach((name) => {
            if (BOT_NAMES.has(name)) {
                const avatar = getBotAvatar(name);
                this.avatars.set(name, avatar);
                return;
            }
            this.userCacheService.getUserByName(name).subscribe((user) => {
                if (!user) {
                    return;
                }
                if (!user.avatar) {
                    this.avatars.set(name, 'default');
                    return;
                }
                this.avatars.set(name, user.avatar);
            });
        });
    }

    getAvatarIcon(name: string): string {
        if (!name) {
            return 'default';
        }
        if (!this.avatars.has(name)) {
            const avatar = getBotAvatar(name);
            this.avatars.set(name, avatar);
        }
        return this.avatars.get(name) ?? 'default';
    }

    get activePlayerName(): string {
        return this.info.activePlayer.name;
    }

    get user(): Player {
        return this.info.player;
    }

    get playersInOrder(): Player[] {
        let orderedPlayers: Player[] = [...this.info.players];

        const userIndex = orderedPlayers.findIndex((player) => player.name === this.info.player.name);
        const left = orderedPlayers.slice(0, userIndex);
        const right = orderedPlayers.slice(userIndex, orderedPlayers.length);
        orderedPlayers = right.concat(left);

        return orderedPlayers;
    }

    get playersFrozenOrder(): Player[] {
        const orderedPlayers: Player[] = [...this.info.players];
        return orderedPlayers;
    }

    get players(): Player[] {
        return this.isObserver ? this.playersFrozenOrder : this.playersInOrder;
    }

    get lettersRemaining(): number {
        return this.info.numberOfLettersRemaining;
    }

    get isObserver(): boolean {
        const clientName = this.account.account?.name;
        if (clientName) {
            return this.info.players.find((player) => player.name === clientName) ? false : true;
        }
        return false;
    }

    isWatchedPlayer(playerName: string): boolean {
        return this.info.player.name === playerName;
    }

    isActivePlayer(player: Player) {
        return player.name === this.activePlayerName;
    }
}
