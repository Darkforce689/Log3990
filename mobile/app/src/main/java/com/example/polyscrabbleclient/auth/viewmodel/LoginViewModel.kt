package com.example.polyscrabbleclient.auth.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.polyscrabbleclient.BuildConfig
import com.example.polyscrabbleclient.auth.model.AuthLoginSate
import com.example.polyscrabbleclient.utils.httprequests.ScrabbleHttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

enum class AuthServerError(val label : String) {
    EmailNotFound("EMAIL_NOT_FOUND"),
    InvalidPassword("INVALID_PASSWORD"),
    NameAlreadyTaken("NAME_ALREADY_TAKEN"),
    EmailAlreadyTaken("EMAIL_ALREADY_TAKEN");

    companion object {
        fun find(value : String): AuthServerError? = values().find{it.label == value}
    }
}

data class LoginRes(val errors: List<String>?, val message: String?)

class AuthenticationViewModel : ViewModel() {
    val logInState = MutableStateFlow(AuthLoginSate())
    val isAuthenticate = MutableStateFlow(false)
    var isInProcess = MutableStateFlow(false)
    val errors : MutableLiveData<List<AuthServerError>> = MutableLiveData(null)

    sealed class AuthEvent {
        class EmailChanged(val email: String) : AuthEvent()
        class PasswordChanged(val password: String) : AuthEvent()
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
            is AuthEvent.Authenticate -> {
                authenticateRequest()
            }
        }
    }

    fun reset() {
        isAuthenticate.value = false
        isInProcess.value = false
        errors.value = null
        logInState.value.email = ""
        logInState.value.password=""
    }

    private fun updateEmail(email:String) {
        logInState.value = logInState.value.copy(email = email)
    }

    private fun updatePassword(password: String) {
        logInState.value = logInState.value.copy(password = password)
    }

    private fun authenticateRequest() {
        // TODO: notify user when server is down
        isInProcess.value = true
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                var response: LoginRes?
                try {
                    response = ScrabbleHttpClient.post(
                        URL(BuildConfig.COMMUNICATION_URL+"/auth/login"),
                        logInState.value,
                        LoginRes::class.java
                    )

                    if (response == null) {
                        // add notification here
                        return@withContext
                    }
                    if(response.message != null) {
                        isAuthenticate.value = true
                        return@withContext
                    }
                    errors.postValue(getServerErrors(response.errors))
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

    private fun getServerErrors(serverErrors : List<String>?) :List<AuthServerError> {
        return serverErrors?.map{content->AuthServerError.find(content)} as List<AuthServerError>
    }
}


