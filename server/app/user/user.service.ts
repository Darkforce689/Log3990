import { USER_COLLECTION } from '@app/constants';
import { MongoDBClientService } from '@app/database/mongodb-client.service';
import { ObjectCrudResult } from '@app/database/object-crud-result.interface';
import { ServerLogger } from '@app/logger/logger';
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
    constructor(private mongoService: MongoDBClientService) {}

    private get collection() {
        return this.mongoService.db.collection(USER_COLLECTION);
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
            // eslint-disable-next-line no-underscore-dangle
            queryClone._id = new ObjectId(userId);
        }
        const result = await this.collection.findOne(queryClone);
        const user = (result ?? undefined) as User | undefined;
        if (user !== undefined) {
            // eslint-disable-next-line no-underscore-dangle
            user._id = user._id.toString();
        }
        return user;
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
