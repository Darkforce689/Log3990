// If you change the order of this enum don't forget to update the DB
export enum ConversationType {
    Chat,
    Game,
}

export interface Conversation extends ConversationCreation {
    _id: string;
    type: ConversationType;
}

export interface ConversationCreation {
    name: string;
}

export interface ConversationDTO {
    _id: string;
    name: string;
}
