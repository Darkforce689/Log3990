import { AfterContentChecked, Component, Input } from '@angular/core';
import { GameInfoService } from '@app/game-logic/game/game-info/game-info.service';
import { UIExchange } from '@app/game-logic/actions/ui-actions/ui-exchange';
import { UIInputControllerService } from '@app/game-logic/actions/ui-actions/ui-input-controller.service';
import {
    EXCHANGEALETTER_ID,
    PLACERANDOMBONUS_ID,
    EXCHANGEHORSE_ID,
    SPLITPOINTS_ID,
    EXCHANGEHORSEALL_ID,
    UI_MAGIC_CARD_MAP,
} from '@app/game-logic/actions/magic-card/magic-card-constants';
import { UIPlace } from '@app/game-logic/actions/ui-actions/ui-place';

@Component({
    selector: 'app-magic-card',
    templateUrl: './magic-card.component.html',
    styleUrls: ['./magic-card.component.scss'],
})
export class MagicCardComponent implements AfterContentChecked {
    @Input() index: number;
    magicCardId: string | undefined;

    constructor(private info: GameInfoService, private inputController: UIInputControllerService) {}

    ngAfterContentChecked(): void {
        this.magicCardId = this.hasMinCard ? this.info.getDrawnMagicCard()[this.index].id : undefined;
    }

    get isItMyTurn(): boolean {
        try {
            return this.info.player === this.info.activePlayer;
        } catch (e) {
            return false;
        }
    }

    get isMagicGame() {
        return this.info.isMagicGame;
    }

    get canUseMagicCards(): boolean {
        return this.isItMyTurn && this.isMagicGame;
    }

    get canExchangeMagicCard(): boolean {
        return (
            this.canUseMagicCards &&
            this.inputController.activeAction instanceof UIExchange &&
            this.inputController.activeAction.concernedIndexes.size === 1 &&
            this.inputController.canBeExecuted &&
            this.info.numberOfLettersRemaining >= 1
        );
    }

    get canPlaceRandomBonusMagicCard(): boolean {
        return (
            this.canUseMagicCards &&
            this.inputController.activeAction instanceof UIPlace &&
            this.inputController.activeAction.pointerPosition !== null &&
            this.inputController.activeAction.concernedIndexes.size === 0 &&
            !this.info.tileHasBonusOnIt(this.inputController.activeAction.pointerPosition)
        );
    }

    get canUse(): boolean {
        switch (this.magicCardId) {
            case EXCHANGEALETTER_ID:
                return this.canExchangeMagicCard;
            case SPLITPOINTS_ID:
                return this.canUseMagicCards;
            case PLACERANDOMBONUS_ID:
                return this.canPlaceRandomBonusMagicCard;
            case EXCHANGEHORSE_ID:
                return this.canUseMagicCards;
            case EXCHANGEHORSEALL_ID:
                return this.canUseMagicCards;
            default:
                return false;
        }
    }

    get hasMinCard(): boolean {
        return this.info.getDrawnMagicCard().length > this.index;
    }

    get name(): string | undefined {
        return this.magicCardId === undefined ? undefined : UI_MAGIC_CARD_MAP.get(this.magicCardId)?.name;
    }

    get description(): string | undefined {
        return this.magicCardId === undefined ? undefined : UI_MAGIC_CARD_MAP.get(this.magicCardId)?.description;
    }

    splitPoints() {
        this.inputController.splitPoints(this.info.player);
    }

    exchangeLetter() {
        this.inputController.exchangeLetter(this.info.player);
    }

    placeBonus() {
        this.inputController.placeBonus(this.info.player);
    }

    exchangeHorse() {
        this.inputController.exchangeHorse(this.info.player);
    }

    exchangeHorseAll() {
        this.inputController.exchangeHorseAll(this.info.player);
    }

    execute() {
        switch (this.magicCardId) {
            case SPLITPOINTS_ID:
                this.splitPoints();
                break;
            case EXCHANGEALETTER_ID:
                this.exchangeLetter();
                break;
            case PLACERANDOMBONUS_ID:
                this.placeBonus();
                break;
            case EXCHANGEHORSE_ID:
                this.exchangeHorse();
                break;
            case EXCHANGEHORSEALL_ID:
                this.exchangeHorseAll();
                break;
        }
    }
}
