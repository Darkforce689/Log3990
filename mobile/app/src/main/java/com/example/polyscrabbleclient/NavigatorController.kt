package com.example.polyscrabbleclient

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.polyscrabbleclient.auth.components.LogInScreen
import com.example.polyscrabbleclient.auth.components.SignUpScreen
import com.example.polyscrabbleclient.auth.viewmodel.AuthenticationViewModel
import com.example.polyscrabbleclient.auth.viewmodel.SignUpViewModel
import com.example.polyscrabbleclient.game.view.GameScreen
import com.example.polyscrabbleclient.game.viewmodels.GameViewModel
import com.example.polyscrabbleclient.message.components.ChatRoomScreen
import com.example.polyscrabbleclient.message.viewModel.ChatBoxViewModel
import kotlinx.coroutines.*

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
            GameScreen(navController)
        }
    }
}



