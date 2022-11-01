import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ChatMessage, Message, MessageType, SystemMessage } from '@app/chat/interfaces/message.interface';
import { MessagesService } from '@app/chat/services/messages/messages.service';
import { OnlineChatHandlerService } from '@app/chat/services/online-chat-handler/online-chat-handler.service';
import { Observable, Subject } from 'rxjs';

describe('Service: Messages', () => {
    let service: MessagesService;
    let onlineChatSpy: jasmine.SpyObj<OnlineChatHandlerService>;
    let mockNewMessage$$: Subject<ChatMessage>;
    let mockErrorMessage$: Subject<string>;
    let mockSystemMessage$: Subject<string>;

    beforeEach(() => {
        mockNewMessage$$ = new Subject<ChatMessage>();
        mockErrorMessage$ = new Subject<string>();
        mockSystemMessage$ = new Subject<string>();

        onlineChatSpy = jasmine.createSpyObj(
            'OnlineChatHandler',
            ['sendMessage'],
            ['isConnected', 'opponentMessage$', 'errorMessage$', 'systemMessage$', 'newMessages$'],
        );

        (Object.getOwnPropertyDescriptor(onlineChatSpy, 'newMessages$')?.get as jasmine.Spy<() => Observable<ChatMessage>>).and.returnValue(
            mockNewMessage$$,
        );

        (Object.getOwnPropertyDescriptor(onlineChatSpy, 'errorMessage$')?.get as jasmine.Spy<() => Observable<string>>).and.returnValue(
            mockErrorMessage$,
        );
        (Object.getOwnPropertyDescriptor(onlineChatSpy, 'systemMessage$')?.get as jasmine.Spy<() => Observable<string>>).and.returnValue(
            mockSystemMessage$,
        );

        TestBed.configureTestingModule({
            providers: [{ provide: OnlineChatHandlerService, useValue: onlineChatSpy }],
            imports: [HttpClientTestingModule],
        });
        service = TestBed.inject(MessagesService);
    });

    it('should create message', () => {
        expect(service).toBeTruthy();
    });

    it('should receive system message', () => {
        const content = 'test';
        const date = new Date();
        const sysMessage: SystemMessage = {
            content,
            date,
            conversation: 'abc',
        };
        service.receiveSystemMessage(sysMessage);
        const expectedMesssage: Message = {
            from: 'System',
            content,
            type: MessageType.System,
            date,
        };
        const log = service.messages;
        const lastMessage = log[log.length - 1];
        expect(lastMessage).toEqual(expectedMesssage);
    });

    it('should receive error', () => {
        const errorContent = 'this is an error';
        service.receiveErrorMessage(errorContent);
        const log = service.messages;
        const lastMessage = log[log.length - 1];
        const message: Message = {
            from: 'SystemError',
            content: errorContent,
            type: MessageType.System,
        };
        expect(lastMessage).toEqual(message);
    });

    it('should receive error Message', () => {
        const errorContent = 'this is a parse error';
        service.receiveErrorMessage(errorContent);
        const log = service.messages;
        const lastMessage = log[log.length - 1];
        const message: Message = {
            from: 'SystemError',
            content: errorContent,
            type: MessageType.System,
        };
        expect(lastMessage).toEqual(message);
    });

    it('should receive error message from chat server', () => {
        const errorContent = 'Error error error';
        const spy = spyOn(service, 'receiveErrorMessage');
        mockErrorMessage$.next(errorContent);
        expect(spy).toHaveBeenCalledOnceWith(errorContent);
    });
});
