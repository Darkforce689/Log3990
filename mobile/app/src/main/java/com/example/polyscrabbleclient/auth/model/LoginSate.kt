package com.example.polyscrabbleclient.auth.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class LoginSate(
    var email: String = "",
    var password: String = "",
    var emailError: MutableState<String> = mutableStateOf(""),
    var passwordError: MutableState<String> = mutableStateOf("")
)
