import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { User } from '@app/pages/register-page/user.interface';
import { BehaviorSubject, Observable, of, Subject } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
    providedIn: 'root',
})
export class UserCacheService {
    private users: Map<string, User> = new Map();
    private usersName: Map<string, User> = new Map();
    constructor(private http: HttpClient) {}

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
        const subject = new BehaviorSubject<User | undefined>(undefined);
        this.http.get(`${environment.serverUrl}/users`, { params: { name } }).subscribe(
            (body) => {
                const { user: fetchedUser } = body as { user: User };
                // eslint-disable-next-line no-underscore-dangle
                this.users.set(fetchedUser._id, fetchedUser);
                this.usersName.set(fetchedUser.name, fetchedUser);
                subject.next(fetchedUser);
            },
            () => {
                subject.next(undefined);
            },
        );
        return subject;
    }

    clear() {
        this.users.clear();
    }
}
