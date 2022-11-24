import { Component, ElementRef, Inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { GameInviteArgs } from '@app/invitations/interfaces/invitation.interface';
import { User, UserStatus } from '@app/pages/register-page/user.interface';
import { AccountService } from '@app/services/account.service';
import { USERS_PERPAGE_SEARCH } from '@app/users/constans';
import { SearchUsersArgs, UserService } from '@app/users/services/user.service';
import { BehaviorSubject, Subscription } from 'rxjs';

export interface UserSearchData {
    args: GameInviteArgs;
    forbidenUsers$?: BehaviorSubject<string[]>; // Change to userId when waiting for other player uses userId
}

@Component({
    selector: 'app-user-search',
    templateUrl: './user-search.component.html',
    styleUrls: ['./user-search.component.scss'],
})
export class UserSearchComponent implements OnInit, OnDestroy {
    @ViewChild('list', { read: ElementRef }) usersList: ElementRef;

    userName: string = '';
    isOnline: boolean = false;

    users$ = new BehaviorSubject<User[]>([]);

    private search$$: Subscription;
    private invitedUsers = new Set<string>();
    private forbidenUsersName = new Set<string>(); // Change to userId when waiting for other player uses userId

    private currentPage = 0;

    private forbidenUsers$$: Subscription | undefined;

    constructor(
        @Inject(MAT_DIALOG_DATA) public data: UserSearchData,
        private dialogRef: MatDialogRef<UserSearchComponent>,
        private userService: UserService,
        private snackBar: MatSnackBar,
        private accountService: AccountService,
    ) {}

    ngOnInit(): void {
        this.forbidenUsers$$ = this.data.forbidenUsers$?.subscribe((forbidenUsers) => {
            this.forbidenUsersName.clear();
            forbidenUsers.forEach((name) => {
                this.forbidenUsersName.add(name);
            });
        });
        this.fetchFirstUsers('', false);
    }

    ngOnDestroy(): void {
        this.forbidenUsers$$?.unsubscribe();
    }

    searchUsers() {
        this.fetchFirstUsers(this.userName, this.isOnline);
    }

    invite(user: User) {
        const { _id: userId } = user;
        this.invitedUsers.add(userId);

        this.userService.invite(user, this.data.args).subscribe(
            () => {
                this.snackBar.open('Votre invitation a bien été envoyée', 'OK', { duration: 2000 });
            },
            () => {
                this.invitedUsers.delete(userId);
                this.snackBar.open('Votre invitation a eu un problème veuillez réessayer', 'OK', { duration: 3500 });
            },
        );
    }

    canInvite(user: User) {
        const { _id: userId, name: userName } = user;
        const accountId = this.getAccountId();
        return !this.invitedUsers.has(userId) && userId !== accountId && !this.forbidenUsersName.has(userName);
    }

    close() {
        this.dialogRef.close();
    }

    onScroll() {
        const isFetching = !this.search$$;
        if (isFetching) {
            return;
        }
        const { scrollTop, scrollHeight, offsetHeight } = this.usersList.nativeElement;
        const isBottom = scrollTop + offsetHeight >= scrollHeight;
        if (!isBottom) {
            return;
        }
        this.currentPage++;
        this.fetchUsers(this.userName, this.isOnline, this.currentPage);
    }

    scrollTop() {
        if (!this.usersList) {
            return;
        }
        const nativeElem = this.usersList.nativeElement;
        nativeElem.scrollTop = 0;
    }

    private getAccountId() {
        // eslint-disable-next-line no-underscore-dangle
        return !this.accountService.account ? '' : this.accountService.account._id;
    }

    private fetchFirstUsers(name: string, isOnline: boolean) {
        this.search$$?.unsubscribe();
        this.currentPage = 0;
        this.scrollTop();
        const searchQuery = isOnline ? { name, status: UserStatus.Online } : { name };
        this.search$$ = this.userService.searchUsers(searchQuery).subscribe((users) => {
            this.users$.next(users);
        });
    }

    // Used to fetch page 1 2 3 etc
    private fetchUsers(name: string, isOnline: boolean, page: number) {
        const isFetching = !this.search$$;
        if (isFetching) {
            return;
        }
        const pagination = {
            page,
            perPage: USERS_PERPAGE_SEARCH,
        };
        const searchQuery: SearchUsersArgs = { name, pagination };
        if (isOnline) {
            searchQuery.status = UserStatus.Online;
        }
        this.search$$ = this.userService.searchUsers(searchQuery).subscribe((newUsers) => {
            const users = this.users$.value;
            users.push(...newUsers);
            this.users$.next(users);
        });
    }

    get onlineStatus() {
        return UserStatus.Online;
    }

    get offlineStatus() {
        return UserStatus.Offline;
    }
}
