import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Pagination } from '@app/chat/interfaces/pagination.interface';
import { BaseInvitation, Invitation, InvitationArgs, InvitationType } from '@app/invitations/interfaces/invitation.interface';
import { User, UserStatus } from '@app/pages/register-page/user.interface';
import { AccountService } from '@app/services/account.service';
import { USERS_PERPAGE_SEARCH } from '@app/users/constans';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from 'src/environments/environment';

export interface GetUsersRes {
    pagination: Pagination;
    users: User[];
}

interface InviteRes {
    invitation: Invitation;
}

export interface SearchUsersArgs {
    name: string;
    status?: UserStatus;
    pagination?: Pagination;
}

export interface SearchUsersQueryParams {
    search: string;
    status?: string;
    perPage: number;
    page: number;
}

@Injectable({
    providedIn: 'root',
})
export class UserService {
    constructor(private http: HttpClient, private accountService: AccountService) {}

    searchUsers(args: SearchUsersArgs): Observable<User[]> {
        const { name: search, pagination, status } = args;
        const { page, perPage } = !pagination ? { page: 0, perPage: USERS_PERPAGE_SEARCH } : pagination;

        const searchParams: SearchUsersQueryParams = {
            search,
            page,
            perPage,
        };
        if (status) {
            searchParams.status = status;
        }
        return this.http.get<GetUsersRes>(`${environment.serverUrl}/users`, { params: searchParams as unknown as HttpParams }).pipe(
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

        const { _id: to } = user;

        const invitation: BaseInvitation = {
            type: InvitationType.Game,
            args,
        };
        return this.http.post<InviteRes>(`${environment.serverUrl}/users/${to}/invite`, invitation).pipe(map((res: InviteRes) => res.invitation));
    }
}
