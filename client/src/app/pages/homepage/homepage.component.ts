import { AfterViewInit, ChangeDetectorRef, Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { ErrorDialogComponent } from '@app/components/modals/error-dialog/error-dialog.component';
import { LeaderboardComponent } from '@app/components/modals/leaderboard/leaderboard.component';
import { AuthService } from '@app/services/auth.service';
import { PopChatService } from '@app/services/pop-chat.service';
import { first } from 'rxjs/operators';

@Component({
    selector: 'app-homepage',
    templateUrl: './homepage.component.html',
    styleUrls: ['./homepage.component.scss'],
})
export class HomepageComponent implements AfterViewInit {
    constructor(
        private matDialog: MatDialog,
        private authService: AuthService,
        private router: Router,
        public popOutService: PopChatService,
        private cdRef: ChangeDetectorRef,
    ) {}

    ngAfterViewInit(): void {
        this.popOutService.windowed$.subscribe(() => {
            this.cdRef.detectChanges();
        });
    }

    openLeaderboard() {
        this.matDialog.open(LeaderboardComponent, {
            width: '500px',
        });
    }

    logout() {
        this.authService.logout().subscribe(
            () => {
                const dialogRef = this.matDialog.open(ErrorDialogComponent, {
                    disableClose: true,
                    autoFocus: true,
                    data: 'Vous avez été déconnecté avec succès',
                });
                dialogRef
                    .afterClosed()
                    .pipe(first())
                    .subscribe(() => {
                        this.router.navigate(['/login']);
                    });
            },
            () => {
                this.matDialog.open(ErrorDialogComponent, {
                    disableClose: true,
                    autoFocus: true,
                    data: "Une erreur est survenue lors de la déconnexion : Le serveur n'est pas disponible",
                });
            },
        );
    }
}
