import { UserService } from '@app/user/user.service';
import { Router } from 'express';
import { StatusCodes } from 'http-status-codes';
import { Service } from 'typedi';

@Service()
export class UserController {
    router: Router;
    constructor(private userService: UserService) {
        this.configureRouter();
    }

    private configureRouter() {
        this.router = Router();
        this.router.get('/:userId', async (req, res) => {
            const { userId } = req.params;
            const user = await this.userService.getUser({ _id: userId });
            if (!user) {
                return res.status(StatusCodes.NOT_FOUND).send({ message: 'User not found' });
            }
            return res.send({ user });
        });
    }
}
