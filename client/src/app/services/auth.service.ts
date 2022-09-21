import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { UserCredentials } from '@app/pages/login-page/user-credentials.interface';
import { UserCreation } from '@app/pages/register-page/user-creation.interface';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { environment } from 'src/environments/environment';

@Injectable({
    providedIn: 'root',
})
export class AuthService {
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
        return this.http.post(`${environment.serverAuthUrl}/login`, userCreds, header);
    }

    logout() {
        return this.http.get(`${environment.serverAuthUrl}/logout`, { responseType: 'text' });
    }

    isAuthenticated(): Observable<boolean> {
        const header = {
            withCredentials: true,
        };
        // TODO maybe change
        return this.http.post(`${environment.serverAuthUrl}/login`, {}, header).pipe(
            map(() => true),
            catchError(() => of(false)),
        );
    }
}
