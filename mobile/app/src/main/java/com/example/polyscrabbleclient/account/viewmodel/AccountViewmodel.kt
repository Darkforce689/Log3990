package com.example.polyscrabbleclient.account.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.polyscrabbleclient.BuildConfig
import com.example.polyscrabbleclient.account.model.UpdateResponse
import com.example.polyscrabbleclient.auth.model.AuthError
import com.example.polyscrabbleclient.auth.viewmodel.AuthValidation
import com.example.polyscrabbleclient.ui.theme.invalid_username_creation
import com.example.polyscrabbleclient.ui.theme.userName_not_unique
import com.example.polyscrabbleclient.user.User
import com.example.polyscrabbleclient.utils.constants.MAX_NAME_LENGTH
import com.example.polyscrabbleclient.utils.constants.MIN_NAME_LENGTH
import com.example.polyscrabbleclient.utils.httprequests.ScrabbleHttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

data class UserUpdate(
    var name: String = "",
    var avatar: String = ""
)

class AccountViewmodel : ViewModel() {
    val userName = mutableStateOf(User.name)
    val avatar = mutableStateOf(User.avatar)
    val userInfoField = mutableStateOf(UserUpdate(User.name, User.avatar))
    var usernameError: MutableState<String?> = mutableStateOf(null)

    fun updateUsername(username: String) {
        usernameError.value = null
        if (username.isBlank()) {
            userInfoField.value = userInfoField.value.copy(name = username)
            return
        }
        if (!username[username.length - 1].isWhitespace()) {
            userInfoField.value = userInfoField.value.copy(name = username)
        }
    }

    fun validateUsername(username: String) {
        if (!AuthValidation.isUsernameValid(username)) {
            usernameError.value =
                invalid_username_creation(MIN_NAME_LENGTH, MAX_NAME_LENGTH)
        }
    }

    fun updateAvatar(avatarName: String) {
        userInfoField.value = userInfoField.value.copy(avatar = avatarName)
    }

    private fun hasError(): Boolean {
        return usernameError.value != null
    }

    private fun getFieldsToUpdate(fields: UserUpdate): UserUpdate {
        val newInfo = UserUpdate()
        if (fields.name != User.name) {
            newInfo.name = fields.name
        }
        if (fields.avatar != User.avatar) {
            newInfo.avatar = fields.avatar
        }
        return newInfo
    }

    fun updateInfoRequest() {
        if (hasError()) {
            return
        }
        val newInfo = getFieldsToUpdate(userInfoField.value)
        if (newInfo.name.isEmpty() && newInfo.avatar.isEmpty()) {
            return
        }

        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val response = ScrabbleHttpClient.patch(
                    URL(BuildConfig.API_URL + "/account"),
                    newInfo,
                    UpdateResponse::class.java
                ) ?: return@withContext
                if (response.errors == null) {
                    viewModelScope.launch {
                        withContext(Dispatchers.IO) {
                            updateUserInfo()
                        }
                    }
                    return@withContext
                }
                if (response.errors[0] == AuthError.NameAlreadyTaken.label) {
                    usernameError.value = userName_not_unique
                }
            }
        }
    }

    private fun updateUserInfo() {
        val updateThread = User.updateUser()
        updateThread.start()
        updateThread.join()
        userName.value = User.name
        avatar.value = User.avatar
    }
}
