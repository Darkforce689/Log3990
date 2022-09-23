package com.example.polyscrabbleclient

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.polyscrabbleclient.auth.components.SignInScreen
import com.example.polyscrabbleclient.auth.components.SignUpScreen
import com.example.polyscrabbleclient.auth.viewmodel.AuthenticationViewModel
import com.example.polyscrabbleclient.message.components.ChatRoomScreen
import com.example.polyscrabbleclient.message.model.User
import com.example.polyscrabbleclient.message.viewModel.ChatBoxViewModel
enum class navPage (val label : String){
    Prototype("prototype"),
    Room("messageList"),
    Login("loginPage"),
    SignUp("signUpPage"),
    Start("startPage")


}
@Composable
fun NavGraph(chatBoxViewModel: ChatBoxViewModel, loginViewModel: AuthenticationViewModel) {
    val navController = rememberNavController()
    NavHost(navController, startDestination = navPage.Start.label) {
        composable(navPage.Start.label) {
            // TODO: to be remove for first page of App
            Column {
                Prototype(navController)
                Login(navController)
            }
        }
        composable(navPage.Room.label) {
            // To place in other function somewhere else
            chatBoxViewModel.joinRoom(navPage.Prototype.label, User)
            ChatRoomScreen(navController, chatBoxViewModel)
        }
        composable(navPage.Login.label){
            SignInScreen(navController, loginViewModel)
        }
        composable(navPage.SignUp.label){
            SignUpScreen(navController, loginViewModel)
        }
    }

}
