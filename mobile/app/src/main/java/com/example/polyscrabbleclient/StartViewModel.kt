package com.example.polyscrabbleclient

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.polyscrabbleclient.auth.AppSocketHandler
import com.example.polyscrabbleclient.message.ChatSocketHandler
import com.example.polyscrabbleclient.ui.theme.no_definition
import com.example.polyscrabbleclient.user.User
import com.example.polyscrabbleclient.utils.httprequests.ScrabbleHttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.net.URL

data class WordOfTheDayInfo(val word: String = "", val points: Int = 0, val definition: String = no_definition)

class StartViewModel : ViewModel() {
    var wordOfTheDay: MutableStateFlow<WordOfTheDayInfo?> = MutableStateFlow(null)

    fun disconnect(navController: NavController) {
        val thread = Thread {
            ScrabbleHttpClient.get(
                URL(BuildConfig.COMMUNICATION_URL + "/auth/logout"),
                String::class.java
            )
        }
        thread.start()
        thread.join()
        AppSocketHandler.disconnect()
        ChatSocketHandler.closeConnection()
        ScrabbleHttpClient.clearCookies()
        User.resetUserInfo()
        viewModelScope.launch(Dispatchers.Main) {
            navController.navigate(NavPage.Login.label) {
                launchSingleTop = true
            }
        }
    }

    fun getWordOfTheDay(callBack: (info:WordOfTheDayInfo) -> Unit) {
        var response: WordOfTheDayInfo? =null
        val thread = Thread {
            response = ScrabbleHttpClient.get(
                URL(BuildConfig.API_URL + "/home"),
                WordOfTheDayInfo::class.java
            ) ?: return@Thread
        }
        thread.start()
        thread.join()
        response?.let { callBack(it) }
    }
}

