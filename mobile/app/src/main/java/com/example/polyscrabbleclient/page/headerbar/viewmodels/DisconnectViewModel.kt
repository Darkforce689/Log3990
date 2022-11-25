package com.example.polyscrabbleclient.page.headerbar.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.polyscrabbleclient.BuildConfig
import com.example.polyscrabbleclient.NavPage
import com.example.polyscrabbleclient.auth.AppSocketHandler
import com.example.polyscrabbleclient.message.ChatSocketHandler
import com.example.polyscrabbleclient.user.User
import com.example.polyscrabbleclient.utils.httprequests.ScrabbleHttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URL


class DisconnectViewModel : ViewModel() {

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
}
