package com.example.polyscrabbleclient.auth.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.polyscrabbleclient.BuildConfig
import com.example.polyscrabbleclient.auth.model.AuthError
import com.example.polyscrabbleclient.auth.model.LoginSate
import com.example.polyscrabbleclient.auth.model.serverResponse.LoginRes
import com.example.polyscrabbleclient.ui.theme.*
import com.example.polyscrabbleclient.utils.httprequests.ScrabbleHttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL


class AuthenticationViewModel : ViewModel() {
    val logInState = mutableStateOf(LoginSate())
    val isAuthenticate = mutableStateOf(false)
    var isInProcess = mutableStateOf(false)

    sealed class AuthEvent {
        class EmailChanged(val email: String) : AuthEvent()
        class PasswordChanged(val password: String) : AuthEvent()
        class ValidateEmail(val email: String) : AuthEvent()
        object Authenticate : AuthEvent()
    }

    fun handleLoginEvent(authEvent: AuthEvent) {
        when (authEvent) {
            is AuthEvent.EmailChanged -> {
                updateEmail(authEvent.email)
            }
            is AuthEvent.PasswordChanged -> {
                updatePassword(authEvent.password)
            }
            is AuthEvent.ValidateEmail -> {
                validateEmail(authEvent.email)
            }
            is AuthEvent.Authenticate -> {
                authenticateRequest()
            }
        }
    }

    fun reset() {
        isAuthenticate.value = false
        isInProcess.value = false
        logInState.value.email = ""
        logInState.value.password = ""
        logInState.value.emailError = mutableStateOf("")
        logInState.value.passwordError = mutableStateOf("")
    }

    private fun updateEmail(email: String) {
        logInState.value.emailError.value = ""
        logInState.value = logInState.value.copy(email = email)
    }

    private fun updatePassword(password: String) {
        logInState.value.passwordError.value = ""
        logInState.value = logInState.value.copy(password = password)
    }

    private fun validateEmail(email: String) {
        if (!AuthValidation.isValidEmail(email)) {
            logInState.value.emailError.value = wrong_form_email
        }
    }

    private fun isFormValid(): Boolean {
        validateEmail(logInState.value.email)
        return logInState.value.emailError.value.isEmpty() &&
            logInState.value.passwordError.value.isEmpty()
    }

    private fun hasAtLeastOneEmptyField(): Boolean {
        val hasEmail = logInState.value.email.isNotEmpty()
        val hasPassword = logInState.value.password.isNotEmpty()
        if (!hasEmail) {
            logInState.value.emailError.value = missing_field
        }
        if (!hasPassword) {
            logInState.value.passwordError.value = missing_field
        }
        return !hasEmail || !hasPassword
    }

    private fun authenticateRequest() {
        // TODO: notify user when server is down
        if (hasAtLeastOneEmptyField()) {
            return
        }
        if (!isFormValid()) {
            return
        }
        isInProcess.value = true
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val response: LoginRes?
                try {
                    response = ScrabbleHttpClient.post(
                        URL(BuildConfig.COMMUNICATION_URL + "/auth/login"),
                        logInState.value,
                        LoginRes::class.java
                    )

                    if (response == null) {
                        // add notification here
                        return@withContext
                    }
                    if (response.message != null) {
                        isAuthenticate.value = true
                        return@withContext
                    }
                    setServerErrors(response.errors)
                    return@withContext
                } catch (e: Exception) {
                    // or add notification here
                    e.printStackTrace()
                } finally {
                    isInProcess.value = false
                }
            }
        }
    }

    private fun setServerErrors(serverErrors: List<String>?) {
        fun setError(error: String) {
            if (error == AuthError.EmailNotFound.label) {
                logInState.value.emailError.value = invalid_email
            }
            if (error == AuthError.InvalidPassword.label) {
                logInState.value.passwordError.value = invalid_password
            }
            if (error == AuthError.AlreadyAuth.label) {
                logInState.value.passwordError.value = already_auth
            }
        }
        serverErrors?.map { content -> setError(content) } as List<AuthError>
    }
}


