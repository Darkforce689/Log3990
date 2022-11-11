package com.example.polyscrabbleclient

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
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
import com.example.polyscrabbleclient.lobby.view.NewGameScreen
import com.example.polyscrabbleclient.lobby.viewmodels.CreateGameViewModel
import com.example.polyscrabbleclient.message.components.ChatRoomScreen
import com.example.polyscrabbleclient.message.viewModel.ChatBoxViewModel
import com.example.polyscrabbleclient.page.headerbar.view.HeaderBar
import com.example.polyscrabbleclient.page.headerbar.viewmodels.ThemeSelectorViewModel
import com.example.polyscrabbleclient.utils.PageSurface

enum class NavPage(val label: String) {
    RegistrationRoute("registration"),
    Login("loginPage"),
    SignUp("signUpPage"),
    MainPage("mainPage"),
    GamePage("gamePage"),
    JoinGamePage("joinGamePage"),
    Prototype("prototype"),
    Room("messageList"),
    Account("account"),
    NewGameRoute("newGameRoute"),
    NewGame("newGame"),
}

@Composable
fun NavGraph(startPage: NavPage, themeSelectorViewModel: ThemeSelectorViewModel) {
    val navController = rememberNavController()

    NavHost(navController, startDestination = startPage.label) {

        composable(NavPage.MainPage.label) {
            PageWithHeader(navController, themeSelectorViewModel) {
                StartView(navController, StartViewModel())
            }
        }
        composable(NavPage.Room.label) {
            PageSurface {
                ChatRoomScreen(navController, ChatBoxViewModel())
            }
        }
        loginGraph(navController)
        composable(NavPage.GamePage.label) {
            PageSurface {
                GameScreen(navController)
            }
        }
        newGame(navController, themeSelectorViewModel)
        composable(NavPage.Account.label) {
            PageSurface {
                AccountView(AccountViewmodel(), navController)
            }
        }
    }
}

@Composable
private fun PageWithHeader(
    navController: NavController,
    themeSelectorViewModel: ThemeSelectorViewModel,
    pageContent: @Composable () -> Unit
) {
    PageSurface {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            HeaderBar(navController, themeSelectorViewModel)
            pageContent()
        }
    }
}

fun NavGraphBuilder.newGame(navController: NavController, themeSelectorViewModel: ThemeSelectorViewModel) {
    navigation(startDestination = NavPage.NewGame.label, route = NavPage.NewGameRoute.label) {
        composable(NavPage.NewGame.label) {
            PageWithHeader(navController, themeSelectorViewModel) {
                NewGameScreen(navController, CreateGameViewModel())
            }
        }
    }
}

fun NavGraphBuilder.loginGraph(navController: NavController) {
    navigation(startDestination = NavPage.Login.label, route = NavPage.RegistrationRoute.label) {
        composable(NavPage.Login.label) {
            PageSurface {
                LogInScreen(navController, AuthenticationViewModel())
            }
        }
        composable(NavPage.SignUp.label) {
            PageSurface {
                SignUpScreen(navController, SignUpViewModel())
            }
        }
    }
}
