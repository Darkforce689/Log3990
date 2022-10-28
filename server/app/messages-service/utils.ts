import { GAME_TOKEN_PREFIX } from '@app/constants';

export const isGameConversation = (conversationId: string) => {
    return isGameToken(conversationId);
};

export const isGameToken = (s: string) => {
    return s.startsWith(GAME_TOKEN_PREFIX);
};
