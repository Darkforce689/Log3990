/* eslint-disable no-underscore-dangle */
import { formatDate } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { AfterViewInit, Component, Inject, LOCALE_ID, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { ReplayComponent } from '@app/account/components/game-history/replay/replay.component';
import { GameHistory, GameHistoryInfo, GameStateHistory } from '@app/account/interfaces/game-history.interface';
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
    isLoading = false;
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
    columnsToDisplay: string[] = ['date', 'type', 'replay'];
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

    constructor(
        private accountService: AccountService,
        private http: HttpClient,
        @Inject(LOCALE_ID) private locale: string,
        private matDialog: MatDialog,
    ) {}

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
        if (!this.user._id) {
            return;
        }
        return game.winnerUsers.find((winnerId) => winnerId === this.user._id) ? true : false;
    }

    getGameStates(gameToken: string) {
        return this.http.get(`${environment.serverUrl}/account/gameStates`, { params: { gameToken } }) as Observable<GameStateHistory[]>;
    }

    replay(game: GameHistoryInfo) {
        if (this.isLoading) {
            return;
        }
        this.isLoading = true;
        const gameToken = game.gameToken;
        const userIndex = game.userIds.findIndex((userId) => userId === this.user._id);
        this.getGameStates(gameToken).subscribe((gameStates: GameStateHistory[]) => {
            this.isLoading = false;
            this.matDialog.open(ReplayComponent, {
                width: '80%',
                height: '85%',
                panelClass: 'custom-dialog-container',
                data: { gameStates, userIndex },
            });
        });
    }
}
