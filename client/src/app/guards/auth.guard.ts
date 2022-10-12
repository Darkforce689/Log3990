import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '@app/services/auth.service';
import { Observable } from 'rxjs';
import { first } from 'rxjs/operators';

@Injectable({
    providedIn: 'root',
})
export class AuthGuard implements CanActivate {
    constructor(private authService: AuthService, private router: Router) {}
    canActivate(): Observable<boolean> {
        const isAuthenticated$ = this.authService.isAuthenticated();
        isAuthenticated$.pipe(first()).subscribe((isAuth) => {
            if (!isAuth) {
                this.router.navigate(['/login']);
            }
        });
        return isAuthenticated$;
    }
}
