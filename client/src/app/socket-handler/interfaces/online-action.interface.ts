import { Letter } from '@app/game-logic/game/board/letter.interface';
import { PlacementSetting } from '@app/game-logic/interfaces/placement-setting.interface';

export interface OnlineAction {
    type: OnlineActionType | OnlineMagicCardActionType;
    placementSettings?: PlacementSetting;
    letters?: string;
    letterRack?: Letter[];
    position?: { x: number; y: number };
}

export enum OnlineActionType {
    Place = 'place',
    Exchange = 'exchange',
    Pass = 'pass',
}

export enum OnlineMagicCardActionType {
    SplitPoints = 'splitPoints',
    ExchangeALetter = 'exchangeALetter',
    PlaceBonus = 'placeBonus',
    ExchangeHorse = 'exchangeHorse',
    ExchangeHorseAll = 'exchangeHorseAll',
    SkipNextTurn = 'skipNextTurn',
}
