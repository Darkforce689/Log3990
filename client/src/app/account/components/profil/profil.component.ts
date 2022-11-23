import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MessagesService } from '@app/chat/services/messages/messages.service';
import { MAX_NAME_LENGTH, MIN_NAME_LENGTH, NO_WHITE_SPACE_RGX } from '@app/game-logic/constants';
import { User } from '@app/pages/register-page/user.interface';
import { AccountService } from '@app/services/account.service';
import { UserCreationError } from '@app/services/auth-errors';
import { first } from 'rxjs/operators';

@Component({
    selector: 'app-profil',
    templateUrl: './profil.component.html',
    styleUrls: ['./profil.component.scss'],
})
export class ProfilComponent implements OnInit {
    user: User = {
        _id: '',
        name: '',
        email: '',
        avatar: 'default',
        averagePoints: 0,
        nGamePlayed: 0,
        nGameWon: 0,
        averageTimePerGame: 0,
    };
    name = new FormControl('', [
        Validators.required,
        Validators.minLength(MIN_NAME_LENGTH),
        Validators.maxLength(MAX_NAME_LENGTH),
        Validators.pattern(NO_WHITE_SPACE_RGX),
    ]);
    avatar: string;

    constructor(private accountService: AccountService, private snackBar: MatSnackBar, private messageService: MessagesService) {}

    ngOnInit(): void {
        this.accountService.account$.subscribe((user: User | undefined) => {
            if (user) {
                this.name.setValue(user.name);
                this.user = user;
            }
        });
    }

    updateAccount() {
        this.updateAvatar();
        this.updateName();
    }

    setAvatar(src: string) {
        this.avatar = src;
    }

    private updateName() {
        if (this.name.value === this.user.name) {
            return;
        }
        if (!this.isNameValidToSend) {
            return;
        }
        const result = this.accountService.updateName(this.name.value);

        result.pipe(first()).subscribe(
            () => {
                this.accountService.actualizeAccount();
                this.openSnackBar('Modification réussie!');
                this.messageService.refreshMessages();
            },
            (httpError: HttpErrorResponse) => {
                const { error: errors } = httpError;
                errors.forEach((error: string) => {
                    if (error === UserCreationError.NameAlreadyTaken) {
                        this.name.setErrors({ notUnique: true });
                    }
                });
            },
        );
    }

    private updateAvatar() {
        if (this.avatar === this.user.avatar) {
            return;
        }
        if (!this.avatar) {
            return;
        }
        const result = this.accountService.updateAvatar(this.avatar);

        result.pipe(first()).subscribe(
            () => {
                this.accountService.actualizeAccount();
                this.openSnackBar('Modification réussie!');
                this.messageService.refreshMessages();
            },
            () => {
                this.openSnackBar('Erreur : la modification a échoué');
            },
        );
    }

    private openSnackBar(message: string) {
        return this.snackBar.open(message, '', { duration: 3000 });
    }

    get isNameValidToSend() {
        return this.name.valid;
    }

    get isNameValid() {
        return (
            (!this.name.hasError('minlength') && !this.name.hasError('pattern') && !this.name.hasError('maxlength')) || this.name.hasError('required')
        );
    }

    get maxNameLength() {
        return MAX_NAME_LENGTH;
    }

    get minNameLength() {
        return MIN_NAME_LENGTH;
    }
}
