import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { User } from '@app/pages/register-page/user.interface';
import { AuthService } from '@app/services/auth.service';
import { BehaviorSubject, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
    providedIn: 'root',
})
export class AccountService {
    private accountSubject = new BehaviorSubject<User | undefined>(undefined);
    get account$(): Observable<User | undefined> {
        return this.accountSubject;
    }

    get account() {
        return this.accountSubject.value;
    }

    constructor(private http: HttpClient, private authService: AuthService) {
        this.authService.isAuthenticated$.subscribe((isAuth) => {
            if (isAuth) {
                this.actualizeAccount();
                return;
            }

            this.updateAccount(undefined);
        });
    }

    actualizeAccount() {
        const request = this.http.get(`${environment.serverUrl}/account`);
        request.subscribe(
            (user) => {
                this.updateAccount(user as User);
            },
            () => {
                this.updateAccount(undefined);
            },
        );
    }

    private updateAccount(user: User | undefined) {
        this.accountSubject.next(user);
    }
}
