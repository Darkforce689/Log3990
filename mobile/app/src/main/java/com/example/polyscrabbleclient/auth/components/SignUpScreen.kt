package com.example.polyscrabbleclient.auth.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.polyscrabbleclient.NavPage
import com.example.polyscrabbleclient.auth.model.AuthSignUpSate
import com.example.polyscrabbleclient.auth.viewmodel.SignUpViewModel
import com.example.polyscrabbleclient.ui.theme.create_account
import com.example.polyscrabbleclient.ui.theme.signUp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SignUpScreen(navController: NavController, viewModel: SignUpViewModel) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val inputFocusRequester = LocalFocusManager.current
    val isCreated = viewModel.isCreated

    if (isCreated.value) {
        navController.navigate(NavPage.Login.label) {
            popUpTo(NavPage.SignUp.label) {
                inclusive = true
            }
            launchSingleTop = true
        }
        viewModel.reset()
    }

    Box(Modifier.clickable { keyboardController?.hide(); inputFocusRequester.clearFocus() }) {
        Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.Start) {
            Icon(imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                modifier = Modifier
                    .padding(10.dp)
                    .clickable {
                        navController.navigate(NavPage.Login.label) {
                            popUpTo(NavPage.SignUp.label) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                        viewModel.reset()
                    })
            SignUpContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                authState = viewModel.signUp.value,
                handleEvent = viewModel::handleSignUpEvent,
                isInProcess = viewModel.isInProcess.value
            )
        }
    }
}

@Composable
fun SignUpContent(
    modifier: Modifier = Modifier,
    authState: AuthSignUpSate,
    isInProcess: Boolean,
    handleEvent: (event: SignUpViewModel.AuthEvent) -> Unit,
) {
    SignUpForm(
        authState = authState,
        onEmailChanged = { email ->
            handleEvent(
                SignUpViewModel.AuthEvent.EmailChanged(email)
            )
        },
        onPasswordChanged = { password ->
            handleEvent(
                SignUpViewModel.AuthEvent.PasswordChanged(password)
            )
        },
        onUsernameChanged = { username ->
            handleEvent(
                SignUpViewModel.AuthEvent.UsernameChanged(username)
            )
        },
        validateUsername = { username ->
            handleEvent(
                SignUpViewModel.AuthEvent.ValidateUsername(
                    username
                )
            )
        },
        validatePassword = { password ->
            handleEvent(
                SignUpViewModel.AuthEvent.ValidatePassword(
                    password
                )
            )
        },
        validateEmail = { email -> handleEvent(SignUpViewModel.AuthEvent.ValidateEmail(email)) },
        onCreate = { handleEvent(SignUpViewModel.AuthEvent.CreateAccount) },
        isInProcess = isInProcess
    )

}

@Composable
fun SignUpForm(
    modifier: Modifier = Modifier,
    authState: AuthSignUpSate,
    onEmailChanged: (email: String) -> Unit,
    onPasswordChanged: (password: String) -> Unit,
    onUsernameChanged: (username: String) -> Unit,
    validateEmail: (email: String) -> Unit,
    validatePassword: (password: String) -> Unit,
    validateUsername: (username: String) -> Unit,
    onCreate: () -> Unit,
    isInProcess: Boolean
) {
    val username = authState.name
    val email = authState.email
    val password = authState.password

    Box(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight(), contentAlignment = Alignment.Center
    ) {
        Card(
            Modifier.width(350.dp)
        ) {
            Column(
                Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(create_account, fontSize = 25.sp)
                Spacer(modifier = Modifier.height(15.dp))

                Column {
                    UserNameInput(
                        name = username,
                        onUsernameChanged = {
                            onUsernameChanged(it)
                        },
                        validateUsername = validateUsername,
                        error = authState.usenameError.value
                    )
                    EmailInput(
                        email = email,
                        onEmailChanged = { onEmailChanged(it) },
                        validateEmail = validateEmail,
                        error = authState.emailError.value
                    )
                    PasswordInput(
                        password = password,
                        onPasswordChanged = {
                            onPasswordChanged(it)
                        },
                        validatePassword = validatePassword,
                        error = authState.passwordError.value,
                    )
                }

                Button(
                    enabled = !isInProcess,
                    onClick = { onCreate() },
                ) {
                    Text(signUp)
                }
            }
        }
    }
}
