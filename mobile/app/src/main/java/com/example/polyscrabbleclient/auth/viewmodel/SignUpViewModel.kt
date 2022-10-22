package com.example.polyscrabbleclient.auth.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.polyscrabbleclient.BuildConfig
import com.example.polyscrabbleclient.auth.model.AuthError
import com.example.polyscrabbleclient.auth.model.AuthSignUpSate
import com.example.polyscrabbleclient.auth.model.ErrorState
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
    val errorState = ErrorState()
    val isCreated = mutableStateOf(false)
    var isInProcess = mutableStateOf(false)

    sealed class AuthEvent {
        class EmailChanged(val email: String) : AuthEvent()
        class PasswordChanged(val password: String) : AuthEvent()
        class UsernameChanged(val username: String) : AuthEvent()
        class ValidateUsername(val username: String) : AuthEvent()
        class ValidateEmail(val email: String) : AuthEvent()
        class ValidatePassword(val password: String) : AuthEvent()
        class ValidateAvatar(val avatar: String) : AuthEvent()
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
            is AuthEvent.ValidateAvatar -> {
                updateAvatar(authEvent.avatar)
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
        signUp.value.avatar = ""
    }

    private fun updateEmail(email: String) {
        errorState.emailError.value = ""
        if (email.isBlank()) {
            signUp.value = signUp.value.copy(email = email)
            return
        }
        if (!email[email.length - 1].isWhitespace()) {
            signUp.value = signUp.value.copy(email = email)
        }
    }

    private fun updatePassword(password: String) {
        errorState.passwordError.value = ""
        signUp.value = signUp.value.copy(password = password)
    }

    private fun updateUsername(username: String) {
        errorState.usenameError.value = ""
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
            errorState.usenameError.value =
                invalid_username_creation(MIN_NAME_LENGTH, MAX_NAME_LENGTH)
        }
    }

    private fun validateEmail(email: String) {
        if (!AuthValidation.isValidEmail(email)) {
            errorState.emailError.value = wrong_form_email
        }
    }

    private fun validatePassword(password: String) {
        if (!AuthValidation.isValidPassword(password)) {
            errorState.passwordError.value =
                invalid_password_creation(MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH)
        }
    }

    private fun updateAvatar(avatarName: String) {
        errorState.avatarError.value = ""
        signUp.value = signUp.value.copy(avatar = avatarName)
    }

    private fun isFormValid(): Boolean {
        validateEmail(signUp.value.email)
        validateUsername(signUp.value.email)
        validatePassword(signUp.value.password)
        return errorState.emailError.value.isEmpty() &&
            errorState.usenameError.value.isEmpty() &&
            errorState.passwordError.value.isEmpty()
    }


    private fun hasAtLeastOneEmptyField(): Boolean {
        val hasName = signUp.value.name.isNotEmpty()
        val hasEmail = signUp.value.email.isNotEmpty()
        val hasPassword = signUp.value.password.isNotEmpty()
        val hasSelectedAvatar = signUp.value.avatar.isNotEmpty()
        if (!hasName) {
            errorState.usenameError.value = missing_field
        }
        if (!hasEmail) {
            errorState.emailError.value = missing_field
        }
        if (!hasPassword) {
            errorState.passwordError.value = missing_field
        }
        if (!hasSelectedAvatar) {
            errorState.avatarError.value = missing_field
        }
        return !hasEmail || !hasName || !hasPassword || !hasSelectedAvatar
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
                errorState.emailError.value = email_not_unique
            }
            if (error == AuthError.NameAlreadyTaken.label) {
                errorState.usenameError.value = userName_not_unique
            }
        }
        serverErrors?.map { content -> setError(content) } as List<AuthError>
    }
}

