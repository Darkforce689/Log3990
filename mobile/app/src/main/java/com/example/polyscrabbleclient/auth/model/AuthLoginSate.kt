package com.example.polyscrabbleclient.auth.model

import androidx.annotation.StringRes
import com.example.polyscrabbleclient.ui.theme.*


enum class AuthMode{LOG_IN, SIGN_UP}
//class AuthMode {}
//object SignedOut : UiState()
//object InProgress : UiState()
//object Error : UiState()
//object SignIn : UiState()

enum class AuthErrors(val label: String) {
    EmailNotFound(invalid_email),
    WrongFormEmail(wrong_form_email),
    InvalidPassword(invalid_password),
    InvalidUsername(invalid_userName),
    NotUniqueUserName(userName_not_unique),
    InvalidPasswordCreation(invalid_password_creation)
}

data class AuthLoginSate(val email: String = "", val password: String = "", val mode:AuthMode = AuthMode.LOG_IN) { // TODO: check for better initalization
    fun isValid(): Boolean {
        return email.isNotEmpty() && password.isNotEmpty()
        //email exists, email form
    }
}

data class AuthSignUpSate(val username: String = "", val email: String = "", val password: String = "", val mode:AuthMode = AuthMode.LOG_IN) { // TODO: check for better initalization
    fun isValid(): Boolean {
        return email.isNotEmpty() && password.isNotEmpty()
        // name: min, max, no white, unique =>server
        // email form, unique => server
        // password min, max,
    }
}
