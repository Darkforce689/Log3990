package com.example.polyscrabbleclient.auth.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.polyscrabbleclient.auth.model.AuthMode
import com.example.polyscrabbleclient.auth.model.AuthLoginSate
import com.example.polyscrabbleclient.auth.viewmodel.AuthenticationViewModel
import com.example.polyscrabbleclient.navPage
import com.example.polyscrabbleclient.ui.theme.connect
import com.example.polyscrabbleclient.ui.theme.connection
import com.example.polyscrabbleclient.ui.theme.email_string
import com.example.polyscrabbleclient.ui.theme.password_string
@Composable
fun SignInScreen(navController: NavController, viewModel: AuthenticationViewModel) {
    Column() {
        Button(modifier = Modifier.padding(20.dp),
            onClick = {
                navController.navigate(navPage.Start.label) {
                    popUpTo(navPage.Login.label) {
                        inclusive = true
                    }
                }
            })
        {
            Text("Page d'accueil")
        }
        AuthContent(
            modifier = Modifier.fillMaxWidth(),
            authState = viewModel.logInState.collectAsState().value,
            handleEvent = viewModel::handleEvent,
            onSignUp = { navController.navigate(navPage.SignUp.label){
                popUpTo(navPage.Login.label)
                }
            }
        )
    }
}

@Composable
fun AuthContent(
    modifier: Modifier=Modifier,
    authState: AuthLoginSate,
    handleEvent: (event: AuthenticationViewModel.AuthEvent)-> Unit,
    onSignUp: () -> Unit
){
    AuthForm(email = authState.email,
             password = authState.password ,
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
            onSignUp = onSignUp
    )

}
//@Preview(showBackground = true)
@Composable
fun AuthForm(modifier: Modifier = Modifier,
             email: String?,
             password: String,
             authMode: AuthMode = AuthMode.LOG_IN,
             onEmailChanged: (email: String) -> Unit,
             onPasswordChanged: (password: String) -> Unit,
             onAuthenticate: () -> Unit,
             onSignUp: ()-> Unit
) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(10.dp), contentAlignment = Alignment.Center) {
        Card(Modifier.padding(15.dp)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(connection)
                Spacer(modifier = Modifier.height(20.dp))
                Column {
                    EmailInput(email = email, onEmailChanged = onEmailChanged)
                    Spacer(modifier = Modifier.height(20.dp))
                    PasswordInput(password = password, onPasswordChanged = onPasswordChanged)
                }
                Spacer(modifier = Modifier.height(20.dp))
                Button(onClick = onAuthenticate ) {
                    Text(connect)
                }
                ClickableText(text = AnnotatedString("Vous n'avez pas de compte?", SpanStyle(color = Color.Cyan, textDecoration = TextDecoration.Underline)), onClick = {onSignUp()})

            }
        }
    }

}

@Composable
fun EmailInput(
    modifier: Modifier = Modifier.fillMaxWidth(),
    email : String?,
    onEmailChanged : (email: String)->Unit
){
    TextField(value = email?: "" , onValueChange = {onEmailChanged(it)},
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send, keyboardType = KeyboardType.Email),
            label = {Text(email_string)}, singleLine = true, leadingIcon = { Icon(
            imageVector = Icons.Default.Email,
            contentDescription = null)}
    )
}

@Composable
fun PasswordInput(
    modifier: Modifier = Modifier.fillMaxWidth(),
    password : String?,
    onPasswordChanged : (email: String)->Unit
){
    TextField(value = password?:"",
        onValueChange = { onPasswordChanged(it)},
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
        label = {Text(password_string)},
        singleLine = true,
        leadingIcon = { Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null)
        }
    )
}

























