/* eslint-disable @typescript-eslint/no-explicit-any */
import { StepperSelectionEvent, STEPPER_GLOBAL_OPTIONS } from '@angular/cdk/stepper';
import { AfterContentChecked, AfterViewInit, ChangeDetectorRef, Component, ElementRef, Inject, ViewChild, ViewChildren } from '@angular/core';
import { AbstractControl, FormArray, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { UIMagicCard, UI_MAGIC_CARD_ARRAY } from '@app/game-logic/actions/magic-card/magic-card-constants';
import {
    DEFAULT_NUMBER_OF_PLAYERS,
    DEFAULT_TIME_PER_TURN,
    MAX_NUMBER_OF_PLAYERS,
    MAX_TIME_PER_TURN,
    MIN_NUMBER_OF_PLAYERS,
    MIN_TIME_PER_TURN,
    STEP_TIME_PER_TURN,
} from '@app/game-logic/constants';
import { BotDifficulty } from '@app/services/bot-difficulty';
import { GameMode } from '@app/socket-handler/interfaces/game-mode.interface';
import { OnlineGameSettingsUI } from '@app/socket-handler/interfaces/game-settings-multi.interface';

enum NewGamePages {
    Settings = 0,
    GameParameters = 1,
    MagicCards = 2,
}

@Component({
    selector: 'app-new-online-game-form',
    templateUrl: './new-online-game-form.component.html',
    styleUrls: ['./new-online-game-form.component.scss'],
    providers: [
        {
            provide: STEPPER_GLOBAL_OPTIONS,
            useValue: { displayDefaultIndicatorType: false },
        },
    ],
})
export class NewOnlineGameFormComponent implements AfterContentChecked, AfterViewInit {
    @ViewChild('password') passwordElement: ElementRef;
    @ViewChildren('stepperIcon') private matStepperIconViewChildren: any;
    matStepperIcons: any;
    gameMode: string;
    selectedIndex = 0;
    onlineGameSettingsUIForm = new FormGroup({
        timePerTurn: new FormControl(DEFAULT_TIME_PER_TURN, [
            Validators.required,
            Validators.min(MIN_TIME_PER_TURN),
            Validators.max(MAX_TIME_PER_TURN),
        ]),
        privateGame: new FormControl(false, [Validators.required]),
        randomBonus: new FormControl(false, [Validators.required]),
        numberOfPlayers: new FormControl(DEFAULT_NUMBER_OF_PLAYERS, [
            Validators.required,
            Validators.min(MIN_NUMBER_OF_PLAYERS),
            Validators.max(MAX_NUMBER_OF_PLAYERS),
        ]),
        hasPassword: new FormControl(false, [Validators.required]),
        password: new FormControl({ value: '', disabled: true }, [Validators.required]),
        botDifficulty: new FormControl(BotDifficulty.Easy, [Validators.required]),
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
        this.gameMode = data.gameMode;
    }

    ngAfterViewInit(): void {
        this.matStepperIcons = this.matStepperIconViewChildren.toArray();
        this.cdref.detectChanges();
    }

    ngAfterContentChecked() {
        this.cdref.detectChanges();
        if (this.isMagicGame) this.onlineGameSettingsUIForm.addControl('magicCardIds', new FormArray([], [Validators.required]));
        else if (this.onlineGameSettingsUIForm.contains('magicCardIds')) this.onlineGameSettingsUIForm.removeControl('magicCardIds');
    }

    playGame(): void {
        const form = this.onlineGameSettingsUIForm.value;
        this.dialogRef.close(form);
    }

    cancel(): void {
        this.dialogRef.close();
        this.onlineGameSettingsUIForm.reset({
            timePerTurn: DEFAULT_TIME_PER_TURN,
            privateGame: false,
            randomBonus: false,
            numberOfPlayers: DEFAULT_NUMBER_OF_PLAYERS,
            magicCardIds: [],
            hasPassword: false,
            password: '',
            botDifficulty: BotDifficulty.Easy,
        });
    }

    onPasswordChange(event: MatCheckboxChange) {
        const hasPassword = event.checked;
        if (hasPassword) {
            this.onlineGameSettingsUIForm.get('password')?.enable();
            this.passwordElement.nativeElement.focus();
            this.onlineGameSettingsUIForm.updateValueAndValidity();
            return;
        }
        this.onlineGameSettingsUIForm.get('password')?.disable();
        this.onlineGameSettingsUIForm.get('password')?.reset();
        this.onlineGameSettingsUIForm.updateValueAndValidity();
    }

    onCheckChange(event: MatCheckboxChange, magicCard: UIMagicCard) {
        const formArray: FormArray = this.onlineGameSettingsUIForm.get('magicCardIds') as FormArray;
        if (event.checked) {
            formArray.push(new FormControl(magicCard.id));
            this.onlineGameSettingsUIForm.updateValueAndValidity();
            return;
        }
        const index = formArray.controls.findIndex((formControl: AbstractControl) => formControl.value === magicCard.id);
        formArray.removeAt(index);
        this.onlineGameSettingsUIForm.updateValueAndValidity();
    }

    someComplete(): boolean {
        const formArray: FormArray = this.onlineGameSettingsUIForm.get('magicCardIds') as FormArray;
        return formArray.length > 0 && !this.allChecked;
    }

    setAll(completed: boolean) {
        if (!completed) {
            const formArray: FormArray = this.onlineGameSettingsUIForm.get('magicCardIds') as FormArray;
            formArray.clear();
            this.onlineGameSettingsUIForm.updateValueAndValidity();
            return;
        }
        const controls = this.availableMagicCards.map((card: UIMagicCard) => new FormControl(card.id));
        const completeFormArray = new FormArray(controls);
        this.onlineGameSettingsUIForm.controls.magicCardIds = completeFormArray;
        this.onlineGameSettingsUIForm.updateValueAndValidity();
    }

    onStepChange(event: StepperSelectionEvent) {
        this.selectedIndex = event.selectedIndex;
    }

    getIsChecked(card: UIMagicCard): boolean {
        const formArray: FormArray = this.onlineGameSettingsUIForm.get('magicCardIds') as FormArray;
        return formArray.controls.find((formControl: AbstractControl) => formControl.value === card.id) !== undefined;
    }

    get allChecked(): boolean {
        const formArray: FormArray = this.onlineGameSettingsUIForm.get('magicCardIds') as FormArray;
        return formArray.length >= this.availableMagicCards.length;
    }

    get selectSettings(): boolean {
        return this.selectedIndex === NewGamePages.Settings;
    }

    get selectGameParamerers(): boolean {
        return this.selectedIndex === NewGamePages.GameParameters;
    }

    get selectMagicCards(): boolean {
        return this.selectedIndex === NewGamePages.MagicCards;
    }

    get formValid(): boolean {
        return this.onlineGameSettingsUIForm.valid;
    }

    get isMagicGame(): boolean {
        return this.gameMode === GameMode.Magic;
    }

    get availableMagicCards(): UIMagicCard[] {
        return UI_MAGIC_CARD_ARRAY;
    }
}
