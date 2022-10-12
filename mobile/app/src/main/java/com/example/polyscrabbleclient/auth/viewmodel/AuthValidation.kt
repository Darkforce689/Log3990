package com.example.polyscrabbleclient.auth.viewmodel

import android.util.Patterns
import com.example.polyscrabbleclient.utils.constants.MAX_NAME_LENGTH
import com.example.polyscrabbleclient.utils.constants.MAX_PASSWORD_LENGTH
import com.example.polyscrabbleclient.utils.constants.MIN_NAME_LENGTH
import com.example.polyscrabbleclient.utils.constants.MIN_PASSWORD_LENGTH

object AuthValidation {
    fun isValidEmail(target: CharSequence): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    fun isValidPassword(password: String): Boolean {
        return password.length in MIN_PASSWORD_LENGTH..MAX_PASSWORD_LENGTH
    }

    fun isUsernameValid(name: String): Boolean {
        return name.length in MIN_NAME_LENGTH..MAX_NAME_LENGTH
    }
}
