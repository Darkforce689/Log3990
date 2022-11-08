import { formatDate } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { AfterViewInit, Component, Inject, LOCALE_ID, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { GameHistory, GameHistoryInfo } from '@app/account/interfaces/game-history.interface';
import { User } from '@app/pages/register-page/user.interface';
import { AccountService } from '@app/services/account.service';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Component({
    selector: 'app-game-history',
    templateUrl: './game-history.component.html',
    styleUrls: ['./game-history.component.scss'],
})
export class GameHistoryComponent implements OnInit, AfterViewInit {
    @ViewChild(MatPaginator) paginator: MatPaginator;

    user: User = {
        _id: '',
        name: '',
        email: '',
        avatar: 'default',
        averagePoints: 0,
        nGamePlayed: 0,
        nGameWon: 0,
        averageTimePerGame: 0,
    };
    columnsToDisplay: string[] = ['date', 'type'];
    gameSource = new MatTableDataSource<GameHistoryInfo>();
    columns = [
        {
            columnDef: 'date',
            header: 'Date',
            // eslint-disable-next-line no-invalid-this
            cell: (game: GameHistoryInfo) => `${formatDate(game.date, 'MMM d, y, h:mm:ss a', this.locale)}`,
        },
        {
            columnDef: 'type',
            header: 'Gagnants',
            // eslint-disable-next-line no-invalid-this
            cell: (game: GameHistoryInfo) => (this.isWinner(game) ? 'Partie gagnée' : 'Partie perdue'),
        },
    ];

    constructor(private accountService: AccountService, private http: HttpClient, @Inject(LOCALE_ID) private locale: string) {}

    ngOnInit(): void {
        this.accountService.account$.subscribe((user: User | undefined) => {
            if (user) {
                this.user = user;
            }
        });
    }

    ngAfterViewInit(): void {
        this.getGamesHistory();
    }

    getGamesHistory() {
        const { pageIndex: page, pageSize: perPage } = this.paginator;
        const res = this.http.get(`${environment.serverUrl}/account/gamesHistory`, { params: { perPage, page } }) as Observable<GameHistory>;
        res.subscribe((gameHistory: GameHistory) => {
            this.gameSource.data = gameHistory.games;
            this.paginator.length = gameHistory.games.length < perPage ? page * perPage + gameHistory.games.length : (page + 2) * perPage;
        });
    }

    isWinner(game: GameHistoryInfo) {
        // eslint-disable-next-line no-underscore-dangle
        if (!this.user._id) {
            return;
        }
        // eslint-disable-next-line no-underscore-dangle
        return game.winnerUsers.find((winnerId) => winnerId === this.user._id) ? true : false;
    }
}
