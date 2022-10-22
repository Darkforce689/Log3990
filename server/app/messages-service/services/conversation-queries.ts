import { Pagination } from '@app/common/interfaces/pagination.interface';

export interface ConversationGetQuery {
    name?: string;
    _id?: string;
}

export interface ConversationSearchQuery {
    pagination: Pagination;
    name: string;
}
