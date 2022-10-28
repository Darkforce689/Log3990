import { AccountController } from '@app/account/account.controller';
import { AuthController } from '@app/auth/controllers/auth.controller';
import { AuthService } from '@app/auth/services/auth.service';
import { SessionMiddlewareService } from '@app/auth/services/session-middleware.service';
import { HttpException } from '@app/classes/http.exception';
import { ENABLE_API_LOGIN } from '@app/constants';
import { DebugController } from '@app/controllers/debug.controller';
import { LeaderboardController } from '@app/controllers/leaderboard-controller/leaderboard.controller';
import { ConversationController } from '@app/messages-service/controllers/conversation.controller';
import { UserController } from '@app/user/controllers/user.controller';
import * as cookieParser from 'cookie-parser';
import * as cors from 'cors';
import * as express from 'express';
import { StatusCodes } from 'http-status-codes';
import * as logger from 'morgan';
import * as swaggerJSDoc from 'swagger-jsdoc';
import * as swaggerUi from 'swagger-ui-express';
import { Service } from 'typedi';

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
        private readonly authController: AuthController,
        private readonly authService: AuthService,
        private readonly sessionMiddlewareService: SessionMiddlewareService,
        private readonly accountController: AccountController,
        private readonly conversationController: ConversationController,
        private readonly userController: UserController,
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
        this.app.use('/api/account', this.accountController.router);
        this.app.use('/api/conversations', this.conversationController.router);
        this.app.use('/api/users', this.userController.router);
        this.errorHandling();
    }

    private config(enableRedisSession: boolean): void {
        const sessionMiddleware = this.sessionMiddlewareService.getSessionMiddleware(enableRedisSession);
        // Middlewares configuration
        this.app.use(sessionMiddleware);

        this.app.use(logger('dev'));
        this.app.use(express.json({ limit: '50mb' }));
        this.app.use(express.urlencoded({ extended: true, limit: '50mb' }));
        this.app.use(cookieParser());
        // TODO put this in env var
        this.app.use(cors({ origin: ['http://localhost:4200'], credentials: true }));
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
