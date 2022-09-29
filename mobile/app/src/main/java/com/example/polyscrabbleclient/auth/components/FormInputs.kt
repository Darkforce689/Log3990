package com.example.polyscrabbleclient.auth.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.polyscrabbleclient.auth.viewmodel.AuthServerError
import com.example.polyscrabbleclient.auth.viewmodel.AuthValidation
import com.example.polyscrabbleclient.ui.theme.*
import com.example.polyscrabbleclient.utils.constants.MAX_NAME_LENGTH
import com.example.polyscrabbleclient.utils.constants.MAX_PASSWORD_LENGTH
import com.example.polyscrabbleclient.utils.constants.MIN_NAME_LENGTH
import com.example.polyscrabbleclient.utils.constants.MIN_PASSWORD_LENGTH

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun UserNameInput(
    modifier: Modifier = Modifier.fillMaxWidth(),
    name: String,
    serverError: AuthServerError?,
    onUsernameChanged: (username: String) -> Unit,
    missingFieldError: Boolean
){
    val focusRequester = FocusRequester()
    val errorMessage = remember { mutableStateOf("") }

    var displayErrorModifier = Modifier.alpha(0f)
    val keyboardController = LocalSoftwareKeyboardController.current

    fun validateInput() {
        if( !AuthValidation.isValidUsername(name)) {
            errorMessage.value = Invalid_username_creation(MIN_NAME_LENGTH, MAX_NAME_LENGTH).message
        } else {
            errorMessage.value = ""
        }
    }
    if(missingFieldError){
        errorMessage.value = missing_field
    }
    if(serverError != null) {
        errorMessage.value = userName_not_unique
    }
    if(errorMessage.value.isNotEmpty()) {
        displayErrorModifier = Modifier.alpha(1f)
    }

    TextField(
        value = name, onValueChange = {onUsernameChanged(it.filter { char -> !char.isWhitespace()})},
        modifier = Modifier
            .focusRequester(focusRequester)
            .onFocusChanged { focusState -> if (!focusState.isFocused && name.isNotBlank()) validateInput() },
        isError = errorMessage.value.isNotEmpty(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() } ),

        label = {Text(userName_string)}, singleLine = true, leadingIcon = { Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = null
        )}
    )
    Requirement(modifier = displayErrorModifier, message = errorMessage.value)

}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EmailInput(
    modifier: Modifier = Modifier.fillMaxWidth(),
    email: String,
    onEmailChanged: (email: String)->Unit,
    serverError: AuthServerError?,
    missingFieldError : Boolean,

) {
    val errorMessage = remember { mutableStateOf("") }
    val focusRequester = FocusRequester()
    var displayErrorModifier = Modifier.alpha(0f)
    val keyboardController = LocalSoftwareKeyboardController.current

    fun validateInput() {
        if(!AuthValidation.isValidEmail(email)){
            errorMessage.value = wrong_form_email
        } else {
            errorMessage.value = ""
        }
    }

    if (serverError != null) {
        if (serverError == AuthServerError.EmailAlreadyTaken) {
            errorMessage.value = email_not_unique
        }
        else if (serverError == AuthServerError.EmailNotFound) {
            errorMessage.value = invalid_email
        }
    }
    if(missingFieldError){
        errorMessage.value = missing_field
    }

    if (errorMessage.value.isNotEmpty()) {
        displayErrorModifier = Modifier.alpha(1f)
    }

    TextField(
        value = email,
        onValueChange = { onEmailChanged(it.filter { char -> !char.isWhitespace() }) },
        modifier = Modifier
            .focusRequester(focusRequester)
            .onFocusChanged { focusState ->
                if (!focusState.isFocused && email.isNotEmpty()) {
                    validateInput()
                }
            },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { keyboardController?.hide() }
        ),
        label = { Text(email_string) },
        singleLine = true,
        isError = errorMessage.value.isNotEmpty(),
        leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = null) }
    )
    Requirement(
        modifier = displayErrorModifier,
        message = errorMessage.value
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PasswordInput(
    modifier: Modifier = Modifier.fillMaxWidth(),
    password : String,
    onPasswordChanged : (email: String)->Unit,
    serverError: AuthServerError?,
    missingFieldError : Boolean,
    onCreation : Boolean
) {
    val focusRequester = FocusRequester()
    val showPassword = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf("") }

    var displayErrorModifier = Modifier.alpha(0f)
    val keyboardController = LocalSoftwareKeyboardController.current

    fun validateInput() {
        if(!AuthValidation.isValidPassword(password)){
            errorMessage.value = Invalid_password_creation(MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH).message
        } else {
            errorMessage.value = ""
        }
    }

    if (serverError != null) {
        errorMessage.value = if (AuthServerError.InvalidPassword.label === serverError.label) invalid_password else already_auth
    }

    if(missingFieldError){
        errorMessage.value = missing_field
    }


    if (errorMessage.value.isNotEmpty()) {
        displayErrorModifier = Modifier.alpha(1f)
    }

    TextField(value = password,
        onValueChange = { onPasswordChanged(it) },
        modifier = Modifier
            .focusRequester(focusRequester)
            .onFocusChanged { focusState -> if (!focusState.isFocused && password.isNotBlank() && onCreation) validateInput() },
        label = { Text(password_string) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
        singleLine = true,
        isError = errorMessage.value.isNotEmpty(),
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
        modifier = displayErrorModifier,
        message = errorMessage.value
    )
}

@Composable
fun Requirement(
    modifier: Modifier = Modifier,
    message : String,
){
    Row(
        modifier = modifier.padding(6.dp),
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
