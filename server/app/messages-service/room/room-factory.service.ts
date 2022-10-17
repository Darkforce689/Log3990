import { Room } from '@app/messages-service/room/room';
import { ConversationService } from '@app/messages-service/services/conversation.service';
import { MessageService } from '@app/messages-service/services/messages-service';
import { Service } from 'typedi';

@Service()
export class RoomFactory {
    constructor(private messageService: MessageService, private conversationService: ConversationService) {}

    // here roomId could be a game room or conversation name
    async createRoom(roomId: string) {
        const conversation = (await this.conversationService.getConversation({ name: roomId })) ?? undefined;
        return new Room(this.messageService, this.conversationService, conversation);
    }
}
