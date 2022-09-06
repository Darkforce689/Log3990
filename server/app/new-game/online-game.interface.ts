import { GameMode } from '@app/game/game-mode.enum';

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
