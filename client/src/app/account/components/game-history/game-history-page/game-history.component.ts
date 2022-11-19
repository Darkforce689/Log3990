/* eslint-disable no-underscore-dangle */
/* eslint-disable no-invalid-this */
import { formatDate } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { AfterViewInit, Component, Inject, LOCALE_ID, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { ReplayComponent } from '@app/account/components/game-history/replay/replay.component';
import { GameHistory, GameHistoryInfo, ReplayGameStates } from '@app/account/interfaces/game-history.interface';
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
    selectedRow: GameHistoryInfo | undefined;
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
            cell: (game: GameHistoryInfo) => `${formatDate(game.date, 'MMM d, y, h:mm:ss a', this.locale)}`,
        },
        {
            columnDef: 'type',
            header: 'Status',
            cell: (game: GameHistoryInfo) => `Partie ${this.getGameStatus(game)}`,
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
        if (!game.winnerIds) {
            return false;
        }
        if (!this.user._id) {
            return false;
        }
        return game.winnerIds.includes(this.user._id);
    }

    isForfeitedPlayer(game: GameHistoryInfo) {
        if (!game.forfeitedIds) {
            return false;
        }
        if (!this.user._id) {
            return false;
        }
        return game.forfeitedIds.includes(this.user._id);
    }

    getGameStatus(gameInfo: GameHistoryInfo) {
        if (this.isWinner(gameInfo)) {
            return 'gagnée';
        }
        if (this.isForfeitedPlayer(gameInfo)) {
            return 'abandonnée';
        }
        return 'perdue';
    }

    getGameStates(gameToken: string) {
        return this.http.get(`${environment.serverUrl}/account/gameStates`, { params: { gameToken } }) as Observable<ReplayGameStates>;
    }

    isLoading(game: GameHistoryInfo) {
        return this.selectedRow === game;
    }

    replay(game: GameHistoryInfo) {
        if (this.isLoading(game)) {
            return;
        }
        this.setSelectedRow(game);
        const gameToken = game.gameToken;
        const userIndex = game.userIds.findIndex((userId) => userId === this.user._id);
        this.getGameStates(gameToken).subscribe(
            (replay: ReplayGameStates) => {
                this.selectedRow = undefined;
                const gameStates = replay.gameStates;
                this.matDialog.open(ReplayComponent, {
                    width: '80%',
                    height: '85%',
                    panelClass: 'custom-dialog-container',
                    data: { gameStates, userIndex },
                });
            },
            () => {
                this.selectedRow = undefined;
            },
        );
    }

    private setSelectedRow(game: GameHistoryInfo) {
        this.selectedRow = game;
    }
}
