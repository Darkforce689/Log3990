import { LogType } from '@app/account/user-log/connection-logs.interface';
import { UserLogService } from '@app/account/user-log/user-log.service';
import { AuthService } from '@app/auth/services/auth.service';
import { SessionMiddlewareService } from '@app/auth/services/session-middleware.service';
import { Session } from '@app/auth/services/session.interface';
import { ServerLogger } from '@app/logger/logger';
import { Invitation } from '@app/user/interfaces/invitations.interface';
import { UserInvitationService } from '@app/user/services/user-invitation.service';
import * as http from 'http';
import * as io from 'socket.io';

export class AppSocketHandler {
    readonly sio: io.Server;

    private connectedUsers = new Map<string, string>(); // userId -> socketId

    constructor(
        server: http.Server,
        private sessionMiddleware: SessionMiddlewareService,
        private authService: AuthService,
        private userLogService: UserLogService,
        private userInvitationService: UserInvitationService,
    ) {
        this.sio = new io.Server(server, {
            path: '/app',
            cors: { origin: '*', methods: ['GET', 'POST'] },
        });

        this.userInvitationService.invitations$.subscribe((invite) => {
            this.sendInvite(invite);
        });
    }

    handleSockets(enableRedisSession: boolean = true) {
        const sessionMiddleware = this.sessionMiddleware.getSocketSessionMiddleware(enableRedisSession);
        this.sio.use(sessionMiddleware);
        this.sio.use(this.authService.socketAuthGuard);

        this.sio.on('connection', (socket) => {
            const { session, sessionID } = socket.request as unknown as { session: Session; sessionID: string };
            const { userId } = session;

            this.userLogService.updateUserLog(Date.now(), LogType.CONNECTION, userId);
            this.authService.assignSessionToUser(userId, sessionID);
            this.addUser(userId, socket.id);

            socket.on('disconnect', () => {
                this.userLogService.updateUserLog(Date.now(), LogType.DECONNECTION, userId);
                this.authService.unassignSessionToUser(userId);
                this.removeUser(userId);
            });
        });
    }

    private addUser(userId: string, socketId: string) {
        this.connectedUsers.set(userId, socketId);
    }

    private removeUser(userId: string) {
        this.connectedUsers.delete(userId);
    }

    private sendInvite(invite: Invitation) {
        const socketId = this.connectedUsers.get(invite.to);
        if (!socketId) {
            ServerLogger.logDebug(`Discarded invite to user: ${invite.to}`);
            return; // discard invite if no user connected
        }
        this.sio.to(socketId).emit('invitations', invite);
    }
}
