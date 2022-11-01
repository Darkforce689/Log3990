import { Component, OnInit } from '@angular/core';
import { MatSelectionListChange } from '@angular/material/list';
import { User } from '@app/pages/register-page/user.interface';
import { AccountService } from '@app/services/account.service';

const enum AccountPage {
    Profil = 'profil',
    StatsGame = 'stats',
}
@Component({
    selector: 'app-account-page',
    templateUrl: './account-page.component.html',
    styleUrls: ['./account-page.component.scss'],
})
export class AccountPageComponent implements OnInit {
    user: User = {
        _id: '',
        name: '',
        email: '',
        avatar: 'default',
        averagePoints: 0,
        nGamePlayed: 0,
        nGameWinned: 0,
        averageTimePerGame: 0,
    };
    showPage: AccountPage = AccountPage.Profil;

    constructor(private accountService: AccountService) {}

    ngOnInit(): void {
        this.accountService.account$.subscribe((user: User | undefined) => {
            if (user) {
                this.user = user;
            }
        });
    }

    updatePage(src: MatSelectionListChange) {
        const selection = src.options[0].value;
        this.showPage = selection;
    }

    get showProfil() {
        return this.showPage === AccountPage.Profil;
    }

    get showStats() {
        return this.showPage === AccountPage.StatsGame;
    }
}
