package com.example.polyscrabbleclient.user.model
import com.google.gson.annotations.SerializedName

data class UserDTO(val _id: String, val email: String, val name: String, val avatar: String, val status: UserStatus)
data class UserGetRes(val user: UserDTO?)

enum class UserStatus(val value: String) {
    @SerializedName("ONLINE")
    Online("ONLINE"),
    @SerializedName("OFFLINE")
    Offline("OFFLINE"),
}
