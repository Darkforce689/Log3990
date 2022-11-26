import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { MatSelectionListChange } from '@angular/material/list';
import { User, UserStatus } from '@app/pages/register-page/user.interface';
import { AccountService } from '@app/services/account.service';
import { PopChatService } from '@app/services/pop-chat.service';

const enum AccountPage {
    Profil = 'profil',
    StatsGame = 'stats',
    GameHistory = 'games',
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
        nGameWon: 0,
        averageTimePerGame: 0,
        status: UserStatus.Online,
        totalExp: 0,
    };
    showPage: AccountPage = AccountPage.Profil;

    constructor(private accountService: AccountService, public popOutService: PopChatService, private cdRef: ChangeDetectorRef) {}

    ngOnInit(): void {
        this.popOutService.windowed$.subscribe(() => {
            this.cdRef.detectChanges();
        });
        this.accountService.account$.subscribe((user: User | undefined) => {
            if (user) {
                this.user = user;
            }
        });
        this.popOutService.isOpen = false;
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

    get showGames() {
        return this.showPage === AccountPage.GameHistory;
    }
}
