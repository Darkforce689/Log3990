import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { UserCredentials } from '@app/pages/login-page/user-credentials.interface';
import { UserCreation } from '@app/pages/register-page/user-creation.interface';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { environment } from 'src/environments/environment';

@Injectable({
    providedIn: 'root',
})
export class AuthService {
    private isAuthSubject = new BehaviorSubject(false);
    get isAuthenticated$(): Observable<boolean> {
        return this.isAuthSubject;
    }

    constructor(private http: HttpClient) {}

    register(userCreation: UserCreation) {
        // eslint-disable-next-line no-console
        console.log(userCreation);
        return this.http.post(`${environment.serverAuthUrl}/register`, userCreation);
    }

    login(userCreds: UserCredentials) {
        // eslint-disable-next-line no-console
        console.log('creds:', userCreds);
        const header = {
            withCredentials: true,
        };
        const res = this.http.post(`${environment.serverAuthUrl}/login`, userCreds, header);
        res.subscribe(
            () => {
                this.setIsAuth(true);
            },
            () => {
                this.setIsAuth(false);
            },
        );
        return res;
    }

    logout() {
        const res = this.http.get(`${environment.serverAuthUrl}/logout`, { responseType: 'text' });
        res.subscribe(
            () => {
                this.setIsAuth(false);
            },
            () => {
                this.setIsAuth(true);
            },
        );
        return res;
    }

    isAuthenticated(): Observable<boolean> {
        const header = {
            withCredentials: true,
        };

        const res = this.http.post(`${environment.serverAuthUrl}/login`, {}, header);
        res.subscribe(
            () => {
                this.setIsAuth(true);
            },
            () => {
                this.setIsAuth(false);
            },
        );
        // TODO maybe change
        return this.http.post(`${environment.serverAuthUrl}/login`, {}, header).pipe(
            map(() => true),
            catchError(() => of(false)),
        );
    }

    private setIsAuth(value: boolean) {
        if (this.isAuthSubject.value === value) {
            return;
        }
        this.isAuthSubject.next(value);
    }
}
