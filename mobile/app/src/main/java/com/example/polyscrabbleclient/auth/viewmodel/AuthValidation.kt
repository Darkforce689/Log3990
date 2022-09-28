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

    fun isValidPassword(password: String) : Boolean {
        return password.isNotBlank() && password.length <= MAX_PASSWORD_LENGTH && password.length > MIN_PASSWORD_LENGTH
    }

    fun isValidUsername(name: String) : Boolean {
        return name.isNotBlank() && name.length <= MAX_NAME_LENGTH && name.length > MIN_NAME_LENGTH
    }

    fun hasAtLeastOneEmptyField(email:String, name : String?, password: String): Boolean {
        var hasName = false
        if (name != null) {
            hasName = name.isEmpty()
        }

        return (email.isEmpty() || hasName || password.isEmpty())
    }

    private fun allEmptyField(email:String, name : String?, password: String): Boolean {
        var hasName = true
        if (name != null) {
            hasName = name.isEmpty()
        }
        return email.isEmpty() && hasName  && password.isEmpty()
    }

}
