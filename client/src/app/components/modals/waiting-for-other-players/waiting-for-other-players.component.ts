import { AfterContentChecked, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { OnlineGameSettings } from '@app/socket-handler/interfaces/game-settings-multi.interface';
import { NewOnlineGameSocketHandler } from '@app/socket-handler/new-online-game-socket-handler/new-online-game-socket-handler.service';
import { UserSearchComponent } from '@app/users/components/user-search/user-search.component';
import { UserCacheService } from '@app/users/services/user-cache.service';
import { BehaviorSubject, Subscription } from 'rxjs';

const SPINNER_WIDTH_STROKE = 7;
const SPINNER_DIAMETER = 40;
@Component({
    selector: 'app-waiting-for-other-players',
    templateUrl: './waiting-for-other-players.component.html',
    styleUrls: ['./waiting-for-other-players.component.scss'],
})
export class WaitingForOtherPlayersComponent implements AfterContentChecked, OnInit {
    spinnerStrokeWidth = SPINNER_WIDTH_STROKE;
    spinnerDiameter = SPINNER_DIAMETER;
    botDifficulty: string;
    gameSettings: OnlineGameSettings;
    avatars = new Map<string, string>();

    private forbidenUserInvite$$: Subscription | undefined;

    constructor(
        private cdref: ChangeDetectorRef,
        private socketHandler: NewOnlineGameSocketHandler,
        private dialog: MatDialog,
        private userCacheService: UserCacheService,
    ) {}

    ngOnInit(): void {
        this.socketHandler.gameSettings$.subscribe((gameSettings) => {
            if (!gameSettings) {
                return;
            }
            this.gameSettings = gameSettings;
            this.addPlayerIcons(gameSettings.playerNames);
            this.addPlayerIcons(gameSettings.tmpPlayerNames);
        });
    }

    ngAfterContentChecked() {
        this.cdref.detectChanges();
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

    invitePlayers() {
        this.forbidenUserInvite$$?.unsubscribe();
        if (!this.gameSettings) {
            throw Error("Can't invite players you haven't received the gamesettings yet.");
        }
        const { id, password } = this.gameSettings;
        const forbidenUsers$ = new BehaviorSubject<string[]>([]);
        const data = {
            args: {
                id,
                password,
            },
            forbidenUsers$,
        };
        this.forbidenUserInvite$$ = this.gameSettings$.subscribe((gameSettings) => {
            if (!gameSettings) {
                return;
            }
            const playerNames = gameSettings.playerNames;
            forbidenUsers$.next(playerNames);
        });
        this.dialog.open(UserSearchComponent, { data });
    }

    launchGame() {
        this.socketHandler.launchGame();
    }

    kickPlayer(playerId: string) {
        this.socketHandler.kickPlayer(playerId);
    }

    acceptPlayer(playerId: string) {
        this.socketHandler.acceptPlayer(playerId);
    }

    refusePlayer(playerId: string) {
        this.socketHandler.refusePlayer(playerId);
    }

    get canLaunchGame() {
        return this.socketHandler.isGameOwner && this.players.length <= this.numberOfPlayers;
    }

    cancel() {
        this.socketHandler.isGameOwner = false;
        this.socketHandler.disconnectSocket();
    }

    isHost(playerId: string) {
        return this.isThatPlayerHost(playerId) ? false : this.isGameOwner();
    }

    isGameOwner() {
        return this.socketHandler.isGameOwner;
    }

    isThatPlayerHost(playerId: string) {
        if (!this.players) {
            return false;
        }
        return playerId === this.players[0];
    }

    getAvatarIcon(playerName: string): string {
        return this.avatars.get(playerName) ?? 'default';
    }

    get gameSettings$() {
        return this.socketHandler.gameSettings$;
    }

    get pendingGameId(): string {
        if (!this.gameSettings) {
            return '';
        }
        return this.gameSettings.id;
    }

    get players(): string[] {
        if (!this.gameSettings) {
            return [];
        }
        return this.gameSettings.playerNames;
    }
    get numberOfPlayers() {
        if (!this.gameSettings) {
            return 0;
        }
        return this.gameSettings.numberOfPlayers;
    }

    get tmpPlayers(): string[] {
        if (!this.gameSettings) {
            return [];
        }
        return this.gameSettings.tmpPlayerNames;
    }

    get deletedGame(): boolean {
        return this.socketHandler.deletedGame$.value;
    }

    get isWaiting(): boolean {
        return this.socketHandler.isWaiting$.value;
    }

    get isPrivateGame(): boolean {
        if (!this.gameSettings) {
            return false;
        }
        return this.gameSettings.privateGame ?? false;
    }
}
