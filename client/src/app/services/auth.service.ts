import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { UserCredentials } from '@app/pages/login-page/user-credentials.interface';
import { UserCreation } from '@app/pages/register-page/user-creation.interface';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
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
        const res = this.http.post(`${environment.serverAuthUrl}/login`, userCreds);
        return res.pipe(
            tap({
                next: () => {
                    this.setIsAuth(true);
                },
                error: () => {
                    this.setIsAuth(false);
                },
            }),
        );
    }

    logout() {
        const res = this.http.get(`${environment.serverAuthUrl}/logout`, { responseType: 'text' });
        return res.pipe(
            tap({
                next: () => {
                    this.setIsAuth(false);
                },
                error: () => {
                    this.setIsAuth(true);
                },
            }),
        );
    }

    isAuthenticated(): Observable<boolean> {
        const header = {
            withCredentials: true,
        };

        const res = this.http.get(`${environment.serverAuthUrl}/validatesession`, header);
        return res.pipe(
            map(() => {
                this.setIsAuth(true);
                return true;
            }),
            catchError(() => {
                this.setIsAuth(false);
                return of(false);
            }),
        );
    }

    private setIsAuth(value: boolean) {
        if (this.isAuthSubject.value === value) {
            return;
        }
        this.isAuthSubject.next(value);
    }
}
