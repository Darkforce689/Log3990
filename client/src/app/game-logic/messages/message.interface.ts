export interface Message {
    content: string;
    from: string;
    date?: Date;
    type: MessageType;
}

export enum MessageType {
    System = 'System',
    Player1 = 'Player1',
    Player2 = 'Player2',
}

export interface ChatMessage {
    from: string;
    content: string;
    date: Date;
}
