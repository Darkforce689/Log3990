package com.example.polyscrabbleclient.account.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.polyscrabbleclient.account.viewmodel.UserUpdate
import com.example.polyscrabbleclient.auth.components.Requirement
import com.example.polyscrabbleclient.auth.components.UserNameInput
import com.example.polyscrabbleclient.ui.theme.email_string
import com.example.polyscrabbleclient.ui.theme.password_string
import com.example.polyscrabbleclient.ui.theme.save
import com.example.polyscrabbleclient.ui.theme.userName_string
import com.example.polyscrabbleclient.user.User

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
                .fillMaxWidth(0.6f)
                .fillMaxHeight(0.6f)
        ) {
            AvatarList { updateAvatar(it) }
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
            .padding(horizontal = 40.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = userName_string,
                modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 15.dp),
                style = MaterialTheme.typography.subtitle1,
                fontWeight = FontWeight.Bold
            )
            UserNameInput(
                name = name,
                error = usernameError ?: "",
                onUsernameChanged = { updateUsername(it) },
                validateUsername = { validateUsername(it) }
            )
        }
        DisabledInput(label = email_string, value = User.email)
        DisabledInput(label = password_string, value = "*****")
        Button(onClick = { updateInfoRequest() }) {
            Text(save)
        }
    }
}

@Composable
fun DisabledInput(label: String, value: String) {
    Column {
        Text(
            text = label,
            modifier = Modifier.padding(vertical = 15.dp),
            style = MaterialTheme.typography.subtitle1,
            fontWeight = FontWeight.Bold
        )
        TextField(value = value, onValueChange = {}, enabled = false)
        Requirement(message = "", showError = false)
    }
}
