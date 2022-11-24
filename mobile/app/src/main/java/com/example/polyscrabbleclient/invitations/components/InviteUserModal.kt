package com.example.polyscrabbleclient.invitations.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.example.polyscrabbleclient.account.components.Avatar
import com.example.polyscrabbleclient.getAssetsId
import com.example.polyscrabbleclient.invitations.models.GameInviteArgs
import com.example.polyscrabbleclient.invitations.viewmodels.InviteUserViewModel
import com.example.polyscrabbleclient.lobby.domain.ActionButton
import com.example.polyscrabbleclient.lobby.domain.ModalActions
import com.example.polyscrabbleclient.lobby.sources.LobbyRepository
import com.example.polyscrabbleclient.lobby.view.ModalView
import com.example.polyscrabbleclient.lobby.viewmodels.NewGameViewModel
import com.example.polyscrabbleclient.ui.theme.*
import com.example.polyscrabbleclient.user.model.UserDTO
import com.example.polyscrabbleclient.user.model.UserStatus


@Composable
fun InviteUserToGameModal(
    inviteArgs: GameInviteArgs,
    isOpened: Boolean,
    onClose: () -> Unit,
) {
    if (!isOpened) {
        return
    }

    ModalView(
        closeModal = {},
        title = null,
    ) { modalButtons ->
        InviteUserToGameModalContent(inviteArgs, onClose) { modalActions ->
            modalButtons(modalActions)
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun InviteUserToGameModalContent(
    inviteArgs: GameInviteArgs,
    onClose: () -> Unit,
    modalButtons: @Composable (
        modalActions: ModalActions
    ) -> Unit
) {
    val viewModel = InviteUserViewModel(inviteArgs)
    val users = viewModel.usersPager.collectAsLazyPagingItems()
    val keyboard = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .width(450.dp)
            .fillMaxHeight(0.5f)
            .clickable {
                keyboard?.hide()
                focusManager.clearFocus()
            }
    ) {
        SearchBar(
            onSearch = {
                viewModel.userName.value = it
                users.refresh()
            },
            userName = viewModel.userName,
            isOnline = viewModel.isOnline,
        )
        Box(Modifier.weight(0.1f)) {
            SearchResultInvitableUserList(
                users = users,
                createButtonState = {
                    viewModel.createButtonDisabledState(it.name)
                },
                onInvite = {
                    viewModel.inviteUser(it)
                }
            )
        }
    }

    modalButtons(
        ModalActions(
            cancel = ActionButton(
                label = { close_invite_user },
                action = {
                    viewModel.close()
                    onClose()
                }
            )
        )
    )
}

@Composable
fun SearchBar(
    userName: MutableState<String>,
    isOnline: MutableState<Boolean>,
    onSearch: (String) -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .weight(1f)
                .padding(end = 30.dp)
        ) {
            OutlinedTextField(
                value = userName.value,
                onValueChange = { onSearch(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(user_search) }
            )
        }
        Button(
            modifier = Modifier.padding(end = 10.dp),
            onClick = { onSearch(userName.value) }
        ) {
            Icon(imageVector = Icons.Default.Search, contentDescription = null)
        }
        Row(modifier= Modifier.padding(5.dp), verticalAlignment = Alignment.CenterVertically) {
            Switch(
                checked = isOnline.value,
                onCheckedChange = {
                    isOnline.value = it
                    onSearch(userName.value)
                }
            )
            Text(user_status_online)
        }

    }
}

@Composable
fun SearchResultInvitableUserList(
    users: LazyPagingItems<UserDTO>,
    createButtonState: (UserDTO) -> MutableState<Boolean>,
    onInvite: (UserDTO) -> Unit,
) {
    LazyColumn(
        Modifier.fillMaxSize()
    ) {
        items(users) { user ->
            user?.let {
                val isEnabled = createButtonState(user)
                InvitableUser(
                    user = user,
                    isInvitable = isEnabled,
                    onInvite = {
                        onInvite(user)
                    }
                )
            }
            Divider(Modifier.height(1.dp))
        }
    }
}

@Composable
fun InvitableUser(
    user: UserDTO,
    isInvitable: MutableState<Boolean>,
    onInvite: () -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        val avatarId = getAssetsId(name = user.avatar)
        Row(
            modifier = Modifier.weight(1f).padding(end = 15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Avatar(
                    modifier = Modifier
                        .size(50.dp),
                    avatarId = if (avatarId == 0) getAssetsId(name = "avatardefault") else avatarId
                )
                Spacer(modifier = Modifier.width(15.dp))
                Text(text = user.name, fontSize = 18.sp)
            }

            if (user.status == UserStatus.Online) {
                Text(user_status_online, fontSize = 17.sp, color = MaterialTheme.colors.userStatusOnline)
            }

            if (user.status == UserStatus.Offline) {
                Text(user_status_offline, fontSize = 17.sp, color = Color.Gray)
            }
        }

        Button(
            modifier = Modifier.size(50.dp),
            shape = CircleShape,
            onClick = {
                onInvite()
            },
            enabled = isInvitable.value,
        ) {
            Icon(imageVector = Icons.Default.Mail, contentDescription = null)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InvitableUserPreview() {
    val user = UserDTO("abc", "email", "olivier1", avatar = "polarbear", status = UserStatus.Online)
    Box(Modifier.width(450.dp)) {
        InvitableUser(
            user = user,
            isInvitable = remember { mutableStateOf(true) },
            onInvite = {}
        )
    }
}


@Preview(showBackground = true)
@Composable
fun SearchBarPreview() {
    var v = remember {
        mutableStateOf("")
    }

    var v2 = remember {
        mutableStateOf(true)
    }
    Box(Modifier.width(400.dp)) {
        SearchBar(userName = v, onSearch = { v.value = it }, isOnline = v2)
    }
}
