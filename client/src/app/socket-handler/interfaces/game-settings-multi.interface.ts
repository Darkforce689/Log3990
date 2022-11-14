import { BotDifficulty } from '@app/services/bot-difficulty';
import { GameMode } from '@app/socket-handler/interfaces/game-mode.interface';

export interface OnlineGameSettingsUI {
    gameMode: GameMode;
    timePerTurn: number;
    playerNames: string[];
    tmpPlayerNames: string[];
    privateGame: boolean;
    gameStatus: string;
    randomBonus: boolean;
    botDifficulty: BotDifficulty;
    numberOfPlayers: number;
    magicCardIds: string[];
    password?: string;
    numberOfBots?: number;
    observerNames?: string[];
}

export interface OnlineGameSettings extends OnlineGameSettingsUI {
    id: string;
}
