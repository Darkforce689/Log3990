import { AfterContentChecked, Component } from '@angular/core';
import { GameInfoService } from '@app/game-logic/game/game-info/game-info.service';
import { IMagicCard } from '@app/game-logic/game/games/online-game/game-state';

@Component({
    selector: 'app-magic-card-list',
    templateUrl: './magic-card-list.component.html',
    styleUrls: ['./magic-card-list.component.scss'],
})
export class MagicCardListComponent implements AfterContentChecked {
    playerIndex: number;
    magicCards: IMagicCard[];

    constructor(private info: GameInfoService) {
        this.playerIndex = this.info.playerIndex;
    }

    ngAfterContentChecked(): void {
        this.magicCards = this.info.getDrawnMagicCard(this.playerIndex);
    }
}
