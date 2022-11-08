import { GameState } from '@app/game-logic/game/games/online-game/game-state';

export interface GameHistoryInfo {
    gameToken: string;
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
