import { TestBed } from '@angular/core/testing';
import { ChatMessage, Message, MessageType } from '@app/game-logic/messages/message.interface';
import { MessagesService } from '@app/game-logic/messages/messages.service';
import { OnlineChatHandlerService } from '@app/game-logic/messages/online-chat-handler/online-chat-handler.service';
import { Observable, Subject } from 'rxjs';

describe('Service: Messages', () => {
    let service: MessagesService;
    let onlineChatSpy: jasmine.SpyObj<OnlineChatHandlerService>;
    let mockOpponentMessage$: Subject<ChatMessage>;
    let mockErrorMessage$: Subject<string>;
    let mockSystemMessage$: Subject<string>;

    beforeEach(() => {
        mockOpponentMessage$ = new Subject<ChatMessage>();
        mockErrorMessage$ = new Subject<string>();
        mockSystemMessage$ = new Subject<string>();

        onlineChatSpy = jasmine.createSpyObj(
            'OnlineChatHandler',
            ['sendMessage'],
            ['isConnected', 'opponentMessage$', 'errorMessage$', 'systemMessage$'],
        );

        (Object.getOwnPropertyDescriptor(onlineChatSpy, 'opponentMessage$')?.get as jasmine.Spy<() => Observable<ChatMessage>>).and.returnValue(
            mockOpponentMessage$,
        );
        (Object.getOwnPropertyDescriptor(onlineChatSpy, 'errorMessage$')?.get as jasmine.Spy<() => Observable<string>>).and.returnValue(
            mockErrorMessage$,
        );
        (Object.getOwnPropertyDescriptor(onlineChatSpy, 'systemMessage$')?.get as jasmine.Spy<() => Observable<string>>).and.returnValue(
            mockSystemMessage$,
        );

        TestBed.configureTestingModule({
            providers: [{ provide: OnlineChatHandlerService, useValue: onlineChatSpy }],
        });
        service = TestBed.inject(MessagesService);
    });

    it('should create message', () => {
        expect(service).toBeTruthy();
    });

    it('should receive message', () => {
        const from = 'paul';
        const content = 'allo';
        service.receiveMessagePlayer(from, content);
        const logs = service.messagesLog;
        const lastMessage = logs[logs.length - 1];
        const expectedMesssage: Message = {
            from,
            content,
            type: MessageType.Player1,
        };
        expect(lastMessage).toEqual(expectedMesssage);
    });

    it('should receive system message', () => {
        const content = 'test';
        service.receiveSystemMessage(content);
        const expectedMesssage: Message = {
            from: 'System',
            content,
            type: MessageType.System,
        };
        const log = service.messagesLog;
        const lastMessage = log[log.length - 1];
        expect(lastMessage).toEqual(expectedMesssage);
    });

    it('should receive error', () => {
        const errorContent = 'this is an error';
        service.receiveErrorMessage(errorContent);
        const log = service.messagesLog;
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
        const log = service.messagesLog;
        const lastMessage = log[log.length - 1];
        const message: Message = {
            from: 'SystemError',
            content: errorContent,
            type: MessageType.System,
        };
        expect(lastMessage).toEqual(message);
    });

    it('should clear log', () => {
        service.receiveMessagePlayer('tim', 'to be');
        service.receiveMessagePlayer('cook', 'or');
        service.receiveMessagePlayer('apple', 'not to be');
        const prevNLogs = service.messagesLog.length;
        expect(prevNLogs).toBe(3);
        service.clearLog();
        const nLogs = service.messagesLog.length;
        expect(nLogs).toBe(0);
    });

    it('should receive opponent message from chat server', () => {
        const from = 'Paul';
        const content = 'Hi';
        const chatMessage = { from, content };
        const spy = spyOn(service, 'receiveMessageOpponent');
        mockOpponentMessage$.next(chatMessage);
        expect(spy).toHaveBeenCalledOnceWith(from, content);
    });

    it('should receive error message from chat server', () => {
        const errorContent = 'Error error error';
        const spy = spyOn(service, 'receiveErrorMessage');
        mockErrorMessage$.next(errorContent);
        expect(spy).toHaveBeenCalledOnceWith(errorContent);
    });

    it('should receive online system message properly', () => {
        const spy = spyOn(service, 'receiveSystemMessage');
        const content = 'this is a string';
        mockSystemMessage$.next(content);
        expect(spy).toHaveBeenCalledOnceWith(content);
    });
});
