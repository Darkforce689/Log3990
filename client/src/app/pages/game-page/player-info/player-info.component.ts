import { Component } from '@angular/core';
import { GameInfoService } from '@app/game-logic/game/game-info/game-info.service';
import { Player } from '@app/game-logic/player/player';
import { AccountService } from '@app/services/account.service';

@Component({
    selector: 'app-player-info',
    templateUrl: './player-info.component.html',
    styleUrls: ['./player-info.component.scss'],
})
export class PlayerInfoComponent {
    constructor(private info: GameInfoService, private account: AccountService) {}

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
