import { BotDifficulty } from '@app/services/bot-difficulty';

export interface BotInfo {
    name: string;
    type: BotDifficulty;
    canEdit: boolean;
}
