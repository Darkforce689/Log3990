import { GameMode } from '@app/socket-handler/interfaces/game-mode.interface';

export interface OnlineGameSettingsUI {
    gameMode: GameMode;
    timePerTurn: number;
    playerName: string;
    opponentNames?: string[];
    randomBonus: boolean;
    dictTitle: string;
    dictDesc?: string;
    isMultiplayerGame: boolean;
}
export interface OnlineGameSettings extends OnlineGameSettingsUI {
    id: string;
}
