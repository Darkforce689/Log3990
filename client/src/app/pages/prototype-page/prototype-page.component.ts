import { Component, OnInit } from '@angular/core';
import { GameInfoService } from '@app/game-logic/game/game-info/game-info.service';
import { OnlineChatHandlerService } from '@app/game-logic/messages/online-chat-handler/online-chat-handler.service';
import { User } from '@app/game-logic/player/user';
import * as uuid from 'uuid';

@Component({
    selector: 'app-prototype-page',
    templateUrl: './prototype-page.component.html',
    styleUrls: ['./prototype-page.component.scss'],
})
export class PrototypePageComponent implements OnInit {
    constructor(private onlineChat: OnlineChatHandlerService, private gameInfo: GameInfoService) {}

    ngOnInit(): void {
        const name = this.genUserName();
        this.gameInfo.user = new User(name);
        this.onlineChat.joinChatRoom('prototype', name);
    }

    private genUserName() {
        const id = uuid.v4();
        return `proto-user-${id}`;
    }
}
