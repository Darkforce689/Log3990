import { Injectable } from '@angular/core';
import { SYSTEM_USER_NAME } from '@app/chat/constants';
import { ChatMessage, Message, MessageType } from '@app/chat/interfaces/message.interface';
import { AccountService } from '@app/services/account.service';
import { UserCacheService } from '@app/users/services/user-cache.service';
import { Observable, ReplaySubject } from 'rxjs';

@Injectable({
    providedIn: 'root',
})
export class MessageFactoryService {
    constructor(private accountService: AccountService, private userCache: UserCacheService) {}

    createMessage(chatMessage: ChatMessage): Observable<Message> {
        if (!this.accountService.account) {
            throw Error('User account not fetched yet');
        }
        const { _id: accountId, name: accountName } = this.accountService.account;
        const isAccountMessage = chatMessage.from === accountId;

        const { content, date } = chatMessage;
        const subject = new ReplaySubject<Message>();
        if (isAccountMessage) {
            subject.next({
                from: accountName,
                content,
                date,
                type: MessageType.FromMe,
            });
            return subject;
        }

        this.userCache.getUser(chatMessage.from).subscribe((user) => {
            const from = user === undefined ? 'USER_DOES_NOT_EXIST' : user.name;
            const type = from === SYSTEM_USER_NAME ? MessageType.System : MessageType.FromOther;
            subject.next({
                from,
                content,
                date,
                type,
            });
        });
        return subject;
    }
}
