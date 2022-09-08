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

    get canLaunchGame() {
        return this.socketHandler.isGameOwner;
    }

    cancel() {
        this.socketHandler.isGameOwner = false;
        this.socketHandler.disconnectSocket();
    }

    get pendingGameId$(): Observable<string | undefined> {
        return this.socketHandler.pendingGameId$.asObservable();
    }
}
