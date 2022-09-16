import { AfterContentChecked, ChangeDetectorRef, Component, Inject, OnInit } from '@angular/core';
import { AbstractControl, FormControl, ValidatorFn, Validators } from '@angular/forms';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ErrorDialogComponent } from '@app/components/modals/error-dialog/error-dialog.component';
import { MAX_NAME_LENGTH, MIN_NAME_LENGTH } from '@app/game-logic/constants';
import { GameLauncherService } from '@app/socket-handler/game-launcher/game-laucher';
import { OnlineGameSettings } from '@app/socket-handler/interfaces/game-settings-multi.interface';
import { NewOnlineGameSocketHandler } from '@app/socket-handler/new-online-game-socket-handler/new-online-game-socket-handler.service';
const NO_WHITE_SPACE_RGX = /^\S*$/;
@Component({
    selector: 'app-join-online-game',
    templateUrl: './join-online-game.component.html',
    styleUrls: ['./join-online-game.component.scss'],
})
export class JoinOnlineGameComponent implements AfterContentChecked, OnInit {
    myName: FormControl;
    private playerNames: string[];

    constructor(
        @Inject(MAT_DIALOG_DATA) public data: OnlineGameSettings,
        private dialogRef: MatDialogRef<JoinOnlineGameComponent>,
        private dialog: MatDialog,
        private cdref: ChangeDetectorRef,
        private socketHandler: NewOnlineGameSocketHandler,
        private gameLaucherService: GameLauncherService,
    ) {}

    ngOnInit() {
        this.playerNames = this.data.playerNames;
        this.myName = new FormControl('', [
            Validators.required,
            Validators.minLength(MIN_NAME_LENGTH),
            Validators.maxLength(MAX_NAME_LENGTH),
            Validators.pattern(NO_WHITE_SPACE_RGX),
            this.forbiddenNameValidator(),
        ]);
    }

    ngAfterContentChecked() {
        this.cdref.detectChanges();
    }

    cancel(): void {
        this.dialogRef.close();
        this.myName.reset();
    }

    sendParameter(): void {
        this.dialogRef.close(this.myName.value);
        this.socketHandler.joinPendingGame(this.data.id, this.myName.value);
        this.socketHandler.error$.subscribe((error: string) => {
            if (error) {
                this.dialog.open(ErrorDialogComponent, { disableClose: true, autoFocus: true, data: error });
            }
        });

        this.gameLaucherService.waitForOnlineGameStart(this.myName.value);
    }

    private forbiddenNameValidator(): ValidatorFn {
        return (control: AbstractControl): { [key: string]: unknown } | null =>
            // TODO GL3A22107-5 : FIX NAME VALIDATOR (next line is faulty)
            control.value !== this.playerNames ? null : { forbidden: control.value };
    }

    get valid() {
        return this.myName.valid;
    }

    get randomBonusType() {
        return this.data.randomBonus ? 'est activé' : 'est désactivé';
    }

    get privateGameType() {
        return this.data.privateGame ? 'Privée' : 'Publique';
    }
}
