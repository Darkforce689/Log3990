import { LiveAnnouncer } from '@angular/cdk/a11y';
import { DatePipe } from '@angular/common';
import { AfterContentChecked, AfterViewInit, ChangeDetectorRef, Component, Inject, OnInit, ViewChild } from '@angular/core';
import { MatDialog, MatDialogConfig, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSort, Sort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { JoinOnlineGameComponent } from '@app/components/modals/join-online-game/join-online-game.component';
import { ACTIVE_STATUS, WAIT_STATUS } from '@app/game-logic/constants';
import { GameMode } from '@app/socket-handler/interfaces/game-mode.interface';
import { OnlineGameSettings } from '@app/socket-handler/interfaces/game-settings-multi.interface';
import { NewOnlineGameSocketHandler } from '@app/socket-handler/new-online-game-socket-handler/new-online-game-socket-handler.service';
import { Observable } from 'rxjs';

export enum LobbyGameType {
    PendingGame,
    ObservableGame,
}

export const DELAY = 100;
@Component({
    selector: 'app-lobby-games',
    templateUrl: './lobby-games.component.html',
    styleUrls: ['./lobby-games.component.scss'],
})
export class LobbyGamesComponent implements AfterContentChecked, OnInit, AfterViewInit {
    @ViewChild(MatSort) tableSort: MatSort;
    columnsToDisplay = ['playerNames', 'randomBonus', 'timePerTurn', 'hasPassword', 'privateGame', 'numberOfPlayers'];
    selectedRow: OnlineGameSettings | undefined;
    lobbyGamesDataSource = new MatTableDataSource<OnlineGameSettings>();
    columns: {
        columnDef: string;
        header: string;
        cell: (form: OnlineGameSettings) => string;
    }[];
    datePipe = new DatePipe('en_US');

    constructor(
        @Inject(MAT_DIALOG_DATA)
        public data: {
            lobbyGameType: LobbyGameType;
            gameMode: GameMode;
            lobbyGames$: Observable<OnlineGameSettings[]>;
        },
        private dialogRef: MatDialogRef<LobbyGamesComponent>,
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
                header: 'Joueur:IA/Max|Obsrv',
                cell: (form: OnlineGameSettings) => this.playerCount(form),
            },
        ];
    }

    ngOnInit() {
        this.data.lobbyGames$.subscribe((gameSettings) => {
            this.lobbyGamesDataSource.data = gameSettings.filter((gameSetting) => gameSetting.gameMode === this.data.gameMode);
        });
        this.onlineSocketHandler.listenForPendingGames();
    }

    ngAfterViewInit() {
        this.lobbyGamesDataSource.sort = this.tableSort;
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
            this.dialogRef.close();
        });
    }

    isSelectedRow(row: OnlineGameSettings): boolean {
        return row === this.selectedRow;
    }

    announceSortChange(sortState: Sort) {
        this.liveAnnouncer.announce(`Sorted ${sortState.direction}ending`);
    }

    playerCount(form: OnlineGameSettings): string {
        if (form.gameStatus === WAIT_STATUS) {
            return `${form.playerNames.length} : ${form.numberOfPlayers - form.playerNames.length} / ${form.numberOfPlayers} | 0`;
        }
        if (form.gameStatus === ACTIVE_STATUS) {
            return `${form.numberOfBots ? form.playerNames.length - form.numberOfBots : form.playerNames.length} : 
            ${form.numberOfBots} / ${form.numberOfPlayers} | 
            ${form.observerNames ? form.observerNames.length : 0}`;
        }
        return '';
    }

    get isEmpty(): boolean {
        return this.lobbyGamesDataSource.data.length === 0;
    }

    get hasOneGame(): boolean {
        return this.lobbyGamesDataSource.data.length === 1;
    }

    get isGameFull(): boolean {
        if (this.selectedRow === undefined) {
            return false;
        }
        if (this.selectedRow.gameStatus === ACTIVE_STATUS) {
            return false;
        }
        return this.selectedRow?.playerNames.length === this.selectedRow?.numberOfPlayers;
    }

    get noLobbyGameLabel(): string {
        return this.data.lobbyGameType === LobbyGameType.PendingGame ? 'Aucune partie en attente' : 'Aucune partie en cours';
    }

    get lobbyGameTitle(): string {
        return this.data.lobbyGameType === LobbyGameType.PendingGame ? 'Joindre une partie' : 'Observer une partie';
    }
}
