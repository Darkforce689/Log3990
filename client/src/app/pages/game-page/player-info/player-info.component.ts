import { Component } from '@angular/core';
import { GameInfoService } from '@app/game-logic/game/game-info/game-info.service';
import { Player } from '@app/game-logic/player/player';

@Component({
    selector: 'app-player-info',
    templateUrl: './player-info.component.html',
    styleUrls: ['./player-info.component.scss'],
})
export class PlayerInfoComponent {
    constructor(private info: GameInfoService) {}

    get activePlayerName(): string {
        return this.info.activePlayer.name;
    }

    get user(): Player {
        return this.info.player;
    }

    get playersInOrder(): Player[] {
        const orderedPlayer = [this.user];
        this.info.players.forEach((player) => {
            if (player.name !== this.user.name) orderedPlayer.push(player);
        });
        return orderedPlayer;
    }

    get players(): Player[] {
        return this.info.players;
    }

    get lettersRemaining(): number {
        return this.info.numberOfLettersRemaining;
    }

    isActivePlayer(player: Player) {
        return player.name === this.activePlayerName;
    }
}
