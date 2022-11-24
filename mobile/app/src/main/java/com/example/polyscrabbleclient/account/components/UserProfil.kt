package com.example.polyscrabbleclient.account.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.polyscrabbleclient.account.viewmodel.UserUpdate
import com.example.polyscrabbleclient.auth.components.Requirement
import com.example.polyscrabbleclient.auth.components.UserNameInput
import com.example.polyscrabbleclient.ui.theme.*
import com.example.polyscrabbleclient.user.User
import com.example.polyscrabbleclient.user.User.currentLevel
import com.example.polyscrabbleclient.user.User.getNextLevel
import com.example.polyscrabbleclient.user.User.getProgressValue

@Composable
fun ProfilContent(
    fields: UserUpdate,
    usernameError: String?,
    updateUsername: (name: String) -> Unit,
    updateAvatar: (name: String) -> Unit,
    validateUsername: (name: String) -> Unit,
    updateInfoRequest: () -> Unit,
) {

    Row(
        Modifier.fillMaxHeight(0.8f),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        UserInfo(
            name = fields.name,
            usernameError = usernameError,
            updateUsername = updateUsername,
            validateUsername = validateUsername,
            updateInfoRequest = updateInfoRequest,
        )
        Box(
            Modifier
                .fillMaxHeight()
                .padding(horizontal = 50.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AvatarList { updateAvatar(it) }
                Spacer(modifier = Modifier.height(15.dp))
                Card(
                    Modifier
                        .height(100.dp)
                        .padding(horizontal = 50.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Progression")
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Niveau : " + currentLevel())
                            Text("Expérience totale : " + User.totalExp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                            Text(currentLevel().toString(), modifier = Modifier.padding(0.dp, 0.dp, 5.dp, 0.dp))
                            LinearProgressIndicator(progress = getProgressValue(), modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .padding(horizontal = 5.dp))
                            Text(getNextLevel().toString(), modifier = Modifier.padding(5.dp, 0.dp, 0.dp, 0.dp))
                        }
                    }
                }
            }

        }
    }
}


@Composable
private fun UserInfo(
    name: String,
    usernameError: String?,
    updateUsername: (name: String) -> Unit,
    validateUsername: (name: String) -> Unit,
    updateInfoRequest: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.5f)
            .padding(horizontal = 40.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        InfoCard(label = userName_string, value = "") {
            UserNameInput(
                name = name,
                error = usernameError ?: "",
                onUsernameChanged = { updateUsername(it) },
                validateUsername = { validateUsername(it) }
            )
        }
        InfoCard(label = email_string, value = User.email)
        InfoCard(label = password_string, value = "*****")
        Button(onClick = { updateInfoRequest() }) {
            Text(save)
        }
    }
}

@Composable
fun InfoCard(
    label: String,
    value: String,
    enabledInput: (@Composable () -> Unit)? = null
) {
    Card(
        modifier = Modifier.padding(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 48.dp)
        ) {
            Text(
                text = label,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.subtitle1,
                fontWeight = FontWeight.Bold
            )
            if (enabledInput !== null) {
                enabledInput()
            } else {
                DisabledInput(value)
            }
        }
    }
}

@Composable
private fun DisabledInput(value: String) {
    TextField(
        value = value,
        onValueChange = {},
        enabled = false,
    )
    Requirement(message = "", showError = false)
}

@Preview(showBackground = true, device = Devices.PIXEL_C)
@Composable
fun UserInfoPreview() {
    Box(modifier = Modifier.fillMaxSize()) {
        UserInfo(name = "myName",
            usernameError = null,
            updateUsername = {},
            validateUsername = {},
            updateInfoRequest = {}
        )
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true, device = Devices.PIXEL_C)
@Composable
fun DarkUserInfoPreview() {
    PolyScrabbleClientTheme(mutableStateOf(true)) {
        UserInfoPreview()
    }
}
