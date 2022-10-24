package com.example.polyscrabbleclient.account.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.polyscrabbleclient.NavPage
import com.example.polyscrabbleclient.account.viewmodel.AccountViewmodel
import com.example.polyscrabbleclient.account.viewmodel.UserUpdate
import com.example.polyscrabbleclient.auth.components.Requirement
import com.example.polyscrabbleclient.auth.components.UserNameInput
import com.example.polyscrabbleclient.getAssetsId
import com.example.polyscrabbleclient.ui.theme.*
import com.example.polyscrabbleclient.user.User

@Composable
fun AccountView(viewmodel: AccountViewmodel, navController: NavController) {
    val inputFocusRequester = LocalFocusManager.current
    Row(Modifier.clickable { inputFocusRequester.clearFocus() }) {
        SideNavigation(
            name = viewmodel.userName.value,
            avatar = viewmodel.avatar.value,
            navController = navController
        )
        ProfilContent(
            fields = viewmodel.userInfoField.value,
            usernameError = viewmodel.usernameError.value,
            updateUsername = { name -> viewmodel.updateUsername(name) },
            validateUsername = { name -> viewmodel.validateUsername(name) },
            updateInfoRequest = { viewmodel.updateInfoRequest() },
            updateAvatar = { avatarName -> viewmodel.updateAvatar(avatarName) }
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun SideNavigation(
    name: String,
    avatar: String,
    navController: NavController
) {
    val keyboard = LocalSoftwareKeyboardController.current
    Column(
        modifier = Modifier
            .width(300.dp)
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(imageVector = Icons.Default.ArrowBack,
            contentDescription = null,
            modifier = Modifier
                .padding(10.dp, 10.dp, 0.dp, 0.dp)
                .align(Alignment.Start)
                .clickable {
                    navController.navigate(NavPage.MainPage.label) {
                        keyboard?.hide()
                        popUpTo(NavPage.Account.label) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                })

        Avatar(Modifier.padding(vertical = 25.dp), getAssetsId(avatar))
        Text(text = name, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(15.dp))
        Divider(Modifier.width(250.dp))
        Spacer(Modifier.height(20.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            sideNavList.forEachIndexed { index, pair ->
                val label = pair.first
                val imageVector = pair.second
                Row {
                    Icon(imageVector, contentDescription = null)
                    Text(
                        text = label,
                        modifier = Modifier.clickable {
                            // TODO: in another PR
//                                    onSelected(index)
                        }
                    )
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
private fun ProfilContent(
    fields: UserUpdate,
    usernameError: String?,
    updateUsername: (name: String) -> Unit,
    updateAvatar: (name: String) -> Unit,
    validateUsername: (name: String) -> Unit,
    updateInfoRequest: () -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        Text(
            my_profil,
            style = MaterialTheme.typography.h4,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxHeight(0.2f)
                .padding(vertical = 50.dp, horizontal = 20.dp)
        )
        Row(
            Modifier.fillMaxHeight(0.8f),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                UserInfo(
                    name = fields.name,
                    usernameError = usernameError,
                    updateUsername = updateUsername,
                    validateUsername = validateUsername
                )
                Button(onClick = { updateInfoRequest() }) {
                    Text(save)
                }
            }
            Box(
                Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight(0.6f)
            ) {
                AvatarList { updateAvatar(it) }
            }
        }
    }
}

@Composable
private fun UserInfo(
    name: String,
    usernameError: String?,
    updateUsername: (name: String) -> Unit,
    validateUsername: (name: String) -> Unit
) {
    Column {
        Text(
            text = userName_string,
            modifier = Modifier.padding(vertical = 15.dp),
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

val sideNavList = listOf(
    Pair(my_profil, Icons.Filled.AccountCircle),
    Pair("Other", Icons.Filled.People),
    Pair("Other 2", Icons.Filled.Star),
    Pair("Other 3", Icons.Filled.AccessTime),
)
