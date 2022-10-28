import { Pagination } from '@app/chat/interfaces/pagination.interface';

export interface ConversationGetQuery {
    search?: string;
    pagination: Pagination;
}
