import { Injectable } from '@angular/core';
import { Invitation, InvitationDTO } from '@app/invitations/interfaces/invitation.interface';
import { AuthService } from '@app/services/auth.service';
import { Observable, Subject } from 'rxjs';
import { io, Socket } from 'socket.io-client';
import { environment } from 'src/environments/environment';

// Service to use to receive server notification
@Injectable({
    providedIn: 'root',
})
export class AppSocketHandlerService {
    socket: Socket | undefined;
    private invitationsSub = new Subject<InvitationDTO>();
    get invitations$(): Observable<Invitation> {
        return this.invitationsSub;
    }

    constructor(private authService: AuthService) {
        this.authService.isAuthenticated$.subscribe((isAuth) => {
            if (isAuth) {
                this.connect();
            } else {
                this.disconnect();
            }
        });
    }

    disconnect() {
        if (!this.socket) {
            return;
        }
        this.socket.disconnect();
        this.socket = undefined;
    }

    connect() {
        if (this.socket) {
            throw Error('Already an app socket connected');
        }
        this.socket = this.connectSocket();
        this.socket.on('connect_error', (e) => {
            // eslint-disable-next-line no-console
            console.log('connection error for app socket', e);
        });

        this.socket.on('invitations', (invitation: InvitationDTO) => {
            this.invitationsSub.next(invitation);
        });
    }

    private connectSocket() {
        return io(environment.serverSocketUrl, { path: '/app', withCredentials: true, transports: ['websocket'] });
    }
}
