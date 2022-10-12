import { Service } from 'typedi';
import { createClient, RedisClientType } from 'redis';
import { REDIS_PWD, REDIS_URL, REDIS_USER } from '@app/constants';

@Service()
export class RedisClientService {
    private redis: RedisClientType;

    get client() {
        return this.redis;
    }

    async start(redisConnection = { url: REDIS_URL, username: REDIS_USER, password: REDIS_PWD, legacyMode: true }): Promise<void> {
        this.redis = createClient(redisConnection);
        await this.redis.connect();
    }
}
