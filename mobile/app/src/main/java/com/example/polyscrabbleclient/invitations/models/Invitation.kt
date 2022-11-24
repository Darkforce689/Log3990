package com.example.polyscrabbleclient.invitations.models

data class GameInviteDTO(
    val from: String,
    val to: String,
    val type: InvitationType,
    val args: GameInviteArgs,
    val date: String,
)

data class BaseInvitation(
    val type: InvitationType,
    val args: InvitationArgs,
)

typealias GameInvite = GameInviteDTO
typealias InvitationArgs = Any

enum class InvitationType(val value: String) {
    Game("Game"),
}

data class GameInviteArgs(
    val id: String,
    val password: String?,
)
