import {
    CONVERSATION_COLLECTION,
    DATABASE_URL,
    GENERAL_CHANNEL,
    LOGS_COLLECTION,
    MESSAGE_COLLECTION,
    USER_COLLECTION,
    USER_CREDS_COLLECTION,
} from '@app/constants';
import {
    DEFAULT_LEADERBOARD_CLASSIC,
    DEFAULT_LEADERBOARD_LOG,
    LEADERBOARD_CLASSIC_COLLECTION,
    LEADERBOARD_LOG_COLLECTION,
} from '@app/database/leaderboard-service/leaderboard-constants';
import { MongoDBClientService } from '@app/database/mongodb-client.service';
import { RedisClientService } from '@app/database/redis-client.service';
import { ServerLogger } from '@app/logger/logger';
import { ConversationType } from '@app/messages-service/interfaces/conversation.interface';
import { CollectionInfo, Db } from 'mongodb';
import 'reflect-metadata';
import { Service } from 'typedi';

@Service()
export class DatabaseService {
    private db: Db;

    constructor(private mongodbService: MongoDBClientService, private redisService: RedisClientService) {}

    async start(url: string = DATABASE_URL) {
        // TODO refactor this service
        await this.mongodbService.start(url);
        this.db = this.mongodbService.db;

        this.createLeaderboardCollection(LEADERBOARD_CLASSIC_COLLECTION);
        this.createLeaderboardCollection(LEADERBOARD_LOG_COLLECTION);
        this.createUserCollection();
        this.createUserCredentialsCollection();
        this.createConversationCollection();
        this.createMessagesCollection();
        this.createLogsCollection();
        await this.redisService.start();
    }

    private async createUserCollection() {
        try {
            const isCollectionExist = await this.isCollectionInDb(USER_COLLECTION);
            if (isCollectionExist) {
                return;
            }
            const collection = await this.db.createCollection(USER_COLLECTION);
            await collection.createIndex({ email: 1 }, { unique: true });
            await collection.createIndex({ name: 1 }, { unique: true });
        } catch (e) {
            throw Error('Data base users collection creation error');
        }
    }

    private async createConversationCollection() {
        try {
            const isCollectionExist = await this.isCollectionInDb(CONVERSATION_COLLECTION);
            if (isCollectionExist) {
                await this.purgeGameConversations();
                return;
            }
            const collection = await this.db.createCollection(CONVERSATION_COLLECTION);
            await collection.createIndex({ name: 1 }, { unique: true });
            await collection.createIndex({ participants: 1 }, { unique: true });
            await collection.createIndex({ type: 1 });
            await collection.insertOne({ name: GENERAL_CHANNEL, participants: [] });
            const isUserCollectionExists = await this.isCollectionInDb(USER_COLLECTION);
            if (isUserCollectionExists) {
                this.db
                    .collection(USER_COLLECTION)
                    .find()
                    .forEach((user) => {
                        // eslint-disable-next-line no-underscore-dangle
                        collection.updateOne({ name: GENERAL_CHANNEL }, { $push: { participants: user._id } });
                    });
            }
            ServerLogger.logInfo("Don't forget to create search index for conversation name in mongoatlas");
        } catch (e) {
            throw Error('Data base conversation collection creation error');
        }
    }

    private async purgeGameConversations() {
        const conversations = this.db.collection(CONVERSATION_COLLECTION);
        if (await this.isCollectionInDb(MESSAGE_COLLECTION)) {
            const messages = this.db.collection(MESSAGE_COLLECTION);
            await conversations.find({ type: ConversationType.Game }).forEach((conversation) => {
                // eslint-disable-next-line no-underscore-dangle
                messages.deleteMany({ conversation: conversation._id });
            });
        }
        await conversations.deleteMany({ type: ConversationType.Game });
    }

    private async createMessagesCollection() {
        try {
            const isCollectionExist = await this.isCollectionInDb(MESSAGE_COLLECTION);
            if (isCollectionExist) {
                return;
            }
            // TODO add sort for date etc
            const collection = await this.db.createCollection(MESSAGE_COLLECTION);
            await collection.createIndex({ conversation: 1 }, { unique: false });
            await collection.createIndex({ date: -1 }, { unique: false });
        } catch (e) {
            throw Error('Data base messages collection creation error');
        }
    }

    private async createUserCredentialsCollection() {
        try {
            const isCollectionExist = await this.isCollectionInDb(USER_COLLECTION);
            if (isCollectionExist) {
                return;
            }
            const collection = await this.db.createCollection(USER_CREDS_COLLECTION);
            await collection.createIndex({ email: 1 }, { unique: true });
        } catch (e) {
            throw Error('Data base users collection creation error');
        }
    }

    private async createLeaderboardCollection(collectionName: string): Promise<void> {
        try {
            const collectionExists = await this.isCollectionInDb(collectionName);
            if (collectionExists) {
                return;
            }
            await this.db.createCollection(collectionName);
            await this.db.collection(collectionName).createIndex({ name: 1 }, { unique: true });
            await this.populateLeaderboardCollection(collectionName);
        } catch (error) {
            ServerLogger.logError(error);
            throw Error('Data base collection creation error');
        }
    }

    private async createLogsCollection() {
        try {
            const isCollectionExist = await this.isCollectionInDb(LOGS_COLLECTION);
            if (isCollectionExist) {
                return;
            }
            await this.db.createCollection(LOGS_COLLECTION);
        } catch (e) {
            throw Error('Data base logs collection creation error');
        }
    }

    private async isCollectionInDb(name: string): Promise<boolean> {
        const collections = await this.db.listCollections().toArray();
        const collection = collections.find((collectionInDb: CollectionInfo) => collectionInDb.name === name);
        return collection !== undefined;
    }

    private async populateLeaderboardCollection(name: string): Promise<void> {
        try {
            const defaultPopulation = name === LEADERBOARD_CLASSIC_COLLECTION ? DEFAULT_LEADERBOARD_CLASSIC : DEFAULT_LEADERBOARD_LOG;
            if ((await this.db.collection(name).countDocuments()) === 0) {
                await this.db.collection(name).insertMany(defaultPopulation);
            }
        } catch (error) {
            ServerLogger.logError(error);
            throw Error('Data base collection population error');
        }
    }

    get database(): Db {
        return this.db;
    }
}
