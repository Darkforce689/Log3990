package com.example.polyscrabbleclient.account.model

import com.example.polyscrabbleclient.auth.model.AuthError

enum class AccountPage {
    Statistics,
    Profil,
    Games;

    companion object {
        fun find(value: String): AuthError? = AuthError.values().find { it.label == value }
    }

}
