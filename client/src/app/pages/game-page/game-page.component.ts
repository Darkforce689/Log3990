import { AfterViewInit, ChangeDetectorRef, Component, HostListener, OnDestroy } from '@angular/core';
import { MatDialog, MatDialogConfig, MatDialogRef } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { AbandonDialogComponent } from '@app/components/modals/abandon-dialog/abandon-dialog.component';
import { DisconnectedFromServerComponent } from '@app/components/modals/disconnected-from-server/disconnected-from-server.component';
import { WinnerDialogComponent, WinnerDialogData } from '@app/components/modals/winner-dialog/winner-dialog.component';
import { UIExchange } from '@app/game-logic/actions/ui-actions/ui-exchange';
import { UIInputControllerService } from '@app/game-logic/actions/ui-actions/ui-input-controller.service';
import { UIPlace } from '@app/game-logic/actions/ui-actions/ui-place';
import { RACK_LETTER_COUNT } from '@app/game-logic/constants';
import { GameInfoService } from '@app/game-logic/game/game-info/game-info.service';
import { GameManagerService } from '@app/game-logic/game/games/game-manager/game-manager.service';
import { InputType, UIInput } from '@app/game-logic/interfaces/ui-input';
import { PopChatService } from '@app/services/pop-chat.service';
import { Subscription } from 'rxjs';

@Component({
    selector: 'app-game-page',
    templateUrl: './game-page.component.html',
    styleUrls: ['./game-page.component.scss'],
})
export class GamePageComponent implements OnDestroy, AfterViewInit {
    dialogRef: MatDialogRef<DisconnectedFromServerComponent> | undefined;
    spectatePlayerNbr: number = 0;
    private disconnected$$: Subscription;
    private endOfGame$$: Subscription;

    constructor(
        private gameManager: GameManagerService,
        private info: GameInfoService,
        private router: Router,
        private dialog: MatDialog,
        private inputController: UIInputControllerService,
        public popOutService: PopChatService,
        private cdRef: ChangeDetectorRef,
    ) {
        try {
            this.gameManager.startGame();
        } catch (e) {
            this.router.navigate(['/']);
        }

        this.disconnected$$ = this.gameManager.disconnectedFromServer$.subscribe(() => {
            this.openDisconnected();
        });

        this.endOfGame$$ = this.info.isEndOfGame$.subscribe(() => {
            const winnerNames = this.info.winner.map((player) => player.name);
            const playerName = this.info.player.name;
            const isWinner = winnerNames.includes(playerName);
            const isObserver = this.isObserver;
            const data: WinnerDialogData = { winnerNames, isWinner, isObserver };
            this.dialog.open(WinnerDialogComponent, { disableClose: true, autoFocus: true, data });
        });
    }

    @HostListener('window:keyup', ['$event'])
    keypressEvent($event: KeyboardEvent) {
        const input: UIInput = { type: InputType.KeyPress, args: $event.key };
        this.inputController.receive(input);
    }

    ngAfterViewInit(): void {
        this.popOutService.windowed$.subscribe(() => {
            this.cdRef.detectChanges();
        });
    }

    ngOnDestroy() {
        this.disconnected$$.unsubscribe();
        this.endOfGame$$.unsubscribe();
    }

    receiveInput(input: UIInput) {
        this.inputController.receive(input);
    }

    abandon() {
        const dialogConfig = new MatDialogConfig();
        dialogConfig.disableClose = true;
        this.dialog.open(AbandonDialogComponent, dialogConfig);
    }

    quit() {
        this.gameManager.stopGame();
        this.router.navigate(['/']);
    }

    get isItMyTurn(): boolean {
        try {
            if (this.isEndOfGame) {
                return false;
            }
            return this.info.player === this.info.activePlayer;
        } catch (e) {
            return false;
        }
    }

    get isEndOfGame(): boolean {
        return this.info.isEndOfGame;
    }

    get canPlace(): boolean {
        return this.isItMyTurn && this.inputController.activeAction instanceof UIPlace && this.inputController.canBeExecuted;
    }

    get canExchange(): boolean {
        return (
            this.isItMyTurn &&
            this.inputController.activeAction instanceof UIExchange &&
            this.inputController.canBeExecuted &&
            this.info.numberOfLettersRemaining > RACK_LETTER_COUNT
        );
    }

    get canPass(): boolean {
        return this.isItMyTurn;
    }

    get canCancel(): boolean {
        return this.canPlace || this.canExchange;
    }

    get isMagicGame() {
        return this.info.isMagicGame;
    }

    get isObserver() {
        return this.info.players.find((player) => player.name === this.gameManager.userName) === undefined;
    }

    pass() {
        this.inputController.pass(this.info.player);
    }

    confirm() {
        this.inputController.confirm();
    }

    cancel() {
        this.inputController.cancel();
    }

    nextPlayer() {
        this.spectatePlayerNbr = (this.spectatePlayerNbr + 1) % this.info.players.length;
        this.info.receivePlayer(this.info.players[this.spectatePlayerNbr]);
    }

    previousPlayer() {
        this.spectatePlayerNbr = this.spectatePlayerNbr === 0 ? this.info.players.length - 1 : this.spectatePlayerNbr - 1;
        this.info.receivePlayer(this.info.players[this.spectatePlayerNbr]);
    }

    openDisconnected() {
        if (this.dialogRef) {
            return;
        }
        this.gameManager.stopGame();
        const disconnectedDialogConfig = new MatDialogConfig();
        disconnectedDialogConfig.autoFocus = true;
        disconnectedDialogConfig.disableClose = true;
        disconnectedDialogConfig.minWidth = 550;
        this.dialogRef = this.dialog.open(DisconnectedFromServerComponent, disconnectedDialogConfig);
        this.dialogRef.afterClosed().subscribe(() => {
            this.dialogRef = undefined;
            this.router.navigate(['/']);
        });
    }
}
