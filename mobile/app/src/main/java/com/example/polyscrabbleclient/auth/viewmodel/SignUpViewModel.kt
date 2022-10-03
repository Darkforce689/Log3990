package com.example.polyscrabbleclient.auth.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.polyscrabbleclient.BuildConfig
import com.example.polyscrabbleclient.auth.model.AuthSignUpSate
import com.example.polyscrabbleclient.message.model.User
import com.example.polyscrabbleclient.utils.httprequests.ScrabbleHttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

data class SignUpRes(val errors: List<String>?, val user: User?)


class SignUpViewModel : ViewModel() {
        val signUp = MutableStateFlow(AuthSignUpSate())
        val isCreated = MutableStateFlow(false)
        var isInProcess = MutableStateFlow(false)
        val errors : MutableLiveData<List<AuthServerError>> by lazy { MutableLiveData(listOf()) }


        sealed class AuthEvent {
            class EmailChanged(val email: String) : AuthEvent()
            class PasswordChanged(val password: String) : AuthEvent()
            class UsernameChanged(val username: String) : AuthEvent()
            object CreateAccount : AuthEvent()
        }

        fun handleSignUpEvent(authEvent: AuthEvent) {
            when (authEvent) {
                is AuthEvent.EmailChanged ->{
                    updateEmail(authEvent.email)
                }
                is AuthEvent.PasswordChanged -> {
                    updatePassword(authEvent.password)
                }
                is AuthEvent.UsernameChanged ->{
                    updateUsername(authEvent.username)
                }
                is AuthEvent.CreateAccount -> {
                    createAccountRequest()
                }
            }
        }

        fun reset() {
            isCreated.value = false
            isInProcess.value = false
            errors.value = null
            signUp.value.name =""
            signUp.value.email=""
            signUp.value.password=""
        }

        private fun updateEmail(email:String) {
            if(email.isBlank()) {
                signUp.value = signUp.value.copy(email = email)
                return
            }
            if(!email[email.length-1].isWhitespace()) {
                signUp.value = signUp.value.copy(email = email)
            }
        }

        private fun updatePassword(password: String) {
            signUp.value = signUp.value.copy(password = password)
        }

        private fun updateUsername(username: String) {
            if(username.isBlank()) {
                signUp.value = signUp.value.copy(name = username)
                return
            }
            if(!username[username.length-1].isWhitespace()) {
                signUp.value = signUp.value.copy(name = username)
            }
        }

        private fun createAccountRequest() {
            viewModelScope.launch {
                withContext(Dispatchers.Default){
                    val resp = ScrabbleHttpClient.post(
                        URL(BuildConfig.COMMUNICATION_URL + "/auth/register"),
                        signUp.value,
                        SignUpRes::class.java
                    )
                    if(resp != null) {
                        if(resp.user != null) {
                            isCreated.value = true
                        } else {
                            addServerErrors(resp.errors)
                            isInProcess.value = false
                        }
                    }
                }
            }
        }

        private fun addServerErrors(serverErrors : List<String>?) {
            val results = serverErrors?.map{content->AuthServerError.find(content)}
            errors.postValue(results as List<AuthServerError>?)
            println(errors.value)
        }
}

