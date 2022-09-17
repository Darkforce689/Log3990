import { UserCreation } from '@app/user/interfaces/user-creation.interface';
import { User } from '@app/user/interfaces/user.interface';
import { USER_COLLECTION } from '@app/constants';
import { MongoDBClientService } from '@app/database/mongodb-client.service';
import { ObjectCrudResult } from '@app/database/object-crud-result.interface';
import { Service } from 'typedi';
import { UserQuery } from '@app/user/user-query.interface';
import { ServerLogger } from '@app/logger/logger';

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
        const result = await this.collection.findOne(query);
        const user = (result ?? undefined) as User | undefined;
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
