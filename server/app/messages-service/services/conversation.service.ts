/* eslint-disable no-underscore-dangle */
import { Pagination } from '@app/common/interfaces/pagination.interface';
import { CONVERSATION_COLLECTION, GAME_CONVO_NAME, GENERAL_CHANNEL } from '@app/constants';
import { MongoDBClientService } from '@app/database/mongodb-client.service';
import { ObjectCrudResult } from '@app/database/object-crud-result.interface';
import { ServerLogger } from '@app/logger/logger';
import { Conversation, ConversationCreation, ConversationDTO, ConversationType } from '@app/messages-service/interfaces/conversation.interface';
import { ConversationCrudError, GameConversationCrudError } from '@app/messages-service/services/conversation-crud-error';
import { ConversationJoinError, ConversationLeaveError } from '@app/messages-service/services/conversation-join-leave-error';
import { ConversationGetQuery, ConversationSearchQuery } from '@app/messages-service/services/conversation-queries';
import { MessageService } from '@app/messages-service/services/messages-service';
import { isGameToken } from '@app/messages-service/utils';
import { ObjectId } from 'mongodb';
import { Observable, Subject } from 'rxjs';
import { Service } from 'typedi';

@Service()
export class ConversationService {
    private deletedConvoSubject = new Subject<Conversation>();
    get deletedConversation$(): Observable<Conversation> {
        return this.deletedConvoSubject;
    }

    constructor(private mongoService: MongoDBClientService, private messageService: MessageService) {}

    private get collection() {
        return this.mongoService.db.collection(CONVERSATION_COLLECTION);
    }

    async getConversations(pagination: Pagination): Promise<ConversationDTO[]> {
        // Can only get chat conversations
        const { perPage, page } = pagination;
        const conversations = await this.collection
            .find({ type: ConversationType.Chat })
            .project({ participants: 0, type: 0 })
            .skip(perPage * page)
            .limit(perPage)
            .toArray();
        return conversations.map((conversation) => {
            conversation._id = conversation._id.toString();
            return conversation as Conversation;
        });
    }

    async searchConversations(query: ConversationSearchQuery): Promise<ConversationDTO[]> {
        // Can only search on chat conversations
        const {
            name,
            pagination: { perPage, page },
        } = query;
        const conversations = await this.collection
            .aggregate([
                { $search: { autocomplete: { path: 'name', query: name } } },
                { $match: { type: ConversationType.Chat } },
                { $skip: perPage * page },
                { $limit: perPage },
            ])
            .project({ participants: 0, type: 0 })
            .toArray();
        return conversations.map((conversation) => {
            conversation._id = conversation._id.toString();
            return conversation as Conversation;
        });
    }

    async getCreatedConversations(creator: string) {
        const conversations = await this.collection
            .find({ creator: new ObjectId(creator) })
            .project({ participants: 0, type: 0 })
            .toArray();
        return conversations.map((conversation) => {
            conversation._id = conversation._id.toString();
            return conversation as Conversation;
        });
    }

    async createConversation(conversationCreation: ConversationCreation): Promise<ObjectCrudResult<Conversation>> {
        const { name, creator } = conversationCreation;

        if (this.isForbidenName(name)) {
            return {
                object: undefined,
                errors: [ConversationCrudError.ConversationCreationForbiden],
            };
        }

        if (await this.isConversationExisting(name)) {
            return {
                object: undefined,
                errors: [ConversationCrudError.ConversationAlreadyExist],
            };
        }

        const conversation = {
            participants: [],
            type: ConversationType.Chat,
            name,
            creator: new ObjectId(creator),
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

    async createGameConversation(gameToken: string): Promise<ObjectCrudResult<Conversation>> {
        if (!isGameToken(gameToken)) {
            return {
                object: undefined,
                errors: [GameConversationCrudError.InvalidGameToken],
            };
        }

        try {
            const gameConversation = {
                name: gameToken,
                type: ConversationType.Game,
            };
            await this.collection.insertOne(gameConversation);
            return {
                object: gameConversation as Conversation,
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

    async deleteConversation(id: string, deleter: string): Promise<ObjectCrudResult<Conversation>> {
        const conversation = (await this.getConversation({ _id: id })) as Conversation | null;
        if (!conversation) {
            return {
                object: undefined,
                errors: [ConversationCrudError.ConversationNotFound],
            };
        }

        const { creator: creatorObjectId } = conversation as unknown as { creator: ObjectId };
        const creator = creatorObjectId === undefined ? undefined : creatorObjectId.toString();
        if (creator !== deleter) {
            return {
                object: undefined,
                errors: [ConversationCrudError.ConversationDeletionForbiden],
            };
        }

        try {
            await this.collection.deleteOne({ _id: new ObjectId(id) });
            await this.messageService.deleteMessagesFromConversation(id);
            this.deletedConvoSubject.next(conversation);
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

    async deleteGameConversation(gameToken: string) {
        const conversation = (await this.getConversation({ name: gameToken })) as Conversation | null;
        if (!conversation) {
            return;
        }
        try {
            await this.collection.deleteOne({ name: gameToken });
            await this.messageService.deleteMessagesFromConversation(conversation._id);
        } catch (e) {
            ServerLogger.logError(e);
        }
    }

    async getConversation(query: ConversationGetQuery): Promise<Conversation | null> {
        const { name, _id } = query;
        if (_id === undefined && name === undefined) {
            throw Error('Empty conversation get query');
        }

        // To prioritize _id search
        const getQuery = _id !== undefined ? { _id: new ObjectId(_id) } : { name: { $eq: name } };
        const conversation = await this.collection.findOne(getQuery, { projection: { participants: 0 } });
        if (conversation !== null) {
            conversation._id = conversation._id.toString();
        }
        return conversation as Conversation;
    }

    async getUserConversations(userId: string): Promise<Conversation[]> {
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

    private isForbidenName(name: string) {
        return GENERAL_CHANNEL === name || GAME_CONVO_NAME === name || isGameToken(name);
    }
}
