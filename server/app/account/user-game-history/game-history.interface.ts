import { GameState } from '@app/game/game-logic/interface/game-state.interface';

export interface GameHistory {
    gameToken: string;
    userdId: string[];
    winnersId: string[];
    date: number;
}
export interface GameStateHistory {
    gameState: GameState;
    gameToken: string;
    date: number;
}
