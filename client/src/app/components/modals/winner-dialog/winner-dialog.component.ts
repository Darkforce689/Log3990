import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

export interface WinnerDialogData {
    winnerNames: string[];
    isWinner: boolean;
    isObserver: boolean;
}
const arrayOffset = -1;
@Component({
    templateUrl: './winner-dialog.component.html',
    styleUrls: ['./winner-dialog.component.scss'],
})
export class WinnerDialogComponent {
    constructor(@Inject(MAT_DIALOG_DATA) public winnerData: WinnerDialogData, private dialogRef: MatDialogRef<WinnerDialogComponent>) {}

    getWinnerMessage(): string {
        const winnerNames = this.winnerData.winnerNames;
        if (winnerNames.length === 1) {
            return `Le gagnant de la partie est ${winnerNames[0]}.`;
        }
        if (winnerNames.length === 0) {
            return 'Tous les joueurs ont abandonné la partie.';
        }
        const winners = 'Les gagnants de la partie sont ';
        return winners.concat(
            winnerNames
                .slice(0, arrayOffset)
                .join(', ')
                .concat(' et ' + winnerNames[winnerNames.length - 1] + '.'),
        );
    }

    getCongratulationMessage(): string {
        const userIsWinner = this.winnerData.isWinner;
        const userIsObserver = this.winnerData.isObserver;
        if (userIsObserver) {
            return 'Fin de la partie.';
        }
        if (userIsWinner) {
            return 'Félicitation!';
        }
        return 'Dommage...';
    }

    close() {
        this.dialogRef.close();
    }
}
