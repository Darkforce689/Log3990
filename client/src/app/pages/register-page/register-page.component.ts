import { HttpErrorResponse } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MAX_NAME_LENGTH, MAX_PASSWORD_LENGTH, MIN_NAME_LENGTH, MIN_PASSWORD_LENGTH, NO_WHITE_SPACE_RGX } from '@app/game-logic/constants';
import { UserCreationError } from '@app/services/auth-errors';
import { AuthService } from '@app/services/auth.service';
import { first } from 'rxjs/operators';

@Component({
    templateUrl: './register-page.component.html',
    styleUrls: ['./register-page.component.scss'],
})
export class RegisterPageComponent {
    registerForm = new FormGroup({
        name: new FormControl('', [
            Validators.required,
            Validators.minLength(MIN_NAME_LENGTH),
            Validators.maxLength(MAX_NAME_LENGTH),
            Validators.pattern(NO_WHITE_SPACE_RGX),
        ]),
        email: new FormControl('', [Validators.required, Validators.email]),
        password: new FormControl('', [Validators.required, Validators.minLength(MIN_PASSWORD_LENGTH), Validators.maxLength(MAX_PASSWORD_LENGTH)]),
    });

    constructor(private authService: AuthService, private router: Router) {}

    register() {
        if (!this.isEmailValidToSend || !this.isNameValidToSend || !this.isPasswordValidToSend) {
            return;
        }
        const result = this.authService.register(this.registerForm.value);
        result.pipe(first()).subscribe(
            () => {
                this.router.navigate(['/login']);
            },
            (httpError: HttpErrorResponse) => {
                const {
                    error: { errors },
                } = httpError;
                errors.forEach((error: string) => {
                    if (error === UserCreationError.EmailAlreadyTaken) {
                        this.registerForm.controls.email.setErrors({ notUnique: true });
                    }

                    if (error === UserCreationError.NameAlreadyTaken) {
                        this.registerForm.controls.name.setErrors({ notUnique: true });
                    }
                });
            },
        );
    }

    get isEmailValidToSend() {
        return this.registerForm.controls.email.valid;
    }

    get isNameValidToSend() {
        return this.registerForm.controls.name.valid;
    }

    get isPasswordValidToSend() {
        return this.registerForm.controls.password.valid;
    }

    get isEmailValid() {
        return !this.registerForm.controls.email.hasError('email') || this.registerForm.controls.name.hasError('required');
    }

    get isNameValid() {
        return (
            (!this.registerForm.controls.name.hasError('minlength') &&
                !this.registerForm.controls.name.hasError('pattern') &&
                !this.registerForm.controls.name.hasError('maxlength')) ||
            this.registerForm.controls.name.hasError('required')
        );
    }

    get isPasswordValid() {
        return (
            (!this.registerForm.controls.password.hasError('minlength') && !this.registerForm.controls.password.hasError('maxlength')) ||
            this.registerForm.controls.password.hasError('required')
        );
    }

    get maxNameLength() {
        return MAX_NAME_LENGTH;
    }

    get minNameLength() {
        return MIN_NAME_LENGTH;
    }

    get maxPasswordLength() {
        return MAX_PASSWORD_LENGTH;
    }

    get minPasswordLength() {
        return MIN_PASSWORD_LENGTH;
    }
}
