import { Player } from '@app/game/game-logic/player/player';

export enum EndOfGameReason {
    Forfeit,
    GameEnded,
    ManualStop,
}

export interface EndOfGame {
    gameToken: string;
    reason: EndOfGameReason;
    players: Player[];
}
