import { BotDifficulty } from '@app/database/bot-info/bot-difficulty';
import { BotInfo } from '@app/database/bot-info/bot-info';

export const DEFAULT_EASY_BOT: BotInfo[] = [
    { name: 'Jimmy', type: BotDifficulty.Easy, canEdit: false },
    { name: 'Sasha', type: BotDifficulty.Easy, canEdit: false },
    { name: 'Beep', type: BotDifficulty.Easy, canEdit: false },
];

export const DEFAULT_EXPERT_BOT: BotInfo[] = [
    { name: 'Terminator', type: BotDifficulty.Expert, canEdit: false },
    { name: 'Mario', type: BotDifficulty.Expert, canEdit: false },
    { name: 'Spooky', type: BotDifficulty.Expert, canEdit: false },
];
