package com.example.polyscrabbleclient.auth.model


enum class AuthMode{LOG_IN, SIGN_UP}

data class AuthState(val email: String = "", val password: String = "", val mode:AuthMode = AuthMode.LOG_IN) { // TODO: check for better initalization
    fun isValid(): Boolean {
        return email.isNotEmpty() && password.isNotEmpty()
    }
}
