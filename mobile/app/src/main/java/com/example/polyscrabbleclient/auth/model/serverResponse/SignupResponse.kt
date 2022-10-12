package com.example.polyscrabbleclient.auth.model.serverResponse

import com.example.polyscrabbleclient.message.model.User

data class SignUpRes(val errors: List<String>?, val user: User?)

