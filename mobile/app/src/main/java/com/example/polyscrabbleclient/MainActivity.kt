package com.example.polyscrabbleclient

import android.os.Bundle
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.polyscrabbleclient.auth.viewmodel.AuthenticationViewModel
import com.example.polyscrabbleclient.auth.viewmodel.LoginRes
import com.example.polyscrabbleclient.message.SocketHandler
import com.example.polyscrabbleclient.message.viewModel.ChatBoxViewModel
import com.example.polyscrabbleclient.ui.theme.PolyScrabbleClientTheme
import com.example.polyscrabbleclient.utils.httprequests.ScrabbleHttpClient
import java.net.*

data class Credentials(val email: String?, val password: String?) {
    constructor() : this(null,null)
}

data class Score(val _id: String, val score: Int, val name: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var startPage: NavPage = NavPage.Login
        if (ScrabbleHttpClient.getAuthCookie() == null) {
            println(ScrabbleHttpClient.getAuthCookie())
            // TODO: Verify when persistent cookies is made
        } else {
            try {
            val thread = Thread {
                    val response = ScrabbleHttpClient.post(
                        URL(BuildConfig.COMMUNICATION_URL + "/auth/login"),
                        Credentials("", ""),
                        LoginRes::class.java
                    )
                    if (response != null) {
                        if (response.errors == null) {
                            startPage = NavPage.MainPage
                        }
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
                NavGraph(chatView, loginViewModel, startPage, startViewModel)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
