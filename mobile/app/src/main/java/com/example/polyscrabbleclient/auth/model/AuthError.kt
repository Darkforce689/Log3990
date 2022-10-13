package com.example.polyscrabbleclient.auth.model

enum class AuthError(val label: String) {
    EmailNotFound("EMAIL_NOT_FOUND"),
    InvalidPassword("INVALID_PASSWORD"),
    NameAlreadyTaken("NAME_ALREADY_TAKEN"),
    EmailAlreadyTaken("EMAIL_ALREADY_TAKEN"),
    AlreadyAuth("ALREADY_AUTH");

    companion object {
        fun find(value: String): AuthError? = values().find { it.label == value }
    }
}
