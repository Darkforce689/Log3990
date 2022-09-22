package com.example.polyscrabbleclient.auth.viewmodel

import androidx.lifecycle.ViewModel
import com.example.polyscrabbleclient.auth.model.AuthMode
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.polyscrabbleclient.auth.model.AuthState

class AuthenticationViewModel : ViewModel() {
    val state = MutableStateFlow(AuthState())

    sealed class AuthEvent {
        object UpdateAuthMode : AuthEvent()
        class EmailChanged(val email: String) : AuthEvent()
        class PasswordChanged(val password: String) : AuthEvent()
        object Authenticate : AuthEvent()
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
            is AuthEvent.Authenticate -> {
                authenticate()
            }
        }
    }

    private fun authenticate() {
        println(state.value)
    }

    private fun updateEmail(email:String) {
        state.value = state.value.copy(email = email)
    }

    private fun updatePassword(password: String) {
        state.value = state.value.copy(password = password)
    }

    private fun updateAuthMode() {
        val updatedMode = if (state.value.mode == AuthMode.LOG_IN) {
            AuthMode.SIGN_UP
        } else {
            AuthMode.LOG_IN
        }
        state.value =  state.value.copy(mode = updatedMode)
    }

}
