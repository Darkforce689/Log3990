package com.example.polyscrabbleclient.auth.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.polyscrabbleclient.NavPage
import com.example.polyscrabbleclient.auth.model.LoginSate
import com.example.polyscrabbleclient.auth.viewmodel.AuthenticationViewModel
import com.example.polyscrabbleclient.ui.theme.connect
import com.example.polyscrabbleclient.ui.theme.connection
import com.example.polyscrabbleclient.ui.theme.no_Account

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LogInScreen(navController: NavController, viewModel: AuthenticationViewModel) {
    val isAuthenticate = viewModel.isAuthenticate.value
    val keyboardController = LocalSoftwareKeyboardController.current
    val inputFocusRequester = LocalFocusManager.current

    if (isAuthenticate) {
        navController.navigate(NavPage.MainPage.label) {
            launchSingleTop = true
        }
        viewModel.reset()
    }

    Box(Modifier.clickable { keyboardController?.hide(); inputFocusRequester.clearFocus() }) {
        AuthContent(
            modifier = Modifier.fillMaxWidth(),
            authState = viewModel.logInState.value,
            handleEvent = viewModel::handleLoginEvent,
            isInProcess = viewModel.isInProcess.value,
            onSignUp = {
                navController.navigate(NavPage.SignUp.label) {
                    popUpTo(NavPage.Login.label) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
                viewModel.reset()
            }
        )
    }
}

@Composable
fun AuthContent(
    modifier: Modifier,
    authState: LoginSate,
    handleEvent: (event: AuthenticationViewModel.AuthEvent) -> Unit,
    onSignUp: () -> Unit,
    isInProcess: Boolean
) {
    AuthForm(
        authState = authState,
        onEmailChanged = { email ->
            handleEvent(
                AuthenticationViewModel.AuthEvent.EmailChanged(email)
            )
        },
        onPasswordChanged = { password ->
            handleEvent(
                AuthenticationViewModel.AuthEvent.PasswordChanged(password)
            )
        },
        onAuthenticate = {
            handleEvent(AuthenticationViewModel.AuthEvent.Authenticate)
        },
        validateEmail = { email ->
            handleEvent(AuthenticationViewModel.AuthEvent.ValidateEmail(email))
        },
        onSignUp = onSignUp,
        isInProcess = isInProcess
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AuthForm(
    modifier: Modifier = Modifier,
    authState: LoginSate,
    onEmailChanged: (email: String) -> Unit,
    onPasswordChanged: (password: String) -> Unit,
    validateEmail: (email: String) -> Unit,
    onAuthenticate: () -> Unit,
    onSignUp: () -> Unit,
    isInProcess: Boolean
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.size(350.dp),
            backgroundColor = MaterialTheme.colors.background
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = connection, fontSize = 25.sp)
                Spacer(modifier = Modifier.height(15.dp))
                Column {
                    EmailInput(
                        email = authState.email,
                        onEmailChanged = { onEmailChanged(it) },
                        error = authState.emailError.value,
                        validateEmail = validateEmail
                    )
                    PasswordInput(
                        password = authState.password,
                        onPasswordChanged = {
                            onPasswordChanged(it)
                        },
                        error = authState.passwordError.value,
                        validatePassword = {}, // No form error possible
                    )
                }
                Button(
                    onClick = { onAuthenticate(); keyboardController?.hide() },
                    enabled = !isInProcess
                ) {
                    Text(connect)
                }
                Spacer(modifier = Modifier.height(20.dp))
                ClickableText(
                    text = AnnotatedString(
                        no_Account,
                        SpanStyle(
                            color = MaterialTheme.colors.secondaryVariant,
                            textDecoration = TextDecoration.Underline
                        )
                    ), onClick = { keyboardController?.hide(); onSignUp() })
            }
        }
    }
}
