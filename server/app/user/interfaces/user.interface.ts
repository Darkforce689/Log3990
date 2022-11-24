import { UserCreation } from '@app/user/interfaces/user-creation.interface';

export interface User extends UserCreation {
    _id: string;
    email: string;
    name: string;
    avatar: string;
    averagePoints: number;
    nGamePlayed: number;
    nGameWon: number;
    averageTimePerGame: number;
    status: UserStatus;
    totalExp: number;
}

export enum UserStatus {
    Online = 'ONLINE',
    Offline = 'OFFLINE',
}
