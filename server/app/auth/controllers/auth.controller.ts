import { UserCreation } from '@app/user/interfaces/user-creation.interface';
import { UserCredentials } from '@app/auth/user-credentials.interface';
import { AuthService } from '@app/auth/services/auth.service';
import { UserService } from '@app/user/user.service';
import { Router } from 'express';
import { StatusCodes } from 'http-status-codes';
import { Service } from 'typedi';
import { User } from '@app/user/interfaces/user.interface';

@Service()
export class AuthController {
    router: Router;
    constructor(private authService: AuthService, private userService: UserService) {
        this.configureRouter();
    }

    private configureRouter() {
        this.router = Router();

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
            const userCredentials: UserCredentials = {
                email,
                password,
            };
            const valid = await this.authService.validateCredentials(userCredentials);
            if (valid) {
                return res.sendStatus(StatusCodes.OK);
            }
            return res.sendStatus(StatusCodes.BAD_REQUEST);
        });
    }
}
