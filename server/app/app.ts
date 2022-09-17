import { AuthController } from '@app/auth/controllers/auth.controller';
import { HttpException } from '@app/classes/http.exception';
import { BotInfoController } from '@app/controllers/bot-info.controller';
import { DebugController } from '@app/controllers/debug.controller';
import { DictionaryController } from '@app/controllers/dictionary.controller';
import { LeaderboardController } from '@app/controllers/leaderboard-controller/leaderboard.controller';
import * as cookieParser from 'cookie-parser';
import * as cors from 'cors';
import * as express from 'express';
import { StatusCodes } from 'http-status-codes';
import * as logger from 'morgan';
import * as swaggerJSDoc from 'swagger-jsdoc';
import * as swaggerUi from 'swagger-ui-express';
import * as session from 'express-session';
import * as connectRedis from 'connect-redis';
import { Service } from 'typedi';
import { ENABLE_API_LOGIN, EXPRESS_SESSION_SECRET, SESSION_MAX_AGE } from '@app/constants';
import { AuthService } from '@app/auth/services/auth.service';
import { RedisClientService } from '@app/database/redis-client.service';

@Service()
export class Application {
    app: express.Application;
    private readonly internalError: number = StatusCodes.INTERNAL_SERVER_ERROR;
    private readonly swaggerOptions: swaggerJSDoc.Options = {
        swaggerDefinition: {
            openapi: '3.0.0',
            info: {
                title: 'Cadriciel Serveur',
                version: '1.0.0',
            },
        },
        apis: ['**/*.ts'],
    };

    constructor(
        private readonly debugController: DebugController,
        private readonly leaderboardController: LeaderboardController,
        private readonly botInfoController: BotInfoController,
        private readonly dictionaryController: DictionaryController,
        private readonly authController: AuthController,
        private readonly authService: AuthService,
        private readonly redisClient: RedisClientService,
    ) {}

    start(enableRedisSession = true, enableApiLogin = ENABLE_API_LOGIN): void {
        this.app = express();
        this.config(enableRedisSession);
        this.bindRoutes(enableApiLogin);
    }

    bindRoutes(enableApiLogin: boolean): void {
        this.app.use('/auth/', this.authController.router);
        this.app.use('/api/docs', swaggerUi.serve, swaggerUi.setup(swaggerJSDoc(this.swaggerOptions)));

        if (enableApiLogin) {
            this.app.use(this.authService.authGuard);
        }

        // All users need to be logged in to access routes bellow if ENABLE_API_LOGIN feature flag is enabled
        this.app.use('/api/servergame', this.debugController.router);
        this.app.use('/api/scores', this.leaderboardController.router);
        this.app.use('/api/botinfo', this.botInfoController.router);
        this.app.use('/api/dictionary', this.dictionaryController.router);

        this.errorHandling();
    }

    private config(enableRedisSession: boolean): void {
        // eslint-disable-next-line @typescript-eslint/naming-convention
        const RedisStore = connectRedis(session);

        const store = enableRedisSession ? new RedisStore({ client: this.redisClient.client, disableTouch: true }) : undefined;
        // Middlewares configuration
        this.app.use(
            session({
                store,
                name: 'session-id',
                secret: EXPRESS_SESSION_SECRET,
                saveUninitialized: true,
                resave: false,
                cookie: { maxAge: SESSION_MAX_AGE },
            }),
        );

        this.app.use(logger('dev'));
        this.app.use(express.json({ limit: '50mb' }));
        this.app.use(express.urlencoded({ extended: true, limit: '50mb' }));
        this.app.use(cookieParser());
        this.app.use(cors());
    }

    private errorHandling(): void {
        // When previous handlers have not served a request: path wasn't found
        this.app.use((req: express.Request, res: express.Response, next: express.NextFunction) => {
            const err: HttpException = new HttpException('Not Found');
            next(err);
        });

        // development error handler
        // will print stacktrace
        if (this.app.get('env') === 'development') {
            this.app.use((err: HttpException, req: express.Request, res: express.Response) => {
                res.status(err.status || this.internalError);
                res.send({
                    message: err.message,
                    error: err,
                });
            });
        }

        // production error handler
        // no stacktraces leaked to user (in production env only)
        this.app.use((err: HttpException, req: express.Request, res: express.Response) => {
            res.status(err.status || this.internalError);
            res.send({
                message: err.message,
                error: {},
            });
        });
    }
}
