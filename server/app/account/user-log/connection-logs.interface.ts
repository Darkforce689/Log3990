export enum LogType {
    CONNECTION = 'connection',
    DECONNECTION = 'deconnection',
}

export interface ConnectionLog {
    date: number;
    type: LogType;
}

export interface UserLogs {
    _id: string;
    logs: ConnectionLog[];
}
