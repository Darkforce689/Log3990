import { UserCreation } from '@app/user/interfaces/user-creation.interface';
import { UserCredentials } from '@app/auth/user-credentials.interface';
import { AuthService } from '@app/auth/services/auth.service';
import { UserService } from '@app/user/user.service';
import { Router } from 'express';
import { StatusCodes } from 'http-status-codes';
import { Service } from 'typedi';
import { User } from '@app/user/interfaces/user.interface';
import { ServerLogger } from '@app/logger/logger';

export enum AuthErrors {
    EmailNotFound = 'EMAIL_NOT_FOUND',
    InvalidPassword = 'INVALID_PASSWORD',
}

@Service()
export class AuthController {
    router: Router;
    constructor(private authService: AuthService, private userService: UserService) {
        this.configureRouter();
    }

    private configureRouter() {
        this.router = Router();

        this.router.get('/logout', async (req, res) => {
            return new Promise((resolve) => {
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
            const { email, name, password } = req.body;
            if (!email || !name || !password) {
                return res.sendStatus(StatusCodes.BAD_REQUEST);
            }
            const userCreation: UserCreation = {
                email,
                name,
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
            return res.send(newUser);
        });

        this.router.post('/login', async (req, res) => {
            const { email, password } = req.body;
            if (!email || !password) {
                return res.sendStatus(StatusCodes.BAD_REQUEST);
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
            session.userId = userId;
            session.loggedIn = true;
            return res.sendStatus(StatusCodes.OK);
        });
    }
}
