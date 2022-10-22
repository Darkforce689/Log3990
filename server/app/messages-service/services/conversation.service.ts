/* eslint-disable no-underscore-dangle */
import { Pagination } from '@app/common/interfaces/pagination.interface';
import { CONVERSATION_COLLECTION, GENERAL_CHANNEL } from '@app/constants';
import { MongoDBClientService } from '@app/database/mongodb-client.service';
import { ObjectCrudResult } from '@app/database/object-crud-result.interface';
import { ServerLogger } from '@app/logger/logger';
import { Conversation, ConversationCreation } from '@app/messages-service/interfaces/conversation.interface';
import { ConversationCrudError } from '@app/messages-service/services/conversation-crud-error';
import { ConversationJoinError, ConversationLeaveError } from '@app/messages-service/services/conversation-join-leave-error';
import { ConversationGetQuery, ConversationSearchQuery } from '@app/messages-service/services/conversation-queries';
import { MessageService } from '@app/messages-service/services/messages-service';
import { ObjectId } from 'mongodb';
import { Service } from 'typedi';

@Service()
export class ConversationService {
    constructor(private mongoService: MongoDBClientService, private messageService: MessageService) {}

    private get collection() {
        return this.mongoService.db.collection(CONVERSATION_COLLECTION);
    }

    async getConversations(pagination: Pagination): Promise<Conversation[]> {
        const { perPage, page } = pagination;
        const conversations = await this.collection
            .find()
            .project({ participants: 0 })
            .skip(perPage * page)
            .limit(perPage)
            .toArray();
        return conversations.map((conversation) => {
            conversation._id = conversation._id.toString();
            return conversation as Conversation;
        });
    }

    async searchConversations(query: ConversationSearchQuery): Promise<Conversation[]> {
        const {
            name,
            pagination: { perPage, page },
        } = query;
        const conversations = await this.collection
            .aggregate([{ $search: { autocomplete: { path: 'name', query: name } } }, { $skip: perPage * page }, { $limit: perPage }])
            .project({ participants: 0 })
            .toArray();
        return conversations.map((conversation) => {
            conversation._id = conversation._id.toString();
            return conversation as Conversation;
        });
    }

    async createConversation(conversationCreation: ConversationCreation): Promise<ObjectCrudResult<Conversation>> {
        const { name } = conversationCreation;
        if (await this.isConversationExisting(name)) {
            return {
                object: undefined,
                errors: [ConversationCrudError.ConversationAlreadyExist],
            };
        }
        const conversation = {
            participants: [],
            ...conversationCreation,
        };
        try {
            await this.collection.insertOne(conversation);
            return {
                errors: [],
                object: conversation as unknown as Conversation,
            };
        } catch (e) {
            ServerLogger.logError(e);
            return {
                object: undefined,
                errors: ['UNEXPECTED_ERROR'],
            };
        }
    }

    async deleteConversation(id: string): Promise<ObjectCrudResult<Conversation>> {
        const conversation = (await this.getConversation({ _id: id })) as Conversation;
        if (!conversation) {
            return {
                object: undefined,
                errors: [ConversationCrudError.ConversationNotFound],
            };
        }

        try {
            await this.collection.deleteOne({ _id: new ObjectId(id) });
            await this.messageService.deleteMessagesFromConversation(id);
            return {
                object: conversation,
                errors: [],
            };
        } catch (e) {
            ServerLogger.logError(e);
            return {
                object: undefined,
                errors: ['UNEXPECTED_ERROR'],
            };
        }
    }

    async getConversation(query: ConversationGetQuery): Promise<Conversation | null> {
        const { name, _id } = query;
        if (_id === undefined && name === undefined) {
            throw Error('Empty conversation get query');
        }

        const conversation =
            _id !== undefined
                ? await this.collection.findOne({ _id: new ObjectId(_id) }, { projection: { participants: 0 } })
                : await this.collection.findOne({ name: { $eq: name } }, { projection: { participants: 0 } });
        if (conversation !== null) {
            conversation._id = conversation._id.toString();
        }
        return conversation as Conversation;
    }

    async getUserConversations(userId: string) {
        const conversations = await this.collection
            .find({ participants: new ObjectId(userId) })
            .project({ participants: 0 })
            .toArray();
        return conversations.map((conversation) => {
            conversation._id = conversation._id.toString();
            return conversation as Conversation;
        });
    }

    async addUserToConversation(conversationId: string, userId: string): Promise<string[]> {
        if (!(await this.isConversationExistingById(conversationId))) {
            return [ConversationJoinError.ConversationDoesNotExists];
        }
        const isUserAlreadyIn = await this.isUserInConversation(conversationId, userId);
        if (isUserAlreadyIn) {
            return [ConversationJoinError.AlreadyJoined];
        }
        const result = await this.collection.updateOne({ _id: new ObjectId(conversationId) }, { $push: { participants: new ObjectId(userId) } });
        if (result.modifiedCount !== 1) {
            return ['UNEXPECTED_ERROR'];
        }
        return [];
    }

    async addUserToGeneralChannel(userId: string) {
        await this.collection.updateOne({ name: GENERAL_CHANNEL }, { $push: { participants: new ObjectId(userId) } });
    }

    async removeUserFromConversation(conversationId: string, userId: string): Promise<string[]> {
        if (!(await this.isConversationExistingById(conversationId))) {
            return [ConversationLeaveError.ConversationDoesNotExists];
        }
        const isUserAlreadyIn = await this.isUserInConversation(conversationId, userId);
        if (!isUserAlreadyIn) {
            return [ConversationLeaveError.NotJoined];
        }
        const result = await this.collection.updateOne({ _id: new ObjectId(conversationId) }, { $pull: { participants: new ObjectId(userId) } });
        if (result.modifiedCount !== 1) {
            return ['UNEXPECTED_ERROR'];
        }
        return [];
    }

    async isUserInConversation(conversationId: string, userId: string) {
        return (await this.collection.findOne({ _id: new ObjectId(conversationId), participants: new ObjectId(userId) })) !== null;
    }

    async isConversationExisting(name: string) {
        const result = await this.collection.findOne({ name: { $eq: name } }, { projection: { participants: 0 } });
        return result !== null;
    }

    async isConversationExistingById(id: string) {
        const result = await this.collection.findOne({ _id: new ObjectId(id) }, { projection: { participants: 0 } });
        return result !== null;
    }
}
