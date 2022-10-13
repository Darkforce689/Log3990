package com.example.polyscrabbleclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.polyscrabbleclient.auth.model.serverResponse.LoginRes
import com.example.polyscrabbleclient.ui.theme.PolyScrabbleClientTheme
import com.example.polyscrabbleclient.auth.viewmodel.AuthenticationViewModel
import com.example.polyscrabbleclient.message.viewModel.ChatBoxViewModel
import com.example.polyscrabbleclient.ui.theme.NoRippleTheme
import com.example.polyscrabbleclient.utils.httprequests.ScrabbleHttpClient
import java.net.*

data class Credentials(val email: String?, val password: String?) {
    constructor() : this(null,null)
}

data class Score(val _id: String, val score: Int, val name: String)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ScrabbleHttpClient.setCookieManager(this)
        var startPage: NavPage = NavPage.Login
        if (ScrabbleHttpClient.getAuthCookie() != null) {
            try {
                val thread = Thread {
                    val response = ScrabbleHttpClient.get(
                        URL(BuildConfig.COMMUNICATION_URL + "/auth/validatesession")
                    )
                    if (response != null) {
                        startPage = NavPage.MainPage
                    } else {
                        ScrabbleHttpClient.clearCookies()
                    }
                }
                thread.start()
                thread.join()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        setContent {
            val chatView: ChatBoxViewModel = viewModel()
            val loginViewModel: AuthenticationViewModel = viewModel()
            val startViewModel: StartViewModel = viewModel()
            PolyScrabbleClientTheme() {
                CompositionLocalProvider(LocalRippleTheme provides NoRippleTheme) {
                    NavGraph(chatView, loginViewModel, startPage, startViewModel)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
