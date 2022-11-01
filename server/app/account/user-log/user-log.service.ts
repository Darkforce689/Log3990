import { ConnectionLog, LogType } from '@app/account/user-log/connection-logs.interface';
import { Pagination } from '@app/common/interfaces/pagination.interface';
import { LOGS_COLLECTION } from '@app/constants';
import { MongoDBClientService } from '@app/database/mongodb-client.service';
import { ServerLogger } from '@app/logger/logger';
import { Service } from 'typedi';

@Service()
export class UserLogService {
    constructor(private mongoService: MongoDBClientService) {}

    private get collection() {
        return this.mongoService.db.collection(LOGS_COLLECTION);
    }

    async updateUserLog(date: number, type: LogType, userId: string) {
        try {
            await this.collection.insertOne({ userId, date, type });
            return [];
        } catch (error) {
            ServerLogger.logError(error);
            return ['UNEXPECTED_ERROR'];
        }
    }

    async getLogHistory(pagination: Pagination, userId: string): Promise<ConnectionLog[]> {
        const { perPage, page } = pagination;
        const result = await this.collection
            .find({ userId })
            .skip(perPage * page)
            .limit(perPage)
            .toArray();
        return result as ConnectionLog[];
    }
}
