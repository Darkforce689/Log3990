import { AfterContentChecked, ChangeDetectorRef, Component, HostListener } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { ALPHABET } from '@app/game-logic/constants';

@Component({
    selector: 'app-joker-dialog',
    templateUrl: './joker-dialog.component.html',
    styleUrls: ['./joker-dialog.component.scss'],
})
export class JokerDialogComponent implements AfterContentChecked {
    alphabet = ALPHABET;
    value?: string;

    constructor(private dialogRef: MatDialogRef<JokerDialogComponent>, private cdref: ChangeDetectorRef) {}

    @HostListener('window:keyup', ['$event'])
    keypressEvent($event: KeyboardEvent) {
        $event.preventDefault();
        const char = $event.key.toUpperCase();
        if (this.alphabet.includes(char)) {
            this.value = char;
        }
    }

    ngAfterContentChecked() {
        this.cdref.detectChanges();
    }

    confirm(): void {
        this.dialogRef.close(this.value);
    }

    cancel(): void {
        this.dialogRef.close();
    }

    onClick(letter: string) {
        this.value = letter;
    }

    get formValid() {
        return !!this.value && this.value.length === 1;
    }
}
