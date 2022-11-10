package com.example.polyscrabbleclient.utils

abstract class Repository<ModelType, SocketType : SocketHandler> {
    abstract var model: ModelType

    protected abstract val socket: SocketType

    protected open fun reset() {
        socket.disconnect()
        setup()
    }

    protected open fun setup() {
        setupSocket()
    }

    protected abstract fun setupEvents()

    private fun setupSocket() {
        socket.setSocket()
        setupEvents()
        socket.ensureConnection()
    }
}
