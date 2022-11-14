import { GameState } from '@app/game/game-logic/interface/game-state.interface';
import { GameMode } from '@app/game/game-mode.enum';

export interface GameHistory {
    gameToken: string;
    gameType: GameMode;
    userdId: string[];
    winnersId: string[];
    date: number;
}
export interface GameStateHistory {
    gameState: GameState;
    gameToken: string;
    date: number;
}
