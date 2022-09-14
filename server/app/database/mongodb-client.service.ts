import { DATABASE_NAME, DATABASE_URL } from '@app/constants';
import { Db, MongoClient } from 'mongodb';
import { Service } from 'typedi';

@Service()
export class MongoDBClientService {
    private mongoDb: Db;

    get db() {
        return this.mongoDb;
    }

    async start(url: string = DATABASE_URL) {
        try {
            const client = await MongoClient.connect(url);
            this.mongoDb = client.db(DATABASE_NAME);
        } catch {
            throw new Error('Database connection error');
        }
    }
}
