import { EXPRESS_SESSION_SECRET, SESSION_MAX_AGE } from '@app/constants';
import { RedisClientService } from '@app/database/redis-client.service';
import * as connectRedis from 'connect-redis';
import { NextFunction, Request, Response } from 'express';
import * as session from 'express-session';
import { Socket } from 'socket.io';
import { ExtendedError } from 'socket.io/dist/namespace';
import { DefaultEventsMap } from 'socket.io/dist/typed-events';
import { Service } from 'typedi';

@Service()
export class SessionMiddlewareService {
    constructor(private redisClient: RedisClientService) {}

    getSessionMiddleware(enableRedisSession: boolean = true) {
        const store = this.createStore(enableRedisSession);
        return session({
            store,
            name: 'session-id',
            secret: EXPRESS_SESSION_SECRET,
            saveUninitialized: false,
            resave: false,
            cookie: { maxAge: SESSION_MAX_AGE },
        });
    }

    getSocketSessionMiddleware(enableRedisSession: boolean = true) {
        const middleware = this.getSessionMiddleware(enableRedisSession);
        return (socket: Socket<DefaultEventsMap, DefaultEventsMap, DefaultEventsMap>, next: (err?: ExtendedError | undefined) => void) => {
            return middleware(socket.request as unknown as Request, {} as unknown as Response, next as NextFunction);
        };
    }

    private createStore(enableRedisSession: boolean = true) {
        // eslint-disable-next-line @typescript-eslint/naming-convention
        const RedisStore = connectRedis(session);
        const store = enableRedisSession ? new RedisStore({ client: this.redisClient.client, disableTouch: true }) : undefined;
        return store;
    }
}
