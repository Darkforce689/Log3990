import { Conversation } from '@app/messages-service/interfaces/conversation.interface';
import { ConvoMessage, Message } from '@app/messages-service/message.interface';
import { ConversationService } from '@app/messages-service/services/conversation.service';
import { MessageService } from '@app/messages-service/services/messages-service';
import { ObjectId } from 'mongodb';

export class Room {
    userIds = new Set<string>();
    userIdToSocketId = new Map<string, string>();

    constructor(private messageService: MessageService, private conversationService: ConversationService, private conversation?: Conversation) {}

    async addMessage(message: Message) {
        const isTmpConversation = !this.conversation;
        if (isTmpConversation) {
            return;
        }

        // eslint-disable-next-line @typescript-eslint/no-non-null-assertion, no-underscore-dangle
        const isUserInConvo = await this.conversationService.isUserInConversation(this.conversation!._id, message.from);
        if (!isUserInConvo) {
            throw Error("Can't join the room: You have not joined the conversation");
        }

        // eslint-disable-next-line no-underscore-dangle, @typescript-eslint/no-non-null-assertion
        const conversation = new ObjectId(this.conversation!._id);
        const from = new ObjectId(message.from);
        const { content, date } = message;
        const convoMessage: ConvoMessage = {
            from,
            content,
            date,
            conversation,
        };
        await this.messageService.addMessage(convoMessage);
    }

    private get isTmpConversation() {
        return !this.conversation;
    }

    async addUser(userId: string, socketId: string) {
        if (!this.isTmpConversation) {
            // eslint-disable-next-line no-underscore-dangle, @typescript-eslint/no-non-null-assertion
            const isUserInConvo = await this.conversationService.isUserInConversation(this.conversation!._id, userId);
            if (!isUserInConvo) {
                throw Error("Can't join the room: You have not joined the conversation");
            }
        }
        this.userIds.add(userId);
        this.userIdToSocketId.set(userId, socketId);
    }

    async deleteUser(userId: string) {
        this.userIds.delete(userId);
    }
}
