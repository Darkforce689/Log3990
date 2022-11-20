import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Pagination } from '@app/chat/interfaces/pagination.interface';
import { BaseInvitation, Invitation, InvitationArgs, InvitationType } from '@app/invitations/interfaces/invitation.interface';
import { User } from '@app/pages/register-page/user.interface';
import { AccountService } from '@app/services/account.service';
import { USERS_PERPAGE_SEARCH } from '@app/users/constans';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from 'src/environments/environment';

interface GetUsersRes {
    pagination: Pagination;
    users: User[];
}

interface InviteRes {
    invitation: Invitation;
}

export interface SearchUsersArgs {
    name: string;
    pagination?: Pagination;
}

@Injectable({
    providedIn: 'root',
})
export class UserService {
    constructor(private http: HttpClient, private accountService: AccountService) {}

    searchUsers(args: SearchUsersArgs): Observable<User[]> {
        const { name, pagination } = args;
        const { page, perPage } = !pagination ? { page: 0, perPage: USERS_PERPAGE_SEARCH } : pagination;
        return this.http.get<GetUsersRes>(`${environment.serverUrl}/users?search=${name}&page=${page}&perPage=${perPage}`).pipe(
            map(
                (res: GetUsersRes) => res.users,
                () => [] as User[],
            ),
        );
    }

    invite(user: User, args: InvitationArgs): Observable<Invitation | undefined> {
        const account = this.accountService.account;
        if (!account) {
            throw Error("You can't send an invite: your account is not loaded yet");
        }

        const { _id: from } = account;
        const { _id: to } = user;

        const invitation: BaseInvitation = {
            from,
            to,
            type: InvitationType.Game,
            args,
        };
        return this.http.post<InviteRes>(`${environment.serverUrl}/users/${to}/invite`, invitation).pipe(map((res: InviteRes) => res.invitation));
    }
}
