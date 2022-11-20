import { AuthService } from '@app/auth/services/auth.service';
import { UserCredentials } from '@app/auth/user-credentials.interface';
import { ServerLogger } from '@app/logger/logger';
import { ConversationService } from '@app/messages-service/services/conversation.service';
import { UserCreation } from '@app/user/interfaces/user-creation.interface';
import { User } from '@app/user/interfaces/user.interface';
import { UserService } from '@app/user/services/user.service';
import { Router } from 'express';
import { StatusCodes } from 'http-status-codes';
import { Service } from 'typedi';

export enum AuthErrors {
    EmailNotFound = 'EMAIL_NOT_FOUND',
    InvalidPassword = 'INVALID_PASSWORD',
    NotAuth = 'NOT_AUTH',
    AlreadyAuth = 'ALREADY_AUTH',
}

@Service()
export class AuthController {
    router: Router;
    constructor(private authService: AuthService, private userService: UserService, private conversationService: ConversationService) {
        this.configureRouter();
    }

    private configureRouter() {
        this.router = Router();

        this.router.get('/logout', async (req, res) => {
            return new Promise((resolve) => {
                const session = req.session as unknown as { userId: string };
                if (session.userId === undefined) {
                    res.sendStatus(StatusCodes.BAD_REQUEST);
                    resolve();
                    return;
                }

                req.session.destroy((error) => {
                    if (error) {
                        ServerLogger.logError(error);
                        res.sendStatus(StatusCodes.INTERNAL_SERVER_ERROR);
                        resolve();
                        return;
                    }
                    res.sendStatus(StatusCodes.OK);
                    resolve();
                });
            });
        });

        this.router.post('/register', async (req, res) => {
            const { email, name, password, avatar } = req.body;
            if (!email || !name || !password || !avatar) {
                return res.sendStatus(StatusCodes.BAD_REQUEST);
            }
            const userCreation: UserCreation = {
                email,
                name,
                avatar,
            };

            const { object: newUser, errors } = await this.userService.createUser(userCreation);
            if (errors.length !== 0) {
                return res.status(StatusCodes.CONFLICT).send({ errors });
            }

            const { _id: userId } = newUser as User;
            const userCreds = {
                email,
                password,
            };

            await this.authService.addCredentialsToUser(userId, userCreds);
            await this.conversationService.addUserToGeneralChannel(userId);
            return res.send({ user: newUser });
        });

        this.router.post('/login', async (req, res) => {
            // User already that already have session
            const { email, password } = req.body;
            if (!email || !password) {
                return res.status(StatusCodes.BAD_REQUEST).send({ errors: ['There is one or more missing field'] });
            }

            const user = await this.userService.getUser({ email });
            if (!user) {
                return res.status(StatusCodes.BAD_REQUEST).send({
                    errors: [AuthErrors.EmailNotFound],
                });
            }

            const userCredentials: UserCredentials = {
                email,
                password,
            };
            const valid = await this.authService.validateCredentials(userCredentials);
            if (!valid) {
                return res.status(StatusCodes.BAD_REQUEST).send({
                    errors: [AuthErrors.InvalidPassword],
                });
            }
            const session = req.session as unknown as { userId: string; loggedIn: boolean };
            const { _id: userId } = user;

            if (this.authService.isUserActive(userId)) {
                return res.status(StatusCodes.CONFLICT).send({ errors: [AuthErrors.AlreadyAuth] });
            }
            session.userId = userId;
            session.loggedIn = true;
            return res.status(StatusCodes.OK).send({ message: 'OK' });
        });

        this.router.get('/validatesession', async (req, res) => {
            const { userId } = req.session as unknown as { userId: string };
            if (userId === undefined) {
                return res.status(StatusCodes.UNAUTHORIZED).send({ errors: [AuthErrors.NotAuth] });
            }

            const activeSessionId = this.authService.getAssignedUserSession(userId);
            if (!activeSessionId) {
                return res.status(StatusCodes.OK).send({ message: 'OK' });
            }

            if (activeSessionId !== req.sessionID) {
                return res.status(StatusCodes.UNAUTHORIZED).send({ errors: [AuthErrors.AlreadyAuth] });
            }
            return res.status(StatusCodes.OK).send({ message: 'OK' });
        });
    }
}
