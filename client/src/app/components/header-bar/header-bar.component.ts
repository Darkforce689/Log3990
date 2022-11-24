import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { User } from '@app/pages/register-page/user.interface';
import { AccountService } from '@app/services/account.service';
import { ThemeService } from '@app/services/theme/theme.service';

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
        totalExp: 0,
    };

    constructor(private themeService: ThemeService, private accountService: AccountService, private router: Router) {
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
}
