import { LiveAnnouncer } from '@angular/cdk/a11y';
import { DatePipe } from '@angular/common';
import { AfterContentChecked, AfterViewInit, ChangeDetectorRef, Component, Inject, OnInit, ViewChild } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSort, Sort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { BOT_NAMES } from '@app/game-logic/constants';
import { getBotAvatar } from '@app/game-logic/utils';
import { GameLauncherService } from '@app/socket-handler/game-launcher/game-laucher';
import { GameMode } from '@app/socket-handler/interfaces/game-mode.interface';
import { OnlineGameSettings } from '@app/socket-handler/interfaces/game-settings-multi.interface';
import { NewOnlineGameSocketHandler } from '@app/socket-handler/new-online-game-socket-handler/new-online-game-socket-handler.service';
import { UserCacheService } from '@app/users/services/user-cache.service';
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
    avatars = new Map<string, string>();

    constructor(
        @Inject(MAT_DIALOG_DATA)
        public data: {
            lobbyGameType: LobbyGameType;
            gameMode: GameMode;
            lobbyGames$: Observable<OnlineGameSettings[]>;
        },
        private dialogRef: MatDialogRef<LobbyGamesComponent>,
        private cdref: ChangeDetectorRef,
        private onlineSocketHandler: NewOnlineGameSocketHandler,
        private userCacheService: UserCacheService,
        private gameLauncher: GameLauncherService,
        private liveAnnouncer: LiveAnnouncer,
    ) {
        this.columns = [
            {
                columnDef: 'randomBonus',
                header: 'Bonus Aléatoire',
                cell: (form: OnlineGameSettings) => (form.randomBonus ? 'activé' : 'désactivé'),
            },
            {
                columnDef: 'timePerTurn',
                header: 'Temps par tour',
                cell: (form: OnlineGameSettings) => `${this.datePipe.transform(form.timePerTurn, 'm:ss')} `,
            },
            {
                columnDef: 'hasPassword',
                header: 'Type de partie',
                cell: (form: OnlineGameSettings) => `${form.password !== undefined ? 'Protégée' : 'Non-protégée'}`,
            },
            {
                columnDef: 'privateGame',
                header: 'Visibilité',
                cell: (form: OnlineGameSettings) => `${form.privateGame ? 'Privée' : 'Publique'}`,
            },
            {
                columnDef: 'numberOfPlayers',
                header: this.playerCountHeader,
                cell: (form: OnlineGameSettings) => this.playerCount(form),
            },
        ];
    }

    ngOnInit() {
        this.data.lobbyGames$.subscribe((gameSettings: OnlineGameSettings[]) => {
            this.lobbyGamesDataSource.data = gameSettings.filter((gameSetting) => gameSetting.gameMode === this.data.gameMode);
            gameSettings.forEach((gameSetting) => {
                const { botNames, playerNames, observerNames } = gameSetting;
                this.addPlayerIcons(playerNames);
                this.addBotIcons(botNames);
                if (!observerNames) {
                    return;
                }
                this.addPlayerIcons(observerNames);
            });
            this.lobbyGamesDataSource.sort = this.tableSort;
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
        if (!this.selectedRow) {
            return;
        }
        const { id, password } = this.selectedRow;
        const hasPassword = password !== undefined;
        this.gameLauncher.joinGame(id, hasPassword);
        this.dialogRef.close();
    }

    isSelectedRow(row: OnlineGameSettings): boolean {
        return row === this.selectedRow;
    }

    announceSortChange(sortState: Sort) {
        this.liveAnnouncer.announce(`Sorted ${sortState.direction}ending`);
    }

    addPlayerIcons(playerNames: string[]) {
        playerNames.forEach((name) =>
            this.userCacheService.getUserByName(name).subscribe((user) => {
                if (!user) {
                    return;
                }
                if (!user.avatar) {
                    this.avatars.set(name, 'default');
                    return;
                }
                this.avatars.set(name, user.avatar);
            }),
        );
    }

    addBotIcons(botNames: string[]) {
        botNames.forEach((name) => {
            const avatar = getBotAvatar(name);
            this.avatars.set(name, avatar);
        });
    }

    getAvatar(name: string): string {
        return this.avatars.get(name) ?? 'default';
    }

    getPlayers(gameSettings: OnlineGameSettings): string[] {
        if (!gameSettings) {
            return [];
        }
        const playerNames = gameSettings.playerNames.filter((name) => !BOT_NAMES.has(name));
        const nPlayers = playerNames.length;
        const totalPlayers = gameSettings.numberOfPlayers;
        const botNames = gameSettings.botNames.slice(nPlayers - 1, totalPlayers);
        return playerNames.concat(botNames);
    }

    getObservers(gameSettings: OnlineGameSettings): string[] {
        return gameSettings.observerNames ?? [];
    }

    playerCount(form: OnlineGameSettings): string {
        const playerNames = form.playerNames.filter((name) => !BOT_NAMES.has(name));
        const nPlayers = playerNames.length;
        const totalPlayers = form.numberOfPlayers;
        const nBots =
            this.data.lobbyGameType === LobbyGameType.ObservableGame
                ? form.playerNames.filter((name) => BOT_NAMES.has(name)).length
                : totalPlayers - nPlayers;
        const observerString =
            this.data.lobbyGameType === LobbyGameType.ObservableGame ? `| ${form.observerNames ? form.observerNames.length : 0}` : '';
        return `${nPlayers} : ${nBots} / ${totalPlayers} ${observerString}`;
    }

    isHost(playerName: string, gameSettings: OnlineGameSettings) {
        return playerName === gameSettings.playerNames[0];
    }

    get playerCountHeader() {
        return this.data.lobbyGameType === LobbyGameType.ObservableGame ? 'Joueur:IA/Max|Obsrv' : 'Joueur:IA/Max';
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
        if (this.data.lobbyGameType === LobbyGameType.ObservableGame) {
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
