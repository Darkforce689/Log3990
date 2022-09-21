import { BOT_INFO_COLLECTION, DATABASE_URL, USER_COLLECTION, USER_CREDS_COLLECTION } from '@app/constants';
import { DEFAULT_EASY_BOT, DEFAULT_EXPERT_BOT } from '@app/database/bot-info/default-bot-names';
import {
    DEFAULT_LEADERBOARD_CLASSIC,
    DEFAULT_LEADERBOARD_LOG,
    LEADERBOARD_CLASSIC_COLLECTION,
    LEADERBOARD_LOG_COLLECTION,
} from '@app/database/leaderboard-service/leaderboard-constants';
import { MongoDBClientService } from '@app/database/mongodb-client.service';
import { RedisClientService } from '@app/database/redis-client.service';
import { ServerLogger } from '@app/logger/logger';
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
        this.createBotInfoCollection();
        this.createUserCollection();
        this.createUserCredentialsCollection();
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

    private async isCollectionInDb(name: string): Promise<boolean> {
        const collections = await this.db.listCollections().toArray();
        const collection = collections.find((collectionInDb: CollectionInfo) => collectionInDb.name === name);
        return collection !== undefined;
    }

    private async createBotInfoCollection() {
        try {
            const collectionExists = await this.isCollectionInDb(BOT_INFO_COLLECTION);
            if (collectionExists) {
                return;
            }
            await this.db.createCollection(BOT_INFO_COLLECTION);
            await this.db.collection(BOT_INFO_COLLECTION).createIndex({ name: 1 }, { unique: true });
            this.populateBotInfoCollection();
        } catch (error) {
            ServerLogger.logError(error);
            throw Error('Data base collection creation error');
        }
    }

    private async populateBotInfoCollection() {
        try {
            await this.db.collection(BOT_INFO_COLLECTION).insertMany(DEFAULT_EASY_BOT);
            await this.db.collection(BOT_INFO_COLLECTION).insertMany(DEFAULT_EXPERT_BOT);
        } catch (error) {
            ServerLogger.logError(error);
            throw Error('Data base collection population error');
        }
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
