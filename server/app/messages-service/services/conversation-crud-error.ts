export enum ConversationCrudError {
    ConversationAlreadyExist = 'CONVERSATION_ALREADY_EXIST',
    ConversationNotFound = 'CONVERSATION_NOT_FOUND',
    ConversationCreationForbiden = 'CONVERSATION_NAME_FORBIDEN',
    ConversationDeletionForbiden = 'CONVERSATION_DELETION_FORBIDEN',
}

export enum GameConversationCrudError {
    ConversationAlreadyCreated = 'CONVERSATION_ALREADY_EXIST',
    InvalidGameToken = 'INVALID_GAMETOKEN',
}
