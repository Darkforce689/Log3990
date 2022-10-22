import { Pagination } from '@app/common/interfaces/pagination.interface';
import { MESSAGE_COLLECTION } from '@app/constants';
import { MongoDBClientService } from '@app/database/mongodb-client.service';
import { ConvoMessage } from '@app/messages-service/message.interface';
import { ObjectId } from 'mongodb';
import { Service } from 'typedi';

@Service()
export class MessageService {
    private get collection() {
        return this.mongoClient.db.collection(MESSAGE_COLLECTION);
    }
    constructor(private mongoClient: MongoDBClientService) {}

    async addMessage(message: ConvoMessage) {
        this.collection.insertOne(message);
    }

    async getMessagesFromConversation(conversationId: string, pagination: Pagination): Promise<ConvoMessage[]> {
        const { perPage, page, offset: paginationOffset } = pagination;
        const offset = paginationOffset ?? 0;
        const messages = await this.collection
            .find({ conversation: new ObjectId(conversationId) })
            .project({ _id: 0 })
            .sort({ date: -1 })
            .skip(offset + perPage * page)
            .limit(perPage)
            .toArray();
        return messages as ConvoMessage[];
    }

    async deleteMessagesFromConversation(conversationId: string): Promise<void> {
        await this.collection.deleteMany({ conversation: new ObjectId(conversationId) });
    }
}
