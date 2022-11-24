import { Session } from '@app/auth/services/session.interface';
import { Pagination } from '@app/common/interfaces/pagination.interface';
import { isObjectId, parseNumWithDefault } from '@app/common/utils';
import { DEFAULT_USERS_PAGE, DEFAULT_USERS_PERPAGE } from '@app/user/constants';
import { Invitation } from '@app/user/interfaces/invitations.interface';
import { UserInvitationService } from '@app/user/services/user-invitation.service';
import { UserService } from '@app/user/services/user.service';
import { Router } from 'express';
import { StatusCodes } from 'http-status-codes';
import { Service } from 'typedi';

const USER_NOT_FOUND = 'USER_NOT_FOUND';
const INVALID_TYPE_OR_ARGS = 'INVALID_TYPE_OR_ARGS';

@Service()
export class UserController {
    router: Router;
    constructor(private userService: UserService, private userInvitationService: UserInvitationService) {
        this.configureRouter();
    }

    private configureRouter() {
        this.router = Router();

        this.router.get('/', async (req, res) => {
            const { search, perPage, page } = req.query as { search: string | undefined; perPage: string | undefined; page: string | undefined };
            const { name } = req.query as { name: string };

            if (name) {
                const user = await this.userService.getUser({ name });
                if (!user) {
                    return res.status(StatusCodes.NOT_FOUND).send({ message: USER_NOT_FOUND });
                }
                return res.send({ user });
            }
            const pagination = this.getUsersPagination(perPage, page);
            const users = await this.userService.getUsers({ name: search }, pagination);
            return res.send({ pagination, users });
        });

        this.router.get('/:userId', async (req, res) => {
            const { userId } = req.params;
            const user = await this.userService.getUser({ _id: userId });
            if (!user) {
                return res.status(StatusCodes.NOT_FOUND).send({ message: USER_NOT_FOUND });
            }
            return res.send({ user });
        });

        this.router.post('/:userId/invite', async (req, res) => {
            const { userId: to } = req.params;
            if (!isObjectId(to)) {
                return res.status(StatusCodes.NOT_FOUND).send({ errors: [USER_NOT_FOUND] });
            }

            const isUserExists = await this.userService.isUserExists(to);
            if (!isUserExists) {
                return res.status(StatusCodes.NOT_FOUND).send({ errors: [USER_NOT_FOUND] });
            }

            const { type, args } = req.body;
            if (type === undefined || args === undefined) {
                return res.status(StatusCodes.BAD_REQUEST).send({ errors: [INVALID_TYPE_OR_ARGS] });
            }

            const { userId: from } = req.session as unknown as Session;

            const invitation: Invitation = {
                from,
                to,
                date: new Date(),
                type,
                args,
            };

            this.userInvitationService.send(invitation);
            return res.send({ invitation });
        });
    }

    private getUsersPagination(perPageStr: string | undefined, pageStr: string | undefined): Pagination {
        const perPage = perPageStr === undefined ? DEFAULT_USERS_PERPAGE : parseNumWithDefault(perPageStr, DEFAULT_USERS_PERPAGE);

        const page = pageStr === undefined ? DEFAULT_USERS_PAGE : parseNumWithDefault(pageStr, DEFAULT_USERS_PAGE);

        return {
            perPage,
            page,
        };
    }
}
