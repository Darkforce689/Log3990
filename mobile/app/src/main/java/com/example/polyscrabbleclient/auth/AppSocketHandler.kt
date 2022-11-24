package com.example.polyscrabbleclient.auth

import com.example.polyscrabbleclient.game.sources.GameSocketHandler
import com.example.polyscrabbleclient.invitations.models.GameInviteDTO
import com.example.polyscrabbleclient.utils.OnEvent
import com.example.polyscrabbleclient.utils.SocketEvent
import com.example.polyscrabbleclient.utils.SocketHandler

class OnAppEvent(override val eventName: String) : OnEvent(eventName) {
    companion object {
        val Invitation = OnAppEvent("invitations")
    }
}

val AppSocketEventTypes: Map<SocketEvent, Class<out Any>> = mapOf(
    Pair(
        OnAppEvent.Invitation,
        GameInviteDTO::class.java
    ),
)

object AppSocketHandler : SocketHandler(AppSocketEventTypes) {
    override val path = "/app"
    override val name: String = GameSocketHandler.javaClass.simpleName
}
