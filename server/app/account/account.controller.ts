import { Session } from '@app/auth/services/session.interface';
import { User } from '@app/user/interfaces/user.interface';
import { UserService } from '@app/user/user.service';
import { Router } from 'express';
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
    }
}
