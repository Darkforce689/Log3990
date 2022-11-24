import { UserStatus } from '@app/user/interfaces/user.interface';
import { ObjectId } from 'mongodb';

export interface UserQuery {
    email?: string;
    name?: string;
    _id?: string | ObjectId;
    avatar?: string;
}

export interface UsersGetQuery {
    name?: string;
    status?: UserStatus;
}
