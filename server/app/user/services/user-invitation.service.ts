import { Invitation } from '@app/user/interfaces/invitations.interface';
import { Observable, Subject } from 'rxjs';
import { Service } from 'typedi';

@Service()
export class UserInvitationService {
    private invitationsSub = new Subject<Invitation>();
    get invitations$(): Observable<Invitation> {
        return this.invitationsSub;
    }

    send(invitation: Invitation) {
        this.invitationsSub.next(invitation);
    }
}
