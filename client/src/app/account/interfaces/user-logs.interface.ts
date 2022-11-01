export enum LogType {
    CONNECTION = 'connection',
    DECONNECTION = 'deconnection',
}

export interface Log {
    date: Date;
    type: LogType;
}

export interface UserLogs {
    userId: string;
    logs: Log[];
}
