import { GAME_TOKEN_PREFIX } from '@app/chat/constants';

export const isGameChatRoom = (roomId: string) => {
    return roomId.startsWith(GAME_TOKEN_PREFIX);
};
