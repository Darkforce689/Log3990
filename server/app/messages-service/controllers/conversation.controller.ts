import { Session } from '@app/auth/services/session.interface';
import { Pagination } from '@app/common/interfaces/pagination.interface';
import { isObjectId, parseBooleanQueryParam, parseNumWithDefault } from '@app/common/utils';
import {
    CONVERSATION_PAGINATION_DEFAULT_PAGE,
    CONVERSATION_PAGINATION_DEFAULT_PERPAGE,
    GENERAL_CHANNEL,
    MESSAGE_PAGINATION_DEFAULT_PAGE,
    MESSAGE_PAGINATION_DEFAULT_PERPAGE,
} from '@app/constants';
import { ConversationJoinError } from '@app/messages-service/services/conversation-join-leave-error';
import { ConversationService } from '@app/messages-service/services/conversation.service';
import { MessageService } from '@app/messages-service/services/messages-service';
import { isGameToken } from '@app/messages-service/utils';
import { Router } from 'express';
import { StatusCodes } from 'http-status-codes';
import { Service } from 'typedi';

@Service()
export class ConversationController {
    router: Router;
    constructor(private conversationService: ConversationService, private messageService: MessageService) {
        this.configureRouter();
    }

    private configureRouter() {
        this.router = Router();

        this.router.get('/', async (req, res) => {
            const { search, perPage, page, joined, created } = req.query;

            // if joined true add all result in one page
            if (parseBooleanQueryParam(joined as string | undefined, false)) {
                const { userId } = req.session as unknown as Session;
                const myConversations = await this.conversationService.getUserConversations(userId);
                return res.send({ pagination: { page: 0, perPage: myConversations.length }, conversations: myConversations });
            }

            // if created true add all result in one page
            if (parseBooleanQueryParam(created as string | undefined, false)) {
                const { userId } = req.session as unknown as Session;
                const createdConversations = await this.conversationService.getCreatedConversations(userId);
                return res.send({ pagination: { page: 0, perPage: createdConversations.length }, conversations: createdConversations });
            }

            const pagination = this.getConversationPagination(perPage as string | undefined, page as string | undefined);

            const conversations =
                search === undefined
                    ? await this.conversationService.getConversations(pagination)
                    : await this.conversationService.searchConversations({ name: search as string, pagination });
            return res.send({ pagination, conversations });
        });

        this.router.post('/', async (req, res) => {
            const { name } = req.body;
            if (!name) {
                return res.status(StatusCodes.BAD_REQUEST).send({ errors: ['No name was given in the body'] });
            }
            const { userId: creator } = req.session as unknown as Session;
            const { object: conversation, errors } = await this.conversationService.createConversation({ name, creator });
            if (errors.length !== 0) {
                return res.status(StatusCodes.CONFLICT).send({ errors });
            }
            return res.send({ conversation });
        });

        this.router.delete('/:conversationId', async (req, res) => {
            const { conversationId } = req.params;
            if (!isObjectId(conversationId)) {
                return res.status(StatusCodes.NOT_FOUND).send({ errors: ['This conversation does not exist'] });
            }
            const conversation = await this.conversationService.getConversation({ _id: conversationId });
            if (!conversation) {
                return res.status(StatusCodes.NOT_FOUND).send({ errors: ['This conversation does not exist'] });
            }

            if (conversation.name === GENERAL_CHANNEL) {
                return res.status(StatusCodes.BAD_REQUEST).send({ errors: ['You cannot delete the general channel'] });
            }
            const { userId } = req.session as unknown as Session;
            const { errors } = await this.conversationService.deleteConversation(conversationId as string, userId);
            if (errors.length !== 0) {
                return res.status(StatusCodes.NOT_FOUND).send({ errors });
            }
            return res.send({ conversation });
        });

        this.router.get('/:conversationId', async (req, res) => {
            const { conversationId } = req.params;
            if (!isObjectId(conversationId)) {
                return res.status(StatusCodes.NOT_FOUND).send({ errors: ['This conversation does not exist'] });
            }

            const conversation = await this.conversationService.getConversation({ _id: conversationId });
            if (!conversation) {
                return res.status(StatusCodes.NOT_FOUND).send({ errors: ['This conversation does not exist'] });
            }
            return res.send({ conversation });
        });

        this.router.get('/:conversationId/messages', async (req, res) => {
            // we can use gametoken as conversationId here for usage simplicity
            const { conversationId } = req.params;
            if (!conversationId) {
                return res.status(StatusCodes.BAD_REQUEST).send({ errors: ['Invalid conversation id'] });
            }
            const isGameConvo = isGameToken(conversationId);
            if (!isObjectId(conversationId) && !isGameConvo) {
                return res.status(StatusCodes.NOT_FOUND).send({ errors: ['This conversation does not exist'] });
            }
            const getQuery = isGameConvo ? { name: conversationId } : { _id: conversationId };
            const conversation = await this.conversationService.getConversation(getQuery);
            if (!conversation) {
                return res.status(StatusCodes.NOT_FOUND).send({ errors: ['This conversation does not exist'] });
            }
            const { perPage, page, offset } = req.query;
            const pagination = this.getMessagePagination(perPage as string | undefined, page as string | undefined, offset as string | undefined);
            // eslint-disable-next-line no-underscore-dangle
            const messages = await this.messageService.getMessagesFromConversation(conversation._id, pagination);
            return res.send({ pagination, messages });
        });

        this.router.get('/:conversationId/join', async (req, res) => {
            const { conversationId } = req.params;
            if (!isObjectId(conversationId)) {
                return res.status(StatusCodes.NOT_FOUND).send({ errors: ['This conversation does not exist'] });
            }
            const conversation = await this.conversationService.getConversation({ _id: conversationId });
            if (!conversation) {
                return res.status(StatusCodes.NOT_FOUND).send({ errors: ['This conversation does not exist'] });
            }

            const { userId } = req.session as unknown as Session;
            const errors: string[] = await this.conversationService.addUserToConversation(conversationId, userId);
            if (errors.length !== 0) {
                const status = errors.includes(ConversationJoinError.AlreadyJoined) ? StatusCodes.CONFLICT : StatusCodes.BAD_REQUEST;
                return res.status(status).send({ errors });
            }
            return res.send({ message: 'Joined successfully' });
        });

        this.router.get('/:conversationId/quit', async (req, res) => {
            const { conversationId } = req.params;
            if (!isObjectId(conversationId)) {
                return res.status(StatusCodes.NOT_FOUND).send({ errors: ['This conversation does not exist'] });
            }
            const conversation = await this.conversationService.getConversation({ _id: conversationId });
            if (!conversation) {
                return res.status(StatusCodes.NOT_FOUND).send({ errors: ['This conversation does not exist'] });
            }

            if (conversation.name === GENERAL_CHANNEL) {
                return res.status(StatusCodes.BAD_REQUEST).send({ errors: ['You cannot quit the general channel'] });
            }
            const { userId } = req.session as unknown as Session;
            const errors: string[] = await this.conversationService.removeUserFromConversation(conversationId, userId);
            if (errors.length !== 0) {
                return res.status(StatusCodes.BAD_REQUEST).send({ errors });
            }
            return res.send({ message: 'Joined successfully' });
        });
    }

    private getMessagePagination(perPageStr: string | undefined, pageStr: string | undefined, offsetStr?: string | undefined): Pagination {
        const perPage =
            perPageStr === undefined ? MESSAGE_PAGINATION_DEFAULT_PERPAGE : parseNumWithDefault(perPageStr, MESSAGE_PAGINATION_DEFAULT_PERPAGE);

        const page = pageStr === undefined ? MESSAGE_PAGINATION_DEFAULT_PAGE : parseNumWithDefault(pageStr, MESSAGE_PAGINATION_DEFAULT_PAGE);
        const offset = offsetStr === undefined ? 0 : parseNumWithDefault(offsetStr, 0);
        return {
            perPage,
            page,
            offset,
        };
    }

    private getConversationPagination(perPageStr: string | undefined, pageStr: string | undefined): Pagination {
        const perPage =
            perPageStr === undefined
                ? CONVERSATION_PAGINATION_DEFAULT_PERPAGE
                : parseNumWithDefault(perPageStr, CONVERSATION_PAGINATION_DEFAULT_PERPAGE);

        const page =
            pageStr === undefined ? CONVERSATION_PAGINATION_DEFAULT_PAGE : parseNumWithDefault(pageStr, CONVERSATION_PAGINATION_DEFAULT_PAGE);

        return {
            perPage,
            page,
        };
    }
}
