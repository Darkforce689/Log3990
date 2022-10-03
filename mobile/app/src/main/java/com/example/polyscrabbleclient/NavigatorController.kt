package com.example.polyscrabbleclient

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.polyscrabbleclient.auth.components.LogInScreen
import com.example.polyscrabbleclient.auth.components.SignUpScreen
import com.example.polyscrabbleclient.auth.viewmodel.AuthenticationViewModel
import com.example.polyscrabbleclient.auth.viewmodel.LoginRes
import com.example.polyscrabbleclient.auth.viewmodel.SignUpViewModel
import com.example.polyscrabbleclient.auth.viewmodel.SignUpRes
import com.example.polyscrabbleclient.game.view.LetterRackView
import com.example.polyscrabbleclient.game.viewmodels.LetterRackViewModel
import com.example.polyscrabbleclient.message.components.ChatRoomScreen
import com.example.polyscrabbleclient.message.model.User
import com.example.polyscrabbleclient.message.viewModel.ChatBoxViewModel
import com.example.polyscrabbleclient.utils.httprequests.ScrabbleHttpClient
import kotlinx.coroutines.*
import java.lang.Exception
import java.net.URL

enum class NavPage (val label : String){
    Start("startPage"),
    Login("loginPage"),
    SignUp("signUpPage"),
    MainPage("mainPage"),
    GamePage("gamePage"),
    Prototype("prototype"),
    Room("messageList")
}
@OptIn(DelicateCoroutinesApi::class)
@Composable
fun NavGraph(chatBoxViewModel: ChatBoxViewModel, loginViewModel: AuthenticationViewModel, startPage : NavPage, startViewModel: StartViewModel) {
    val navController = rememberNavController()

    NavHost(navController, startDestination = startPage.label) {

        composable(NavPage.MainPage.label) {
            StartView(navController, startViewModel)
        }
        composable(NavPage.Room.label) {
            // Todo : place in other function somewhere else with room id
                chatBoxViewModel.joinRoom(NavPage.Prototype.label)
                ChatRoomScreen(navController, chatBoxViewModel)

        }
        composable(NavPage.Login.label) {
            LogInScreen(navController, loginViewModel)
        }
        composable(NavPage.SignUp.label) {
            SignUpScreen(navController, SignUpViewModel())
        }
        composable(NavPage.GamePage.label) {
            LetterRackView(navController)
        }
    }
}



