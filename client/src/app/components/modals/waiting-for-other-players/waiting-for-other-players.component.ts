import { AfterContentChecked, ChangeDetectorRef, Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { NewOnlineGameSocketHandler } from '@app/socket-handler/new-online-game-socket-handler/new-online-game-socket-handler.service';
import { UserSearchComponent } from '@app/users/components/user-search/user-search.component';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';

const SPINNER_WIDTH_STROKE = 7;
const SPINNER_DIAMETER = 40;
@Component({
    selector: 'app-waiting-for-other-players',
    templateUrl: './waiting-for-other-players.component.html',
    styleUrls: ['./waiting-for-other-players.component.scss'],
})
export class WaitingForOtherPlayersComponent implements AfterContentChecked {
    spinnerStrokeWidth = SPINNER_WIDTH_STROKE;
    spinnerDiameter = SPINNER_DIAMETER;
    botDifficulty: string;

    private forbidenUserInvite$$: Subscription | undefined;

    constructor(private cdref: ChangeDetectorRef, private socketHandler: NewOnlineGameSocketHandler, private dialog: MatDialog) {}

    ngAfterContentChecked() {
        this.cdref.detectChanges();
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

    get gameSettings$() {
        return this.socketHandler.gameSettings$;
    }

    get gameSettings() {
        return this.gameSettings$.value;
    }

    get pendingGameId$(): Observable<string | undefined> {
        return this.socketHandler.pendingGameId$.asObservable();
    }

    get players() {
        return this.gameSettings?.playerNames ?? [];
    }
    get numberOfPlayers() {
        return this.gameSettings?.numberOfPlayers ?? 0;
    }

    get tmpPlayers() {
        return this.gameSettings?.tmpPlayerNames;
    }

    get deletedGame(): boolean {
        return this.socketHandler.deletedGame$.value;
    }

    get isWaiting(): boolean {
        return this.socketHandler.isWaiting$.value;
    }

    get isPrivateGame(): boolean {
        return this.gameSettings?.privateGame ?? false;
    }
}
