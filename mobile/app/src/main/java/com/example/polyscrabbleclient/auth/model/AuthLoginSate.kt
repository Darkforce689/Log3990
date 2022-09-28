package com.example.polyscrabbleclient.auth.model

data class AuthSignUpSate (
    var name:String = "",
    var email: String = "",
    var password: String = ""
)

data class AuthLoginSate(
    var email: String = "",
    var password: String = ""
)
