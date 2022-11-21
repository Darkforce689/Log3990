import { Letter } from '@app/game/game-logic/board/letter.interface';
import { PlacementSetting } from '@app/game/game-logic/interface/placement-setting.interface';
export interface OnlineAction {
    type: OnlineActionType | OnlineMagicCardActionType;
    placementSettings?: PlacementSetting;
    letters?: string;
    letterRack?: Letter[];
    position?: { x: number; y: number };
    positions?: { x: number; y: number }[];
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
    ExtraTurn = 'extraTurn',
    ReduceTimer = 'reduceTimer',
}
