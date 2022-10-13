package com.example.polyscrabbleclient.auth.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.polyscrabbleclient.BuildConfig
import com.example.polyscrabbleclient.auth.model.AuthError
import com.example.polyscrabbleclient.auth.model.AuthSignUpSate
import com.example.polyscrabbleclient.auth.model.serverResponse.SignUpRes
import com.example.polyscrabbleclient.ui.theme.*
import com.example.polyscrabbleclient.utils.constants.MAX_NAME_LENGTH
import com.example.polyscrabbleclient.utils.constants.MAX_PASSWORD_LENGTH
import com.example.polyscrabbleclient.utils.constants.MIN_NAME_LENGTH
import com.example.polyscrabbleclient.utils.constants.MIN_PASSWORD_LENGTH
import com.example.polyscrabbleclient.utils.httprequests.ScrabbleHttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class SignUpViewModel : ViewModel() {
    val signUp = mutableStateOf(AuthSignUpSate())
    val isCreated = mutableStateOf(false)
    var isInProcess = mutableStateOf(false)

    sealed class AuthEvent {
        class EmailChanged(val email: String) : AuthEvent()
        class PasswordChanged(val password: String) : AuthEvent()
        class UsernameChanged(val username: String) : AuthEvent()
        class ValidateUsername(val username: String) : AuthEvent()
        class ValidateEmail(val email: String) : AuthEvent()
        class ValidatePassword(val password: String) : AuthEvent()
        object CreateAccount : AuthEvent()
    }

    fun handleSignUpEvent(authEvent: AuthEvent) {
        when (authEvent) {
            is AuthEvent.EmailChanged -> {
                updateEmail(authEvent.email)
            }
            is AuthEvent.PasswordChanged -> {
                updatePassword(authEvent.password)
            }
            is AuthEvent.UsernameChanged -> {
                updateUsername(authEvent.username)
            }
            is AuthEvent.ValidateUsername -> {
                validateUsername(authEvent.username)
            }
            is AuthEvent.ValidateEmail -> {
                validateEmail(authEvent.email)
            }
            is AuthEvent.ValidatePassword -> {
                validatePassword(authEvent.password)
            }
            is AuthEvent.CreateAccount -> {
                createAccountRequest()
            }
        }
    }

    fun reset() {
        isCreated.value = false
        isInProcess.value = false
        signUp.value.name = ""
        signUp.value.email = ""
        signUp.value.password = ""
    }

    private fun updateEmail(email: String) {
        signUp.value.emailError.value = ""
        if (email.isBlank()) {
            signUp.value = signUp.value.copy(email = email)
            return
        }
        if (!email[email.length - 1].isWhitespace()) {
            signUp.value = signUp.value.copy(email = email)
        }
    }

    private fun updatePassword(password: String) {
        signUp.value.passwordError.value = ""
        signUp.value = signUp.value.copy(password = password)
    }

    private fun updateUsername(username: String) {
        signUp.value.usenameError.value = ""
        if (username.isBlank()) {
            signUp.value = signUp.value.copy(name = username)
            return
        }
        if (!username[username.length - 1].isWhitespace()) {
            signUp.value = signUp.value.copy(name = username)
        }
    }

    private fun validateUsername(username: String) {
        if (!AuthValidation.isUsernameValid(username)) {
            signUp.value.usenameError.value =
                Invalid_username_creation(MIN_NAME_LENGTH, MAX_NAME_LENGTH).message
        }
    }

    private fun validateEmail(email: String) {
        if (!AuthValidation.isValidEmail(email)) {
            signUp.value.emailError.value = wrong_form_email
        }
    }

    private fun validatePassword(password: String) {
        if (!AuthValidation.isValidPassword(password)) {
            signUp.value.passwordError.value =
                Invalid_password_creation(MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH).message
        }
    }

    private fun isFormValid(): Boolean {
        validateEmail(signUp.value.email)
        validateUsername(signUp.value.email)
        validatePassword(signUp.value.password)
        return signUp.value.emailError.value.isEmpty() &&
            signUp.value.usenameError.value.isEmpty() &&
            signUp.value.passwordError.value.isEmpty()
    }


    private fun hasAtLeastOneEmptyField(): Boolean {
        val hasName = signUp.value.name.isNotEmpty()
        val hasEmail = signUp.value.email.isNotEmpty()
        val hasPassword = signUp.value.password.isNotEmpty()
        if (!hasName) {
            signUp.value.usenameError.value = missing_field
        }
        if (!hasEmail) {
            signUp.value.emailError.value = missing_field
        }
        if (!hasPassword) {
            signUp.value.passwordError.value = missing_field
        }
        return !hasEmail || !hasName || !hasPassword
    }

    private fun createAccountRequest() {
        if (hasAtLeastOneEmptyField()) {
            return
        }
        if (!isFormValid()) {
            return
        }

        isInProcess.value = true
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val response = ScrabbleHttpClient.post(
                    URL(BuildConfig.COMMUNICATION_URL + "/auth/register"),
                    signUp.value,
                    SignUpRes::class.java
                )
                if (response != null) {
                    if (response.user != null) {
                        isCreated.value = true
                    } else {
                        setServerErrors(response.errors)
                    }
                }
                isInProcess.value = false
            }
        }
    }

    private fun setServerErrors(serverErrors: List<String>?) {
        fun setError(error: String) {
            if (error == AuthError.EmailAlreadyTaken.label) {
                signUp.value.emailError.value = email_not_unique
            }
            if (error == AuthError.NameAlreadyTaken.label) {
                signUp.value.usenameError.value = userName_not_unique
            }
        }
        serverErrors?.map { content -> setError(content) } as List<AuthError>
    }
}

