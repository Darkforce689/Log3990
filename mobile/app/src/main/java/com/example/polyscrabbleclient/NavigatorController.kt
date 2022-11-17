package com.example.polyscrabbleclient

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Message
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.example.polyscrabbleclient.lobby.view.NewGameScreen
import com.example.polyscrabbleclient.lobby.viewmodels.CreateGameViewModel
import com.example.polyscrabbleclient.message.components.ChatBox
import com.example.polyscrabbleclient.message.components.ChatRoomScreen
import com.example.polyscrabbleclient.message.viewModel.ChatBoxViewModel
import com.example.polyscrabbleclient.page.headerbar.view.HeaderBar
import com.example.polyscrabbleclient.page.headerbar.viewmodels.ThemeSelectorViewModel
import com.example.polyscrabbleclient.utils.Background
import com.example.polyscrabbleclient.utils.PageSurface
import com.example.polyscrabbleclient.utils.PhysicalButtons
import kotlinx.coroutines.launch

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
            PageWithHeaderAndChat(navController, themeSelectorViewModel, Background.Home) {
                StartView(navController, StartViewModel())
            }
        }

        loginGraph(navController)
        composable(NavPage.GamePage.label) {
            PageWithChat {
                PageSurface(Background.Game) {
                    GameScreen(navController)
                }
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
fun PageWithHeaderAndChat(
    navController: NavController,
    themeSelectorViewModel: ThemeSelectorViewModel,
    background: Background? = Background.Page,
    pageContent: @Composable () -> Unit
) {
    PageWithChat(background) {
        HeaderBar(navController, themeSelectorViewModel)
        pageContent()
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PageWithChat(
    background: Background? = Background.Page,
    pageContent: @Composable () -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var keyboard: SoftwareKeyboardController? = LocalSoftwareKeyboardController.current
    var focus = LocalFocusManager.current
    val viewModel: ChatPageViewModel = viewModel()

    LaunchedEffect(drawerState.currentValue) {
        if (drawerState.isClosed) {
            keyboard?.hide()
            focus.clearFocus()
            viewModel.onCloseChat()
        } else {
            PhysicalButtons.pushBackPress {
                scope.launch {
                    drawerState.close()
                    viewModel.onBackPressed()
                }
            }
        }
    }

    CompositionLocalProvider(LocalLayoutDirection.provides(LayoutDirection.Rtl)) {
        ModalDrawer(
            drawerState = drawerState,
            drawerContent = {
                CompositionLocalProvider(
                    LocalLayoutDirection.provides(LayoutDirection.Ltr)
                ) {
                    ChatBox(ChatBoxViewModel())
                }
            },
        ) {
            CompositionLocalProvider(
                LocalLayoutDirection.provides(LayoutDirection.Ltr)
            ) {
                Scaffold(
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                        ) {
                            Icon(Icons.Default.Message, null)
                        }
                    }
                ) {
                    PageSurface(background) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            pageContent()
                        }
                    }
                }
            }
        }
    }

}

fun NavGraphBuilder.newGame(
    navController: NavController,
    themeSelectorViewModel: ThemeSelectorViewModel
) {
    navigation(startDestination = NavPage.NewGame.label, route = NavPage.NewGameRoute.label) {
        composable(NavPage.NewGame.label) {
            PageWithHeaderAndChat(navController, themeSelectorViewModel) {
                NewGameScreen(navController, CreateGameViewModel())
            }
        }
    }
}

fun NavGraphBuilder.loginGraph(navController: NavController) {
    navigation(startDestination = NavPage.Login.label, route = NavPage.RegistrationRoute.label) {
        composable(NavPage.Login.label) {
            PageSurface(Background.Home) {
                LogInScreen(navController, AuthenticationViewModel())
            }
        }
        composable(NavPage.SignUp.label) {
            PageSurface(Background.Home) {
                SignUpScreen(navController, SignUpViewModel())
            }
        }
    }
}
