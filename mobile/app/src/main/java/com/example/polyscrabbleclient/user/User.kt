package com.example.polyscrabbleclient.user

import com.example.polyscrabbleclient.BuildConfig
import com.example.polyscrabbleclient.utils.httprequests.ScrabbleHttpClient
import java.net.URL

object User {
    var name: String = ""
    var email: String = ""
    var _id: String = ""
    var avatar: String = "avatardefault"

    fun updateUser(): Thread {
        data class AccountRes(val _id: String, val name: String, val email: String, val avatar : String)

        val accountUrl = URL(BuildConfig.API_URL + "/account")
        val thread = Thread {
            val account = ScrabbleHttpClient.get(accountUrl, AccountRes::class.java)
                ?: throw RuntimeException("No account linked to this cookie")
            name = account.name
            email = account.email
            _id = account._id
            avatar = account.avatar
        }
        return thread
    }
}
