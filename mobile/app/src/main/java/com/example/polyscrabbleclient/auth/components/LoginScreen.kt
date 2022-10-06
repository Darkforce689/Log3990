package com.example.polyscrabbleclient.auth.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
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
import com.example.polyscrabbleclient.auth.model.AuthLoginSate
import com.example.polyscrabbleclient.auth.viewmodel.AuthenticationViewModel
import com.example.polyscrabbleclient.NavPage
import com.example.polyscrabbleclient.auth.viewmodel.AuthServerError
import com.example.polyscrabbleclient.auth.viewmodel.AuthValidation
import com.example.polyscrabbleclient.ui.theme.*

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LogInScreen(navController: NavController, viewModel: AuthenticationViewModel) {
    val isAuthenticate = viewModel.isAuthenticate.collectAsState().value
    val keyboardController = LocalSoftwareKeyboardController.current
    val inputFocusRequester = LocalFocusManager.current

    Box(Modifier.clickable {keyboardController?.hide(); inputFocusRequester.clearFocus() }) {
        AuthContent(
            modifier = Modifier.fillMaxWidth(),
            authState = viewModel.logInState.collectAsState().value,
            errors = viewModel.errors.observeAsState().value,
            handleEvent = viewModel::handleLoginEvent,
            isInProcess = viewModel.isInProcess.collectAsState().value,
            onSignUp = {
                viewModel.reset()
                navController.navigate(NavPage.SignUp.label) {
                    popUpTo(NavPage.Login.label) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        )
    }
    if (isAuthenticate) {
        viewModel.reset()
        navController.navigate(NavPage.MainPage.label) {
            launchSingleTop = true
        }
    }
}

@Composable
fun AuthContent(
    modifier: Modifier,
    authState: AuthLoginSate,
    errors: List<AuthServerError>?,
    handleEvent: (event: AuthenticationViewModel.AuthEvent)-> Unit,
    onSignUp: () -> Unit,
    isInProcess: Boolean
) {
    AuthForm(
        email = authState.email,
        password = authState.password,
        serverError = errors,
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
        onSignUp = onSignUp,
        isInProcess = isInProcess
    )
}

//@Preview(showBackground = true)
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AuthForm(
    modifier: Modifier = Modifier,
    email: String,
    password: String,
    serverError: List<AuthServerError>?,
    onEmailChanged: (email: String) -> Unit,
    onPasswordChanged: (password: String) -> Unit,
    onAuthenticate: () -> Unit,
    onSignUp: ()-> Unit,
    isInProcess : Boolean
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val missingEmailError = remember { mutableStateOf(false) }
    val missingPasswordError = remember { mutableStateOf(false) }

    fun hasEmptySpace(): Boolean {
        return AuthValidation.hasAtLeastOneEmptyField(
            email = email,
            password = password,
            name = null
        )
    }

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
                Column() {
                    EmailInput(
                        email = email,
                        onEmailChanged = { onEmailChanged(it); missingEmailError.value = false },
                        serverError = serverError?.find { error -> error.label == AuthServerError.EmailNotFound.label },
                        missingFieldError = missingEmailError.value
                    )
                    PasswordInput(
                        password = password,
                        onPasswordChanged = { onPasswordChanged(it); missingPasswordError.value = false },
                        serverError = serverError?.find { error -> error.label == AuthServerError.InvalidPassword.label || error.label == AuthServerError.AlreadyAuth.label },
                        missingFieldError = missingPasswordError.value,
                        onCreation = false
                    )
                }
                Button(
                    onClick = {
                        if (!hasEmptySpace()) {
                            onAuthenticate(); keyboardController?.hide()
                        } else {
                            if (email.isEmpty()) {
                                missingEmailError.value = true
                            }
                            if (password.isEmpty()) {
                                missingPasswordError.value = true
                            }
                        }
                    },
                    enabled = !isInProcess

                ) {
                    Text(connect)
                }
                Spacer(modifier = Modifier.height(20.dp))
                ClickableText(
                    text = AnnotatedString(
                        no_Account,
                        SpanStyle(color = MaterialTheme.colors.secondaryVariant, textDecoration = TextDecoration.Underline)
                    ), onClick = { keyboardController?.hide(); onSignUp() })
            }
        }
    }
}
