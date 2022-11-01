import { formatDate } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { AfterViewInit, Component, Inject, Input, LOCALE_ID, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { MILLI_TO_SECONDS, MIN_IN_HOUR, SECONDS_TO_HOUR, SEC_IN_MIN, TIME_BASE } from '@app/account/constants';
import { Log, LogType, UserLogs } from '@app/account/interfaces/user-logs.interface';
import { User } from '@app/pages/register-page/user.interface';
import { AccountService } from '@app/services/account.service';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
@Component({
    selector: 'app-game-stats-page',
    templateUrl: './game-stats-page.component.html',
    styleUrls: ['./game-stats-page.component.scss'],
})
export class GameStatsPageComponent implements OnInit, AfterViewInit {
    @ViewChild(MatPaginator) paginator: MatPaginator;
    @Input() roundRules = '1.0-2';

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
    columnsToDisplay: string[] = ['date', 'type'];
    logsSource = new MatTableDataSource<Log>();
    columns = [
        {
            columnDef: 'date',
            header: 'Date',
            // eslint-disable-next-line no-invalid-this
            cell: (logs: Log) => `${formatDate(logs.date, 'MMM d, y, h:mm:ss a', this.locale)}`,
        },
        {
            columnDef: 'type',
            header: 'Type de connexions',
            cell: (logs: Log) => (logs.type === LogType.CONNECTION ? 'Connexion' : 'Déconnexion'),
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
        this.getLogHistory();
    }

    getLogHistory() {
        const { pageIndex: page, pageSize: perPage } = this.paginator;
        const res = this.http.get(`${environment.serverUrl}/account/logHistory`, { params: { perPage, page } }) as Observable<UserLogs>;
        res.subscribe((user: UserLogs) => {
            this.logsSource.data = user.logs;
            this.paginator.length = user.logs.length < perPage ? page * perPage + user.logs.length : (page + 2) * perPage;
        });
    }

    formatTime(time: number): string {
        const totalSeconds = parseInt((time / MILLI_TO_SECONDS).toString(), TIME_BASE);
        const hours = Math.floor(totalSeconds / SECONDS_TO_HOUR);
        const minutes = Math.floor((totalSeconds - hours * SECONDS_TO_HOUR) / MIN_IN_HOUR);
        const seconds = totalSeconds - hours * SECONDS_TO_HOUR - minutes * SEC_IN_MIN;
        const hoursDisplay = hours < TIME_BASE ? '0' + hours : hours.toString();
        const minutesDisplay = minutes < TIME_BASE ? '0' + minutes : minutes.toString();
        const secondsDisplay = seconds < TIME_BASE ? '0' + seconds : seconds.toString();
        return hoursDisplay + 'h ' + minutesDisplay + 'm ' + secondsDisplay + 's';
    }

    get timeFormatted() {
        return this.user.averageTimePerGame ? this.formatTime(this.user.averageTimePerGame) : this.formatTime(0);
    }

    get averagePoints() {
        return this.user.averagePoints ? this.user.averagePoints : 0;
    }

    get nGamePlayed() {
        return this.user.nGamePlayed ? this.user.nGamePlayed : 0;
    }

    get nGameWinned() {
        return this.user.nGameWinned ? this.user.nGameWinned : 0;
    }
}
