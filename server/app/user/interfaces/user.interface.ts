import { UserCreation } from '@app/user/interfaces/user-creation.interface';

export interface User extends UserCreation {
    _id: string;
    email: string;
    name: string;
}
