import { BotDifficulty } from '@app/database/bot-info/bot-difficulty';
import { GameMode } from '@app/game/game-mode.enum';

export interface OnlineGameSettingsUI {
    gameMode: GameMode;
    timePerTurn: number;
    playerNames: string[];
    privateGame: boolean;
    gameStatus?: string;
    randomBonus: boolean;
    botDifficulty: BotDifficulty;
    numberOfPlayers: number;
    magicCardIds: string[];
}

export interface OnlineGameSettings extends OnlineGameSettingsUI {
    id: string;
}
