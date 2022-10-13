import { AfterContentChecked, ChangeDetectorRef, Component, Inject } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import {
    DEFAULT_NUMBER_OF_PLAYERS,
    DEFAULT_TIME_PER_TURN,
    MAX_NUMBER_OF_PLAYERS,
    MAX_TIME_PER_TURN,
    MIN_NUMBER_OF_PLAYERS,
    MIN_TIME_PER_TURN,
    STEP_TIME_PER_TURN
} from '@app/game-logic/constants';
import { OnlineGameSettingsUI } from '@app/socket-handler/interfaces/game-settings-multi.interface';

// const NO_WHITE_SPACE_RGX = /^\S*$/;

@Component({
    selector: 'app-new-online-game-form',
    templateUrl: './new-online-game-form.component.html',
    styleUrls: ['./new-online-game-form.component.scss'],
})
export class NewOnlineGameFormComponent implements AfterContentChecked {
    onlineGameSettingsUIForm = new FormGroup({
        timePerTurn: new FormControl(DEFAULT_TIME_PER_TURN, [
            Validators.required,
            Validators.min(MIN_TIME_PER_TURN),
            Validators.max(MAX_TIME_PER_TURN),
        ]),
        randomBonus: new FormControl(false, [Validators.required]),
        numberOfPlayers: new FormControl(DEFAULT_NUMBER_OF_PLAYERS, [
            Validators.required,
            Validators.min(MIN_NUMBER_OF_PLAYERS),
            Validators.max(MAX_NUMBER_OF_PLAYERS),
        ]),
    });

    minTimePerTurn = MIN_TIME_PER_TURN;
    maxTimePerTurn = MAX_TIME_PER_TURN;
    stepTimePerTurn = STEP_TIME_PER_TURN;
    minNumberOfPlayers = MIN_NUMBER_OF_PLAYERS;
    maxNumberOfPlayers = MAX_NUMBER_OF_PLAYERS;

    constructor(
        @Inject(MAT_DIALOG_DATA) public data: OnlineGameSettingsUI,
        private dialogRef: MatDialogRef<NewOnlineGameFormComponent>,
        private cdref: ChangeDetectorRef,
    ) {
        this.onInit();
    }

    onInit() {
        return;
    }

    ngAfterContentChecked() {
        this.cdref.detectChanges();
    }

    playGame(): void {
        const form = this.onlineGameSettingsUIForm.value;
        this.dialogRef.close(form);
    }

    cancel(): void {
        this.dialogRef.close();
        this.onlineGameSettingsUIForm.reset({
            timePerTurn: DEFAULT_TIME_PER_TURN,
            randomBonus: false,
        });
    }

    get formValid() {
        return this.onlineGameSettingsUIForm.valid;
    }
}
