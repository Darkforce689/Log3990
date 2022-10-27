/* eslint-disable dot-notation */
/* eslint-disable @typescript-eslint/no-explicit-any */
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { OnlineChatHandlerService } from '@app/chat/services/online-chat-handler/online-chat-handler.service';
import { SocketMock } from '@app/game-logic/socket-mock';
import { AccountService } from '@app/services/account.service';
import { take } from 'rxjs/operators';

describe('online chat handler', () => {
    const accountService = { account: { name: 'Tim' } };

    let service: OnlineChatHandlerService;
    beforeEach(() => {
        TestBed.configureTestingModule({ providers: [{ provide: AccountService, useValue: accountService }], imports: [HttpClientTestingModule] });
        service = TestBed.inject(OnlineChatHandlerService);
        (service.socket as any) = new SocketMock();
        service['bindRoomChannels']();
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    it('should call error when servit emit error', () => {
        const spy = spyOn<any>(service, 'receiveChatServerError');
        (service.socket as any).peerSideEmit('error', 'test');
        expect(spy).toHaveBeenCalledTimes(1);
    });

    it('should call receiveSystemMessage when servit emit systemMessages', () => {
        const spy = spyOn<any>(service, 'receiveSystemMessage');
        (service.socket as any).peerSideEmit('systemMessages', 'test');
        expect(spy).toHaveBeenCalledTimes(1);
    });

    it('joinChatRoomWithUser should call joinChatRoom', () => {
        const spy = spyOn<any>(service, 'joinChatRoom');
        service['joinChatRoomWithUser']('1');
        expect(spy).toHaveBeenCalledTimes(1);
    });

    it('receiveChatServerError should set next subject', () => {
        let test = '';
        service.errorMessage$.pipe(take(1)).subscribe((value) => {
            test = value;
        });
        service['receiveChatServerError']('Huston we got a problem');
        expect(test).toEqual('Huston we got a problem');
    });

    it('receiveSystemMessage shoudl set sysMessage', () => {
        let test = '';
        service.systemMessage$.pipe(take(1)).subscribe((value) => {
            test = value;
        });
        const message = 'Dont look directly on the sun';
        service['receiveSystemMessage'](message);
        expect(test).toEqual(message);
    });

    it('should not throw when joining two time a game', () => {
        (service.socket as unknown) = undefined;
        expect(() => {
            service['joinChatRoom']('1');
        }).not.toThrowError();
    });
});
