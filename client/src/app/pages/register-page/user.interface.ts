// TODO add in user service or component dir
export interface User {
    _id: string;
    name: string;
    email: string;
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
