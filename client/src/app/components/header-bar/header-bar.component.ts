import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { ErrorDialogComponent } from '@app/components/modals/error-dialog/error-dialog.component';
import { User, UserStatus } from '@app/pages/register-page/user.interface';
import { AccountService } from '@app/services/account.service';
import { AuthService } from '@app/services/auth.service';
import { PopChatService } from '@app/services/pop-chat.service';
import { ThemeService } from '@app/services/theme/theme.service';
import { first } from 'rxjs/operators';

@Component({
    selector: 'app-header-bar',
    templateUrl: './header-bar.component.html',
    styleUrls: ['./header-bar.component.scss'],
})
export class HeaderBarComponent {
    user: User = {
        _id: '',
        name: '',
        email: '',
        avatar: 'default',
        averagePoints: 0,
        nGamePlayed: 0,
        nGameWon: 0,
        averageTimePerGame: 0,
        status: UserStatus.Online,
        totalExp: 0,
    };

    constructor(
        private themeService: ThemeService,
        private accountService: AccountService,
        private router: Router,
        private authService: AuthService,
        private matDialog: MatDialog,
        private popChatService: PopChatService,
    ) {
        this.accountService.account$.subscribe((user: User | undefined) => {
            if (user) {
                this.user = user;
            }
        });
    }

    get isDarkMode() {
        return this.themeService.isDark;
    }

    navigateToAccount() {
        this.router.navigate(['/account']);
    }

    logout() {
        this.authService.logout().subscribe(
            () => {
                const dialogRef = this.matDialog.open(ErrorDialogComponent, {
                    disableClose: true,
                    autoFocus: true,
                    data: 'Vous avez été déconnecté avec succès',
                });
                dialogRef
                    .afterClosed()
                    .pipe(first())
                    .subscribe(() => {
                        this.popChatService.closeExternalWindow();
                        this.router.navigate(['/login']);
                    });
            },
            () => {
                this.matDialog.open(ErrorDialogComponent, {
                    disableClose: true,
                    autoFocus: true,
                    data: "Une erreur est survenue lors de la déconnexion : Le serveur n'est pas disponible",
                });
            },
        );
    }
}
