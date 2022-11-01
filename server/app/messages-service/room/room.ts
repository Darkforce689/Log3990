/* eslint-disable @typescript-eslint/no-non-null-assertion */
/* eslint-disable no-underscore-dangle */
import { Conversation, ConversationType } from '@app/messages-service/interfaces/conversation.interface';
import { ConvoMessage, Message } from '@app/messages-service/message.interface';
import { ConversationService } from '@app/messages-service/services/conversation.service';
import { MessageService } from '@app/messages-service/services/messages-service';
import { ObjectId } from 'mongodb';

export class Room {
    userIds = new Set<string>();
    userIdToSocketId = new Map<string, string>();

    constructor(private messageService: MessageService, private conversationService: ConversationService, private conversation?: Conversation) {}

    async addMessage(message: Message) {
        if (this.isTmpConversation) {
            return;
        }

        const isUserInConvo = await this.isUserInConvo(message.from);
        if (!isUserInConvo) {
            throw Error("Can't join the room: You have not joined the conversation");
        }
        await this.addMessageToConversation(message);
    }

    async addSystemMessage(message: Message) {
        if (this.isTmpConversation) {
            return;
        }

        this.addMessageToConversation(message);
    }

    private get isTmpConversation() {
        return !this.conversation;
    }

    async addUser(userId: string, socketId: string) {
        if (!this.isTmpConversation) {
            const isUserInConvo = await this.isUserInConvo(userId);
            if (!isUserInConvo) {
                throw Error("Can't join the room: You have not joined the conversation");
            }
        }
        this.userIds.add(userId);
        this.userIdToSocketId.set(userId, socketId);
    }

    async isUserInConvo(userId: string) {
        if (this.isTmpConversation) {
            throw Error('There is no conversation bound to this room');
        }

        const { type: convoType } = this.conversation!;
        return convoType === ConversationType.Game ? true : await this.conversationService.isUserInConversation(this.conversation!._id, userId);
    }

    async deleteUser(userId: string) {
        this.userIds.delete(userId);
    }

    private async addMessageToConversation(message: Message) {
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
}
