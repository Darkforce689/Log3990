import { BotDifficulty } from '@app/database/bot-info/bot-difficulty';
import { GameMode } from '@app/game/game-mode.enum';

export interface OnlineGameSettingsUI {
    gameMode: GameMode;
    timePerTurn: number;
    playerNames: string[];
    privateGame: boolean;
    randomBonus: boolean;
    dictTitle: string;
    dictDesc?: string;
    botDifficulty: BotDifficulty;
    numberOfPlayers: number;
}

export interface OnlineGameSettings extends OnlineGameSettingsUI {
    id: string;
}
