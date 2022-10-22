import { AfterContentChecked, ChangeDetectorRef, Component } from '@angular/core';
import { NewOnlineGameSocketHandler } from '@app/socket-handler/new-online-game-socket-handler/new-online-game-socket-handler.service';
import { Observable } from 'rxjs';

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
    isSoloStarted: boolean = false;
    constructor(private cdref: ChangeDetectorRef, private socketHandler: NewOnlineGameSocketHandler) {}

    ngAfterContentChecked() {
        this.cdref.detectChanges();
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
        if (this.isThatPlayerHost(playerId)) {
            return false;
        }
        return this.socketHandler.isGameOwner;
    }

    isThatPlayerHost(playerId: string) {
        if (!this.players) {
            return;
        }
        return playerId === this.players[0];
    }

    get pendingGameId$(): Observable<string | undefined> {
        return this.socketHandler.pendingGameId$.asObservable();
    }

    get players() {
        return this.socketHandler.gameSettings$.value?.playerNames ?? [];
    }
    get numberOfPlayers() {
        return this.socketHandler.gameSettings$.value?.numberOfPlayers ?? 0;
    }

    get tmpPlayers() {
        return this.socketHandler.gameSettings$.value?.tmpPlayerNames;
    }

    get deletedGame(): boolean {
        return this.socketHandler.deletedGame$.value;
    }

    get kickedFromGame(): boolean {
        return this.socketHandler.kickedFromGame$.value;
    }

    get isWaiting(): boolean {
        return this.socketHandler.isWaiting$.value;
    }
}
