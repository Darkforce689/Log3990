package com.example.polyscrabbleclient.account.model

enum class LogType(val label: String) {
    CONNECTION("connection"),
    DECONNECTION("deconnection"),
}

data class ConnectionLog(
    val _id: String,
    val userId: String,
    val date: String,
    val type: String,
)
