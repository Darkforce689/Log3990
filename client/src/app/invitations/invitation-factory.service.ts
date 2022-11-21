import { Injectable } from '@angular/core';
import { INEXISTANT_USERNAME } from '@app/invitations/constants';
import { Invitation, InvitationDTO } from '@app/invitations/interfaces/invitation.interface';
import { UserCacheService } from '@app/users/services/user-cache.service';
import { Observable, ReplaySubject } from 'rxjs';

@Injectable({
    providedIn: 'root',
})
export class InvitationFactoryService {
    constructor(private userCache: UserCacheService) {}

    createInvitation(invitationDTO: InvitationDTO): Observable<Invitation> {
        const invitation$ = new ReplaySubject<Invitation>();
        const { from: fromId } = invitationDTO;
        this.userCache.getUser(fromId).subscribe((user) => {
            const from = !user ? INEXISTANT_USERNAME : user.name;
            const invitation = { ...invitationDTO, from } as Invitation;
            invitation$.next(invitation);
            invitation$.complete();
        });
        return invitation$;
    }
}
