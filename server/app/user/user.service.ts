/* eslint-disable no-underscore-dangle */
import { SYSTEM_USER_NAME, USER_COLLECTION } from '@app/constants';
import { MongoDBClientService } from '@app/database/mongodb-client.service';
import { ObjectCrudResult } from '@app/database/object-crud-result.interface';
import { ServerLogger } from '@app/logger/logger';
import { GameStats } from '@app/user/interfaces/game-stats.interface';
import { UserCreation } from '@app/user/interfaces/user-creation.interface';
import { User } from '@app/user/interfaces/user.interface';
import { UserQuery } from '@app/user/user-query.interface';
import { ObjectId } from 'mongodb';
import { Service } from 'typedi';

export enum UserCreationError {
    NameAlreadyTaken = 'NAME_ALREADY_TAKEN',
    EmailAlreadyTaken = 'EMAIL_ALREADY_TAKEN',
}
@Service()
export class UserService {
    private systemUser: User | undefined;

    constructor(private mongoService: MongoDBClientService) {}

    private get collection() {
        return this.mongoService.db.collection(USER_COLLECTION);
    }

    async getSystemUser() {
        if (this.systemUser) {
            return this.systemUser;
        }
        const sysUser = await this.collection.findOne({ name: SYSTEM_USER_NAME });
        if (!sysUser) {
            throw Error(`No system user please create one in collection: ${USER_COLLECTION}`);
        }
        this.systemUser = sysUser as User;
        return this.systemUser;
    }

    async createUser(userCreation: UserCreation): Promise<ObjectCrudResult<User>> {
        const errors = [];
        if (await this.isEmailExists(userCreation.email)) {
            errors.push(UserCreationError.EmailAlreadyTaken);
        }

        if (await this.isNameExists(userCreation.name)) {
            errors.push(UserCreationError.NameAlreadyTaken);
        }

        if (errors.length > 0) {
            return {
                errors,
                object: undefined,
            };
        }

        try {
            await this.collection.insertOne(userCreation);
            return {
                errors,
                object: userCreation as User,
            };
        } catch (error) {
            ServerLogger.logError(error);
            return {
                errors: ['UNEXPECTED_ERROR'],
                object: undefined,
            };
        }
    }

    async getUser(query: UserQuery): Promise<User | undefined> {
        // To prevent error when reusing queries multiple times
        const queryClone = {
            ...query,
        };
        const { _id: userId } = queryClone;
        if (!(userId instanceof ObjectId) && userId !== undefined) {
            queryClone._id = new ObjectId(userId);
        }
        const result = await this.collection.findOne(queryClone);
        const user = (result ?? undefined) as User | undefined;
        if (user !== undefined) {
            user._id = user._id.toString();
        }
        return user;
    }

    async updateName(query: UserQuery, userId: string): Promise<string[]> {
        const errors = [];
        try {
            const { name } = query;
            if (!name) {
                throw Error('Name undefined');
            }

            if (await this.isNameExists(name)) {
                errors.push(UserCreationError.NameAlreadyTaken);
            }

            if (errors.length > 0) {
                return errors;
            }

            query._id = new ObjectId(userId);
            if (!this.collection.find({ _id: query._id })) {
                throw Error('No user found.');
            }
            this.collection.updateOne({ _id: query._id }, { $set: { name } });
        } catch (error) {
            ServerLogger.logError(error);
            return ['UNEXPECTED_ERROR'];
        }
        return [];
    }

    async updateAvatar(query: UserQuery, userId: string): Promise<string[]> {
        try {
            const { avatar } = query;
            query._id = new ObjectId(userId);
            if (!this.collection.find({ _id: query._id })) {
                throw Error('No user found.');
            }
            this.collection.updateOne({ _id: query._id }, { $set: { avatar } });
        } catch (error) {
            ServerLogger.logError(error);
            return ['UNEXPECTED_ERROR'];
        }
        return [];
    }

    async updateStatistics(gameStats: GameStats, name: string): Promise<string[]> {
        try {
            const { isWinner, points, totalTime } = gameStats;
            if (!this.collection.find({ name })) {
                throw Error('No user found.');
            }
            const user = (await this.collection.findOne({ name: { $eq: name } })) as unknown as User;
            const averagePoints = user.averagePoints ? user.averagePoints : 0;
            const nGamePlayed = user.nGamePlayed ? user.nGamePlayed : 0;
            const nGameWinned = user.nGameWinned ? user.nGameWinned : 0;
            const averageTimePerGame = user.averageTimePerGame ? user.averageTimePerGame : 0;

            this.collection.updateOne(
                { name },
                {
                    $set: {
                        averagePoints: (averagePoints * nGamePlayed + points) / (nGamePlayed + 1),
                        nGamePlayed: nGamePlayed + 1,
                        nGameWinned: isWinner ? nGameWinned + 1 : nGameWinned,
                        averageTimePerGame: (averageTimePerGame * nGamePlayed + totalTime) / (nGamePlayed + 1),
                    },
                },
            );
        } catch (error) {
            ServerLogger.logError(error);
            return ['UNEXPECTED_ERROR'];
        }
        return [];
    }

    private async isEmailExists(email: string) {
        const result = await this.collection.findOne({ email: { $eq: email } });
        return result !== null;
    }

    private async isNameExists(name: string) {
        const result = await this.collection.findOne({ name: { $eq: name } });
        return result !== null;
    }
}
