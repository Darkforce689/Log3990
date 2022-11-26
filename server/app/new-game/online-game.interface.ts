import { BotDifficulty } from '@app/game/game-logic/player/bot/bot-difficulty';
import { GameMode } from '@app/game/game-mode.enum';

export interface OnlineGameSettingsUI {
    gameMode: GameMode;
    timePerTurn: number;
    playerNames: string[];
    tmpPlayerNames: string[];
    privateGame: boolean;
    gameStatus?: string;
    randomBonus: boolean;
    botDifficulty: BotDifficulty;
    numberOfPlayers: number;
    magicCardIds: string[];
    password?: string;
    numberOfBots?: number;
    observerNames?: string[];
    botNames: string[];
}

export interface OnlineGameSettings extends OnlineGameSettingsUI {
    id: string;
}
