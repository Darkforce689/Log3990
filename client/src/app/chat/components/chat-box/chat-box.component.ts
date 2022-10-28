import { AfterViewInit, ChangeDetectorRef, Component, ElementRef, EventEmitter, OnDestroy, Output, ViewChild } from '@angular/core';
import { Message } from '@app/chat/interfaces/message.interface';
import { ConversationService } from '@app/chat/services/conversation/conversation.service';
import { MessagesService, MessageUpdateReason } from '@app/chat/services/messages/messages.service';
import { NOT_ONLY_SPACE_RGX } from '@app/game-logic/constants';
import { InputComponent, InputType, UIInput } from '@app/game-logic/interfaces/ui-input';
import { BehaviorSubject } from 'rxjs';

const MAX_MESSAGE_LENGTH = 512;
const IS_BOTTOM_ERROR = 0.01;

enum ScrollGrowDirection {
    Up,
    Down,
}

@Component({
    selector: 'app-chat-box',
    templateUrl: './chat-box.component.html',
    styleUrls: ['./chat-box.component.scss'],
})
export class ChatBoxComponent implements AfterViewInit, OnDestroy {
    @ViewChild('chat', { read: ElementRef }) chat: ElementRef;
    @Output() clickChatbox = new EventEmitter();

    readonly self = InputComponent.Chatbox;

    messageContent: string;

    messages$: BehaviorSubject<Message[]> = new BehaviorSubject<Message[]>([] as Message[]);
    readonly maxMessageLength = MAX_MESSAGE_LENGTH;

    private previousChatScroll: { scrollTop: number; scrollHeight: number; offsetHeight: number };
    private isChangingConvo = false;
    constructor(private messageService: MessagesService, private cdRef: ChangeDetectorRef, private conversationService: ConversationService) {}

    ngOnDestroy(): void {
        // TODO implement
        this.messageService.disconnect();
    }

    ngAfterViewInit(): void {
        this.cdRef.detectChanges();
        this.messageService.messages$.subscribe(({ messages, reason }) => {
            this.messages$.next(messages);
            this.cdRef.detectChanges();
            this.updateChatScroll(reason);
        });
        this.conversationService.currentConversation$.subscribe(() => {
            this.isChangingConvo = true;
        });
    }

    click() {
        const input: UIInput = { from: this.self, type: InputType.LeftClick };
        this.clickChatbox.emit(input);
    }

    sendMessage() {
        const content = this.messageContent;
        if (!this.isMessageValid(content)) {
            return;
        }

        this.messageService.receiveNonDistributedPlayerMessage(content);

        this.resetMessageContent();
    }

    onChatScroll() {
        const { scrollTop, scrollHeight, offsetHeight } = this.chat.nativeElement;
        this.previousChatScroll = { scrollTop, scrollHeight, offsetHeight };
        const isTop = scrollTop === 0;
        if (isTop && !this.isChangingConvo) {
            this.messageService.fetchNextMessagesFromCurrentConvo();
        }
    }

    private resetMessageContent() {
        this.messageContent = '';
    }

    private isMessageValid(messageContent: string): boolean {
        if (!messageContent) {
            return false;
        }
        const content = messageContent;
        return content.length !== 0 && content.length <= MAX_MESSAGE_LENGTH && NOT_ONLY_SPACE_RGX.test(content);
    }

    private updateChatScroll(reason: MessageUpdateReason) {
        if (this.isChangingConvo) {
            this.scrollDownChat();
            this.isChangingConvo = false;
            return;
        }

        if (reason === MessageUpdateReason.ReceivedPreviouslySentMessage) {
            this.scrollDownChat();
            return;
        }

        if (reason === MessageUpdateReason.ReceiveMessageFromOther) {
            const growDirection = this.isFocusedOnLastMessage() ? ScrollGrowDirection.Up : ScrollGrowDirection.Down;
            this.staySamePlaceInChat(growDirection);
            return;
        }

        this.staySamePlaceInChat(ScrollGrowDirection.Up);
    }

    private isFocusedOnLastMessage() {
        if (!this.previousChatScroll) {
            return true;
        }
        const { scrollHeight, scrollTop, offsetHeight } = this.previousChatScroll;
        return scrollTop + offsetHeight + IS_BOTTOM_ERROR * scrollHeight >= scrollHeight;
    }

    private scrollDownChat() {
        const chatNativeElement = this.chat.nativeElement;
        chatNativeElement.scrollTop = chatNativeElement.scrollHeight;
    }

    private staySamePlaceInChat(growDirection: ScrollGrowDirection) {
        const chatNativeElement = this.chat.nativeElement;
        const { scrollHeight, scrollTop } = chatNativeElement;
        const { scrollTop: prevScrollTop, scrollHeight: prevScrollHeight } = this.previousChatScroll ?? { scrollHeight, scrollTop };
        const scrollDelta = growDirection === ScrollGrowDirection.Up ? scrollHeight - prevScrollHeight : 0;
        chatNativeElement.scrollTop = prevScrollTop + scrollDelta;
    }
}
