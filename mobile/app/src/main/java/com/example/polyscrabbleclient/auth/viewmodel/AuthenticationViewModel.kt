package com.example.polyscrabbleclient.auth.viewmodel

import androidx.lifecycle.ViewModel
import com.example.polyscrabbleclient.auth.model.AuthMode
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.polyscrabbleclient.auth.model.AuthLoginSate
import com.example.polyscrabbleclient.auth.model.AuthSignUpSate
import com.example.polyscrabbleclient.ui.theme.signUp

class AuthenticationViewModel : ViewModel() {
    val logInState = MutableStateFlow(AuthLoginSate())
    val signUpSate = MutableStateFlow(AuthSignUpSate())

    sealed class AuthEvent {
        object UpdateAuthMode : AuthEvent()
        class EmailChanged(val email: String) : AuthEvent()
        class PasswordChanged(val password: String) : AuthEvent()
        class UsernameChanged(val username: String) : AuthEvent()
        object Authenticate : AuthEvent()
        object CreateAccount : AuthEvent()
    }

    fun handleEvent(authEvent: AuthEvent) {
        when (authEvent) {
            is AuthEvent.UpdateAuthMode -> {
                updateAuthMode()
            }
            is AuthEvent.EmailChanged ->{
                updateEmail(authEvent.email)
            }
            is AuthEvent.PasswordChanged -> {
                updatePassword(authEvent.password)
            }
            is AuthEvent.UsernameChanged ->{
                updateUsername(authEvent.username)
            }
            is AuthEvent.Authenticate -> {
                authenticate()
            }
            is AuthEvent.CreateAccount -> {
                createAccount()
            }
        }
    }

    private fun authenticate() {
        println(logInState.value)
    }

    private fun createAccount() {
        println(signUpSate.value)
    }

    private fun updateEmail(email:String) {
        logInState.value = logInState.value.copy(email = email)
    }

    private fun updatePassword(password: String) {
        logInState.value = logInState.value.copy(password = password)
    }

    private fun updateUsername(username: String) {
        logInState.value = logInState.value.copy(password = username)
    }

    private fun updateAuthMode() {
        val updatedMode = if (logInState.value.mode == AuthMode.LOG_IN) {
            AuthMode.SIGN_UP
        } else {
            AuthMode.LOG_IN
        }
        logInState.value =  logInState.value.copy(mode = updatedMode)
    }

}
