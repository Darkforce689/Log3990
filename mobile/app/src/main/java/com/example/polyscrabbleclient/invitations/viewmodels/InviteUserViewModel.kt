package com.example.polyscrabbleclient.invitations.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.polyscrabbleclient.invitations.models.BaseInvitation
import com.example.polyscrabbleclient.invitations.models.GameInviteArgs
import com.example.polyscrabbleclient.invitations.models.InvitationType
import com.example.polyscrabbleclient.invitations.sources.UserInviteRepository
import com.example.polyscrabbleclient.invitations.sources.UserSearchSource
import com.example.polyscrabbleclient.lobby.sources.LobbyRepository
import com.example.polyscrabbleclient.user.User
import com.example.polyscrabbleclient.user.model.UserDTO
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@OptIn(DelicateCoroutinesApi::class)
class InviteUserViewModel(private val args: GameInviteArgs): ViewModel() {
    val userName = mutableStateOf("")
    val isOnline = mutableStateOf(false)

    private val nonInvitableUserNames = HashSet<String>()

    val usersPager = Pager(PagingConfig(pageSize = 10)) {
        createUserSource()
    }.flow.cachedIn(viewModelScope)

    private fun createUserSource(): UserSearchSource {
        return UserSearchSource(userName.value, isOnline.value)
    }

    init {
        LobbyRepository.model.playerNamesInLobby.onEach {
            setNonInvitableUsers(it)
        }.launchIn(GlobalScope)
    }

    fun close() {
        userName.value = ""
    }

    private fun setNonInvitableUsers(users: List<String>) {
        if (isUserInviteButtonEnabled == null) {
            nonInvitableUserNames.clear()
            nonInvitableUserNames.addAll(users)
            return
        }

        nonInvitableUserNames.forEach{
            isUserInviteButtonEnabled[it]?.value = true
        }

        users.forEach{
            val isEnabled = isUserInviteButtonEnabled[it]
            if (isEnabled != null) {
                isEnabled.value = false
            }
        }

        nonInvitableUserNames.clear()
        nonInvitableUserNames.addAll(users)
    }

    fun inviteUser(user: UserDTO) {
        val baseInvitation = BaseInvitation(InvitationType.Game, args)
        UserInviteRepository.invite(user, baseInvitation) {
                val isEnabled = isUserInviteButtonEnabled[user.name]
                if (isEnabled != null) {
                    isEnabled.value = false
                }
        }.start()
    }

    private val isUserInviteButtonEnabled: MutableMap<String, MutableState<Boolean>> = HashMap()
    fun createButtonDisabledState(userName: String): MutableState<Boolean> {
        val isInvitable = User.name != userName && !nonInvitableUserNames.contains(userName)
        val mutableState = mutableStateOf(isInvitable)
        isUserInviteButtonEnabled[userName] = mutableState
        return mutableState
    }
}
