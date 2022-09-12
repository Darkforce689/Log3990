import { BotDifficulty } from '@app/database/bot-info/bot-difficulty';

export interface BotInfo {
    name: string;
    type: BotDifficulty;
    canEdit: boolean;
}
