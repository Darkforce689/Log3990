import { ComponentPortal } from '@angular/cdk/portal';
import { Component } from '@angular/core';
import { ChatBoxComponent } from '@app/chat/components/chat-box/chat-box.component';
@Component({
    selector: 'app-integrated-chat',
    templateUrl: './integrated-chat.component.html',
    styleUrls: ['./integrated-chat.component.scss'],
})
export class IntegratedChatComponent /* implements AfterViewInit*/ {
    opened: boolean;
    content = 'fdhfdg hfghfhf  dghfgdh  \n fgd g gfdhdfgh fdghg fhdfhdf dfsdfsdfsdfsdfsggccccccccccfdsdfsdfsdfsdfsdfsfds \n';
    chatComponentPortal = new ComponentPortal(ChatBoxComponent);
}
