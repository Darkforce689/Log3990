import { GameState } from '@app/game/game-logic/interface/game-state.interface';
import { GameMode } from '@app/game/game-mode.enum';

export interface GameHistory {
    gameToken: string;
    gameMode: GameMode;
    userIds: string[];
    winnerIds: string[];
    forfeitedIds: string[];
    date: number;
}
export interface GameStateHistory {
    gameState: GameState;
    gameToken: string;
    date: number;
}
