package com.example.polyscrabbleclient

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.polyscrabbleclient.account.components.AccountView
import com.example.polyscrabbleclient.account.viewmodel.AccountViewmodel
import com.example.polyscrabbleclient.auth.components.LogInScreen
import com.example.polyscrabbleclient.auth.components.SignUpScreen
import com.example.polyscrabbleclient.auth.viewmodel.AuthenticationViewModel
import com.example.polyscrabbleclient.auth.viewmodel.SignUpViewModel
import com.example.polyscrabbleclient.game.view.GameScreen
import com.example.polyscrabbleclient.game.viewmodels.GameViewModel
import com.example.polyscrabbleclient.lobby.view.LobbyScreen
import com.example.polyscrabbleclient.message.components.ChatRoomScreen
import com.example.polyscrabbleclient.message.viewModel.ChatBoxViewModel

enum class NavPage(val label: String) {
    Registration("registration"),
    Login("loginPage"),
    SignUp("signUpPage"),
    MainPage("mainPage"),
    GamePage("gamePage"),
    LobbyPage("lobbyPage"),
    Prototype("prototype"),
    Room("messageList"),
    Profil("profil"),
    Account("account")
}

@Composable
fun NavGraph(startPage: NavPage) {
    val navController = rememberNavController()

    NavHost(navController, startDestination = startPage.label) {

        composable(NavPage.MainPage.label) {
            StartView(navController, StartViewModel())
        }
        composable(NavPage.Room.label) {
            val chatBoxViewModel = ChatBoxViewModel()
            // Todo : place in other function somewhere else with room id
            chatBoxViewModel.joinRoom(NavPage.Prototype.label)
            ChatRoomScreen(navController, chatBoxViewModel)
        }
        loginGraph(navController)
        composable(NavPage.GamePage.label) {
            GameScreen(navController)
        }
        account(navController)
    }
}

fun NavGraphBuilder.loginGraph(navController: NavController) {
    navigation(startDestination = NavPage.Login.label, route = NavPage.Registration.label) {
        composable(NavPage.Login.label) {
            LogInScreen(navController, AuthenticationViewModel())
        }
        composable(NavPage.SignUp.label) {
            SignUpScreen(navController, SignUpViewModel())
        }
    }
}

fun NavGraphBuilder.account(navController: NavController) {
    navigation(startDestination = NavPage.Profil.label, route = NavPage.Account.label) {
        composable(NavPage.Profil.label) {
            AccountView(AccountViewmodel(), navController)
        }
        composable(NavPage.LobbyPage.label) {
            LobbyScreen(navController)
        }
    }
}




