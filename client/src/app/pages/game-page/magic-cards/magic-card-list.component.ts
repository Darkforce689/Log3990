import { AfterContentChecked, Component } from '@angular/core';
import { GameInfoService } from '@app/game-logic/game/game-info/game-info.service';
import { IMagicCard } from '@app/game-logic/game/games/online-game/game-state';

@Component({
    selector: 'app-magic-card-list',
    templateUrl: './magic-card-list.component.html',
    styleUrls: ['./magic-card-list.component.scss'],
})
export class MagicCardListComponent implements AfterContentChecked {
    magicCards: IMagicCard[];

    constructor(private info: GameInfoService) {}

    ngAfterContentChecked(): void {
        try {
            this.magicCards = this.info.getDrawnMagicCard();
        } catch (e) {
            return;
        }
    }
}
