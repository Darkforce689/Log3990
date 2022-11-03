import { BotDifficulty } from '@app/services/bot-difficulty';

export interface GameSettings {
    timePerTurn: number;
    playerName: string;
    tmpPlayerNames: string[];
    botDifficulty: BotDifficulty;
    privateGame: boolean;
    gameStatus: string;
    randomBonus: boolean;
    numberOfPlayers: number;
    magicCardIds: string[];
    password?: string;
}
