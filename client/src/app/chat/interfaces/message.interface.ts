export interface Message {
    content: string;
    from: string;
    date?: Date;
    type: MessageType;
}

export enum MessageType {
    System = 'System',
    FromMe = 'FromMe',
    FromOther = 'FromOther',
}

export interface ChatMessage {
    from: string;
    content: string;
    conversation: string;
    date: Date;
}

export interface BaseMessage {
    content: string;
    conversation: string;
}
