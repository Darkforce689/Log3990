package com.example.polyscrabbleclient.message.model

enum class MessageType {SYSTEM, ME, OTHER}
data class Message(val content: String, val from: String, val type: MessageType)
