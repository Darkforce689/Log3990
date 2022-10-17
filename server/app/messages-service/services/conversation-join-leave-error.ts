export enum ConversationJoinError {
    AlreadyJoined = 'USER_ALREADY_IN_CONVERSATION',
    ConversationDoesNotExists = 'CONVERSATION_DOES_NOT_EXISTS',
}

export enum ConversationLeaveError {
    NotJoined = 'USER_NOT_IN_CONVERSATION',
    ConversationDoesNotExists = 'CONVERSATION_DOES_NOT_EXISTS',
}
