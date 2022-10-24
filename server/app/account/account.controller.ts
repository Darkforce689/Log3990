import { Session } from '@app/auth/services/session.interface';
import { User } from '@app/user/interfaces/user.interface';
import { UserService } from '@app/user/user.service';
import { Router } from 'express';
import { StatusCodes } from 'http-status-codes';
import { Service } from 'typedi';

@Service()
export class AccountController {
    router: Router;
    constructor(private userService: UserService) {
        this.configureRouter();
    }

    private configureRouter() {
        this.router = Router();

        this.router.get('', async (req, res) => {
            const { userId } = req.session as unknown as Session;
            // Safe because of auth middleware
            const user = (await this.userService.getUser({ _id: userId })) as User;
            return res.send(user);
        });

        this.router.patch('', async (req, res) => {
            const { userId } = req.session as unknown as Session;
            const errors: string[] = [];
            if (req.body.name) {
                errors.push(...(await this.userService.updateName(req.body, userId)));
                if (errors.length > 0) {
                    res.status(StatusCodes.CONFLICT).send({ errors });
                    return;
                }
            }
            if (req.body.avatar) {
                errors.push(...(await this.userService.updateAvatar(req.body, userId)));
                if (errors.length > 0) {
                    res.status(StatusCodes.BAD_REQUEST);
                    return;
                }
            }
            res.status(StatusCodes.OK).send({ message: 'OK' });
        });
    }
}
