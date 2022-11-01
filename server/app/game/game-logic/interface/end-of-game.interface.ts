import { Player } from '@app/game/game-logic/player/player';
import { GameStats } from '@app/user/interfaces/game-stats.interface';

export enum EndOfGameReason {
    Forfeit,
    GameEnded,
    ManualStop,
}

export interface EndOfGame {
    gameToken: string;
    reason: EndOfGameReason;
    players: Player[];
    stats: Map<string, GameStats>;
}
