export interface GameSettings {
    timePerTurn: number;
    playerName: string;
    tmpPlayerNames: string[];
    botDifficulty: string;
    privateGame: boolean;
    gameStatus: string;
    randomBonus: boolean;
    numberOfPlayers: number;
    magicCardIds: string[];
    password?: string;
}
