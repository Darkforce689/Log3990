import { ObjectId } from 'mongodb';

export interface Message extends BaseMessage {
    from: string;
    date: Date;
}

export interface ConvoMessage {
    conversation: ObjectId;
    content: string;
    from: ObjectId;
    date: Date;
}

export interface BaseMessage {
    content: string;
    conversation: string;
}
