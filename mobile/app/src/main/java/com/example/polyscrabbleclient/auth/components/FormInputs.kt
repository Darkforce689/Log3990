package com.example.polyscrabbleclient.auth.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.polyscrabbleclient.ui.theme.email_string
import com.example.polyscrabbleclient.ui.theme.password_string
import com.example.polyscrabbleclient.ui.theme.userName_string

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun UserNameInput(
    modifier: Modifier = Modifier.fillMaxWidth(),
    name: String,
    error: String,
    onUsernameChanged: (username: String) -> Unit,
    validateUsername: (username: String) -> Unit
) {
    val focusRequester = FocusRequester()
    val keyboardController = LocalSoftwareKeyboardController.current
    TextField(
        value = name,
        onValueChange = { onUsernameChanged(it.filter { char -> !char.isWhitespace() }) },
        modifier = Modifier
            .focusRequester(focusRequester)
            .onFocusChanged { focusState ->
                if (!focusState.isFocused && name.isNotBlank()) validateUsername(
                    name
                )
            },
        isError = error.isNotEmpty(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.None
        ),
        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
        label = { Text(userName_string) },
        singleLine = true,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null
            )
        }
    )
    Requirement(
        showError = error.isNotEmpty(),
        message = error
    )

}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EmailInput(
    modifier: Modifier = Modifier.fillMaxWidth(),
    email: String,
    onEmailChanged: (email: String) -> Unit,
    validateEmail: (username: String) -> Unit,
    error: String,
) {
    val focusRequester = FocusRequester()
    val keyboardController = LocalSoftwareKeyboardController.current

    TextField(
        value = email,
        onValueChange = { onEmailChanged(it.filter { char -> !char.isWhitespace() }) },
        modifier = Modifier
            .focusRequester(focusRequester)
            .onFocusChanged { focusState ->
                if (!focusState.isFocused && email.isNotEmpty()) {
                    validateEmail(email)
                }
            },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.None
        ),
        keyboardActions = KeyboardActions(
            onDone = { keyboardController?.hide() }
        ),
        label = { Text(email_string) },
        singleLine = true,
        isError = error.isNotEmpty(),
        leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = null) }
    )
    Requirement(
        showError = error.isNotEmpty(),
        message = error
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PasswordInput(
    modifier: Modifier = Modifier.fillMaxWidth(),
    password: String,
    onPasswordChanged: (email: String) -> Unit,
    validatePassword: (password: String) -> Unit,
    error: String,
) {
    val showPassword = remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = FocusRequester()

    TextField(value = password,
        onValueChange = { onPasswordChanged(it) },
        modifier = Modifier
            .focusRequester(focusRequester)
            .onFocusChanged { focusState ->
                if (!focusState.isFocused && password.isNotBlank()) validatePassword(password)
            },
        label = { Text(password_string) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.None
        ),
        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
        singleLine = true,
        isError = error.isNotEmpty(),
        visualTransformation = if (showPassword.value) VisualTransformation.None
        else PasswordVisualTransformation(),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null
            )
        },
        trailingIcon = {
            val (icon, iconColor) = if (showPassword.value)
                Pair(Icons.Filled.Visibility, MaterialTheme.colors.primary)
            else Pair(Icons.Filled.VisibilityOff, Color.Gray)
            IconButton(onClick = { showPassword.value = !showPassword.value }) {
                Icon(icon, null, tint = iconColor)
            }
        }
    )
    Requirement(
        showError = error.isNotEmpty(),
        message = error
    )
}

@Composable
fun Requirement(
    modifier: Modifier = Modifier,
    showError: Boolean,
    message: String,
) {
    Row(
        modifier = modifier
            .padding(6.dp)
            .alpha(if (showError) 1f else 0f),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(12.dp),
            imageVector = Icons.Default.Error,
            contentDescription = null,
            tint = Color.Red
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = message,
            fontSize = 12.sp,
            color = Color.Red
        )
    }
}
