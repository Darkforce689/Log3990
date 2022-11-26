import { AfterContentChecked, AfterViewInit, ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { MessagesService } from '@app/chat/services/messages/messages.service';
import { ErrorDialogComponent } from '@app/components/modals/error-dialog/error-dialog.component';
import { getBotAvatar } from '@app/game-logic/utils';
import { PopChatService } from '@app/services/pop-chat.service';
import { OnlineGameSettings } from '@app/socket-handler/interfaces/game-settings-multi.interface';
import { NewOnlineGameSocketHandler } from '@app/socket-handler/new-online-game-socket-handler/new-online-game-socket-handler.service';
import { UserSearchComponent } from '@app/users/components/user-search/user-search.component';
import { UserCacheService } from '@app/users/services/user-cache.service';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';

const SPINNER_WIDTH_STROKE = 7;
const SPINNER_DIAMETER = 40;
@Component({
    selector: 'app-waiting-for-other-players',
    templateUrl: './waiting-for-other-players.component.html',
    styleUrls: ['./waiting-for-other-players.component.scss'],
})
export class WaitingForOtherPlayersComponent implements AfterContentChecked, OnInit, AfterViewInit, OnDestroy {
    spinnerStrokeWidth = SPINNER_WIDTH_STROKE;
    spinnerDiameter = SPINNER_DIAMETER;
    botDifficulty: string;
    gameSettings: OnlineGameSettings;
    avatars = new Map<string, string>();
    isGameOwner: boolean = false;
    currentPlayers: string[] = [];

    private forbidenUserInvite$$: Subscription | undefined;
    private gameDeleted$$: Subscription;
    private gameSettings$$: Subscription;
    private errors$$: Subscription;

    constructor(
        private cdref: ChangeDetectorRef,
        private socketHandler: NewOnlineGameSocketHandler,
        private dialog: MatDialog,
        private userCacheService: UserCacheService,
        public popOutService: PopChatService,
        private messageService: MessagesService,
        private router: Router,
    ) {}

    ngOnInit(): void {
        this.isGameOwner = this.socketHandler.isGameOwner;
        this.gameSettings$$ = this.socketHandler.gameSettings$.subscribe((gameSettings) => {
            if (!gameSettings) {
                return;
            }
            this.gameSettings = gameSettings;
            const { playerNames, tmpPlayerNames, botNames } = gameSettings;
            this.addPlayerIcons(playerNames);
            this.addPlayerIcons(tmpPlayerNames);
            this.addBotIcons(botNames);
            this.currentPlayers = this.getPlayers(gameSettings);
            this.cdref.detectChanges();
        });
        this.gameDeleted$$ = this.socketHandler.deletedGame$.subscribe((isDeleted) => {
            if (isDeleted) {
                const errorDialog = this.dialog.open(ErrorDialogComponent, { data: "L'hôte a annulé la partie" });
                errorDialog.afterClosed().subscribe(() => {
                    this.messageService.leaveGameConversation();
                    this.router.navigate(['/home']);
                });
            }
        });
        this.errors$$ = this.socketHandler.error$.subscribe((error) => {
            if (error) {
                const errorDialog = this.dialog.open(ErrorDialogComponent, { disableClose: true, autoFocus: true, data: error });
                errorDialog.afterClosed().subscribe(() => {
                    this.messageService.leaveGameConversation();
                    this.router.navigate(['/home']);
                });
            }
        });
    }

    ngAfterViewInit(): void {
        this.popOutService.windowed$.subscribe(() => {
            this.cdref.detectChanges();
        });
    }

    ngAfterContentChecked() {
        this.cdref.detectChanges();
    }

    ngOnDestroy() {
        this.gameDeleted$$.unsubscribe();
        this.errors$$.unsubscribe();
        this.gameSettings$$.unsubscribe();
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
        this.forbidenUserInvite$$ = this.socketHandler.gameSettings$.subscribe((gameSettings) => {
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
        // this.openLoadingGame()
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
        this.messageService.leaveGameConversation();
        this.socketHandler.disconnectSocket();
        this.router.navigate(['/home']);
    }

    // openLoadingGame(): MatDialogRef<LoadingGameComponent> {
    //     const loadingGameDialogConfig = new MatDialogConfig();
    //     loadingGameDialogConfig.disableClose = true;
    //     loadingGameDialogConfig.width = '255px';
    //     const loadingGameDialog = this.dialog.open(LoadingGameComponent, loadingGameDialogConfig);
    //     return loadingGameDialog;
    // }

    isHost() {
        return this.isGameOwner;
    }

    isThatPlayerHost(playerName: string) {
        if (!this.players) {
            return false;
        }
        return playerName === this.players[0];
    }

    getAvatarIcon(playerName: string): string {
        return this.avatars.get(playerName) ?? 'default';
    }

    getPlayers(gameSettings: OnlineGameSettings): string[] {
        if (!gameSettings) {
            return [];
        }
        const playerNames = gameSettings.playerNames;
        const nPlayers = playerNames.length;
        const totalPlayers = gameSettings.numberOfPlayers;
        const botNames = gameSettings.botNames.slice(nPlayers - 1, totalPlayers);
        return playerNames.concat(botNames);
    }

    isKickable(name: string) {
        return this.gameSettings.playerNames.includes(name) && !this.isThatPlayerHost(name);
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

    get isWaiting(): Observable<boolean> {
        const subject = new BehaviorSubject<boolean>(false);
        this.socketHandler.isWaiting$.subscribe((isDeleted) => {
            subject.next(isDeleted);
        });
        return subject;
    }

    get isPrivateGame(): Observable<boolean> {
        const subject = new BehaviorSubject<boolean>(false);
        this.socketHandler.gameSettings$.subscribe((gameSettings) => {
            if (!gameSettings) {
                return;
            }
            subject.next(gameSettings.privateGame);
        });
        return subject;
    }
}
