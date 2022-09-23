package com.example.polyscrabbleclient.auth.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.polyscrabbleclient.auth.model.AuthMode
import com.example.polyscrabbleclient.auth.model.AuthSignUpSate
import com.example.polyscrabbleclient.auth.viewmodel.AuthenticationViewModel
import com.example.polyscrabbleclient.navPage
import com.example.polyscrabbleclient.ui.theme.create_account
import com.example.polyscrabbleclient.ui.theme.signUp
import com.example.polyscrabbleclient.ui.theme.userName_string

@Composable
fun SignUpScreen(navController: NavController, viewModel: AuthenticationViewModel) {
    Column() {
        Button(modifier = Modifier.padding(20.dp),
            onClick = {
                navController.navigate("startPage") {
                    popUpTo("loginPage") {
                        inclusive = true
                    }
                }
            })
        {
            Text("Page d'accueil")
        }
        SignUpContent(
            modifier = Modifier.fillMaxWidth(),
            authState = viewModel.signUpSate.collectAsState().value,
            handleEvent = viewModel::handleEvent,
            onLogin = { navController.navigate(navPage.Login.label) {
                popUpTo(navPage.SignUp.label) {
                    inclusive = true
                }
            }}

        )
    }
}

@Composable
fun SignUpContent(
    modifier: Modifier=Modifier,
    authState: AuthSignUpSate,
    handleEvent: (event: AuthenticationViewModel.AuthEvent)-> Unit,
    onLogin: () -> Unit
){
    SignUpForm(email = authState.email,
             password = authState.password,
             username = authState.username,
             authMode = authState.mode,
             onEmailChanged = { email ->
                 handleEvent(
                     AuthenticationViewModel.AuthEvent.EmailChanged(email)
                 )},
             onPasswordChanged = { password ->
                 handleEvent(
                     AuthenticationViewModel.AuthEvent.PasswordChanged(password)
                 )},
            onAuthenticate = {
                handleEvent(AuthenticationViewModel.AuthEvent.Authenticate)
            },
            onUsernameChanged = {username ->
                handleEvent(
                    AuthenticationViewModel.AuthEvent.UsernameChanged(username)
                )},
            onLogin = {/*TODO*/}

    )

}
//@Preview(showBackground = true)
@Composable
fun SignUpForm(modifier: Modifier = Modifier,
                email: String?,
                password: String,
                username: String?,
                authMode: AuthMode = AuthMode.SIGN_UP,
                onEmailChanged: (email: String) -> Unit,
                onPasswordChanged: (password: String) -> Unit,
                onUsernameChanged:(username: String) -> Unit,
                onAuthenticate: () -> Unit,
                onLogin : () -> Unit
) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(10.dp), contentAlignment = Alignment.Center) {
        Card {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(create_account)
                Spacer(modifier = Modifier.height(20.dp))
                Column {
                    UserNameInput(name = username, onUsernameChanged = onUsernameChanged)
                    Spacer(modifier = Modifier.height(20.dp))
                    EmailInput(email = email, onEmailChanged = onEmailChanged)
                    Spacer(modifier = Modifier.height(20.dp))
                    PasswordInput(password = password, onPasswordChanged = onPasswordChanged)
                }
                Spacer(modifier = Modifier.height(20.dp))
                Button(onClick = {/*Todo*/ onLogin} ) {
                    Text(signUp)
                }

            }
        }
    }

}

@Composable
fun UserNameInput(
    modifier: Modifier = Modifier.fillMaxWidth(),
    name : String?,
    onUsernameChanged: (username: String) -> Unit
){
    TextField(value = name?: "", {},
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send, keyboardType = KeyboardType.Email),
        label = {Text(userName_string)}, singleLine = true, leadingIcon = { Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = null)}
    )
}



























