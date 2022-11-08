package com.example.polyscrabbleclient.user.model

data class UserDTO(val _id: String, val email: String, val name: String, val avatar: String)
data class UserGetRes(val user: UserDTO)
