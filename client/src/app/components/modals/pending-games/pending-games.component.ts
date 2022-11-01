import { LiveAnnouncer } from '@angular/cdk/a11y';
import { DatePipe } from '@angular/common';
import { AfterContentChecked, AfterViewInit, ChangeDetectorRef, Component, Inject, OnInit, ViewChild } from '@angular/core';
import { MatDialog, MatDialogConfig, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSort, Sort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { JoinOnlineGameComponent } from '@app/components/modals/join-online-game/join-online-game.component';
import { getRandomInt } from '@app/game-logic/utils';
import { GameMode } from '@app/socket-handler/interfaces/game-mode.interface';
import { OnlineGameSettings } from '@app/socket-handler/interfaces/game-settings-multi.interface';
import { NewOnlineGameSocketHandler } from '@app/socket-handler/new-online-game-socket-handler/new-online-game-socket-handler.service';
import { BehaviorSubject, timer } from 'rxjs';

export const DELAY = 100;
@Component({
    selector: 'app-pending-games',
    templateUrl: './pending-games.component.html',
    styleUrls: ['./pending-games.component.scss'],
})
export class PendingGamesComponent implements AfterContentChecked, OnInit, AfterViewInit {
    @ViewChild(MatSort) tableSort: MatSort;
    columnsToDisplay = ['playerNames', 'randomBonus', 'timePerTurn', 'hasPassword', 'privateGame', 'numberOfPlayers'];
    selectedRow: OnlineGameSettings | undefined;
    pendingGameDataSource = new MatTableDataSource<OnlineGameSettings>();
    observableGameDataSource = new MatTableDataSource<OnlineGameSettings>();
    columns: {
        columnDef: string;
        header: string;
        cell: (form: OnlineGameSettings) => string;
    }[];
    datePipe = new DatePipe('en_US');
    private isClicked: boolean = false;

    constructor(
        @Inject(MAT_DIALOG_DATA) public gameMode: GameMode,
        private dialogRef: MatDialogRef<PendingGamesComponent>,
        private dialog: MatDialog,
        private cdref: ChangeDetectorRef,
        private onlineSocketHandler: NewOnlineGameSocketHandler,
        private liveAnnouncer: LiveAnnouncer,
    ) {
        this.columns = [
            {
                columnDef: 'id',
                header: 'Id',
                cell: (form: OnlineGameSettings) => `${form.id}`,
            },
            {
                columnDef: 'playerNames',
                header: 'Joueurs',
                cell: (form: OnlineGameSettings) => `${form.playerNames}`,
            },
            {
                columnDef: 'randomBonus',
                header: 'Bonus Aléatoire',
                cell: (form: OnlineGameSettings) => (form.randomBonus ? 'activé' : 'désactivé'),
            },
            {
                columnDef: 'hasPassword',
                header: 'Mot de passe',
                cell: (form: OnlineGameSettings) => `${form.password !== undefined ? 'Oui' : 'Non'}`,
            },
            {
                columnDef: 'privateGame',
                header: 'Type de partie',
                cell: (form: OnlineGameSettings) => `${form.privateGame ? 'Privée' : 'Publique'}`,
            },
            {
                columnDef: 'timePerTurn',
                header: 'Temps par tour',
                cell: (form: OnlineGameSettings) => `${this.datePipe.transform(form.timePerTurn, 'm:ss')} `,
            },
            {
                columnDef: 'numberOfPlayers',
                header: 'Joueurs : IA / Max',
                cell: (form: OnlineGameSettings) =>
                    `${form.playerNames.length} : ${form.numberOfPlayers - form.playerNames.length} / ${form.numberOfPlayers}`,
            },
        ];
    }

    ngOnInit() {
        this.pendingGames$.subscribe((gameSettings) => {
            const filteredGameSettings = gameSettings.filter((gameSetting) => gameSetting.gameMode === this.gameMode);
            this.pendingGameDataSource.data = filteredGameSettings;
        });
        this.observableGames$.subscribe((gameSettings) => {
            const filteredGameSettings = gameSettings.filter((gameSetting) => gameSetting.gameMode === this.gameMode);
            this.observableGameDataSource.data = filteredGameSettings;
        });
        this.onlineSocketHandler.listenForPendingGames();
    }

    ngAfterViewInit() {
        this.pendingGameDataSource.sort = this.tableSort;
    }

    ngAfterContentChecked() {
        this.cdref.detectChanges();
    }

    cancel(): void {
        this.dialogRef.close();
    }

    setSelectedRow(row: OnlineGameSettings): void {
        if (this.selectedRow === row) {
            this.selectedRow = undefined;
            return;
        }
        this.selectedRow = row;
    }

    joinGame(): void {
        const joinPendingGameRef = new MatDialogConfig();
        joinPendingGameRef.autoFocus = true;
        joinPendingGameRef.disableClose = true;
        joinPendingGameRef.data = this.selectedRow;

        const joinPendingGame = this.dialog.open(JoinOnlineGameComponent, joinPendingGameRef);
        joinPendingGame.beforeClosed().subscribe(() => {
            this.isClicked = false;
            this.dialogRef.close();
        });
    }

    isSelectedRow(row: OnlineGameSettings): boolean {
        return row === this.selectedRow;
    }

    pickRandomGame(): void {
        if (this.isClicked) {
            return;
        }
        this.isClicked = true;
        const gameNumber = getRandomInt(this.pendingGameDataSource.data.length);
        this.selectedRow = this.pendingGameDataSource.data[gameNumber];
        timer(DELAY).subscribe(() => {
            this.joinGame();
        });
    }

    announceSortChange(sortState: Sort) {
        this.liveAnnouncer.announce(`Sorted ${sortState.direction}ending`);
    }

    get isEmpty(): boolean {
        return this.pendingGameDataSource.data.length === 0;
    }

    get hasOneGame(): boolean {
        return this.pendingGameDataSource.data.length === 1;
    }

    get pendingGames$(): BehaviorSubject<OnlineGameSettings[]> {
        return this.onlineSocketHandler.pendingGames$;
    }

    get observableGames$(): BehaviorSubject<OnlineGameSettings[]> {
        return this.onlineSocketHandler.observableGames$;
    }
}
