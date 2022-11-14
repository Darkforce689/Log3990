import { GameState } from '@app/game-logic/game/games/online-game/game-state';
import { GameMode } from '@app/socket-handler/interfaces/game-mode.interface';

export interface GameHistoryInfo {
    gameToken: string;
    gameMode: GameMode;
    userIds: string[];
    winnerUsers: string[];
    date: number;
}

export interface GameHistory {
    games: GameHistoryInfo[];
    userId: string;
}

export interface GameStateHistory {
    gameState: GameState;
    gameToken: string;
    date: number;
}
