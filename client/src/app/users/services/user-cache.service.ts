import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ConversationService } from '@app/chat/services/conversation/conversation.service';
import { BOT_NAMES } from '@app/game-logic/constants';
import { User } from '@app/pages/register-page/user.interface';
import { TIME_INVALIDATE_USER_CACHE } from '@app/users/constans';
import { BehaviorSubject, Observable, of, Subject, timer } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
    providedIn: 'root',
})
export class UserCacheService {
    private users: Map<string, User> = new Map();
    private usersName: Map<string, User> = new Map();
    constructor(private http: HttpClient, private conversationService: ConversationService) {
        timer(TIME_INVALIDATE_USER_CACHE, TIME_INVALIDATE_USER_CACHE).subscribe(() => {
            this.usersName.clear();
        });

        this.conversationService.currentConversation$.subscribe(() => {
            this.invalidateUsers();
        });
    }

    getUser(userId: string): Observable<User | undefined> {
        const user = this.users.get(userId);
        if (user) {
            return of(user);
        }
        const subject = new Subject<User | undefined>();
        this.http.get(`${environment.serverUrl}/users/${userId}`).subscribe(
            (body) => {
                const { user: fetchedUser } = body as { user: User };
                this.users.set(userId, fetchedUser);
                this.usersName.set(fetchedUser.name, fetchedUser);
                subject.next(fetchedUser);
            },
            () => {
                subject.next(undefined);
            },
        );
        return subject;
    }

    getUserByName(name: string): Observable<User | undefined> {
        const user = this.usersName.get(name);
        if (user) {
            return of(user);
        }
        if (BOT_NAMES.has(name)) {
            return of(undefined);
        }
        const subject = new BehaviorSubject<User | undefined>(undefined);
        this.http.get(`${environment.serverUrl}/users`, { params: { name } }).subscribe(
            (body) => {
                const { user: fetchedUser } = body as { user: User };
                this.usersName.set(fetchedUser.name, fetchedUser);
                subject.next(fetchedUser);
            },
            () => {
                subject.next(undefined);
            },
        );
        return subject;
    }

    invalidateUsers() {
        this.users.clear();
        this.usersName.clear();
    }
}
