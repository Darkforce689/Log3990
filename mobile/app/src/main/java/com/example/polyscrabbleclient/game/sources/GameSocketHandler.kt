package com.example.polyscrabbleclient.game.sources

import com.example.polyscrabbleclient.utils.SocketHandler
import io.socket.client.Socket

object GameSocketHandler : SocketHandler(GameEventTypes) {
    override val path = "/game"
    override val name: String = GameSocketHandler.javaClass.simpleName

    override fun onEvent(event: String) {
        super.onEvent(event)
        when (event) {
            Socket.EVENT_DISCONNECT -> gameSocketDisconnected()
        }
    }

    private val onGameDisconnectedCallbacks = ArrayList<() -> Unit>()

    fun onGameDisconnected(callback: () -> Unit) {
        onGameDisconnectedCallbacks.add(callback)
    }

    private fun gameSocketDisconnected() {
        onGameDisconnectedCallbacks.forEach { it() }
    }

    fun clearEventsCallbacks() {
        onGameDisconnectedCallbacks.clear()
    }
}
