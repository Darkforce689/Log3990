package com.example.polyscrabbleclient.lobby.sources

import com.example.polyscrabbleclient.utils.SocketHandler

object LobbySocketHandler : SocketHandler(LobbyEventTypes) {
    override val path = "/newGame"
    override val name: String = LobbySocketHandler.javaClass.simpleName
}
