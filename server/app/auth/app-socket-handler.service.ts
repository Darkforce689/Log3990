import { AuthService } from '@app/auth/services/auth.service';
import { SessionMiddlewareService } from '@app/auth/services/session-middleware.service';
import { Session } from '@app/auth/services/session.interface';
import * as http from 'http';
import * as io from 'socket.io';

export class AppSocketHandler {
    readonly sio: io.Server;
    constructor(server: http.Server, private sessionMiddleware: SessionMiddlewareService, private authService: AuthService) {
        this.sio = new io.Server(server, {
            path: '/app',
            cors: { origin: '*', methods: ['GET', 'POST'] },
        });
    }

    handleSockets(enableRedisSession: boolean = true) {
        const sessionMiddleware = this.sessionMiddleware.getSocketSessionMiddleware(enableRedisSession);
        this.sio.use(sessionMiddleware);
        this.sio.use(this.authService.socketAuthGuard);

        this.sio.on('connection', (socket) => {
            const { session, sessionID } = socket.request as unknown as { session: Session; sessionID: string };
            const { userId } = session;
            this.authService.assignSessionToUser(userId, sessionID);

            socket.on('disconnect', () => {
                this.authService.unassignSessionToUser(userId);
            });
        });
    }
}
