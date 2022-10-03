import { Injectable } from '@angular/core';
import { AuthService } from '@app/services/auth.service';
import { io, Socket } from 'socket.io-client';
import { environment } from 'src/environments/environment';

// Service to use to receive server notification
@Injectable({
    providedIn: 'root',
})
export class AppSocketHandlerService {
    socket: Socket | undefined;

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
        // TODO add invation socket channel
    }

    private connectSocket() {
        return io(environment.serverSocketUrl, { path: '/app', withCredentials: true, transports: ['websocket'] });
    }
}
