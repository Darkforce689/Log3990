import { Component, OnDestroy, OnInit } from '@angular/core';
import { MessagesService } from '@app/game-logic/messages/messages.service';
import { OnlineChatHandlerService } from '@app/game-logic/messages/online-chat-handler/online-chat-handler.service';
import { AccountService } from '@app/services/account.service';
import { Subscription } from 'rxjs';

@Component({
    selector: 'app-prototype-page',
    templateUrl: './prototype-page.component.html',
    styleUrls: ['./prototype-page.component.scss'],
})
export class PrototypePageComponent implements OnInit, OnDestroy {
    private account$$: Subscription;
    constructor(private onlineChat: OnlineChatHandlerService, private messageService: MessagesService, private accountService: AccountService) {}

    ngOnInit(): void {
        this.account$$ = this.accountService.account$.subscribe((user) => {
            if (!user) {
                return;
            }
            this.onlineChat.joinChatRoomWithUser('prototype');
        });
        // this.onlineChat.joinChatRoomWithUser('prototype');
    }

    ngOnDestroy(): void {
        this.account$$.unsubscribe();
        this.onlineChat.leaveChatRoom();
        this.messageService.clearLog();
    }
}
