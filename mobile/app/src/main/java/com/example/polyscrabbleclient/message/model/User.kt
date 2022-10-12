package com.example.polyscrabbleclient.message.model

import com.example.polyscrabbleclient.BuildConfig
import com.example.polyscrabbleclient.utils.httprequests.ScrabbleHttpClient
import java.lang.RuntimeException
import java.net.URL

object User {
    var name :String = ""
    var email :String = ""
    var _id:String = ""

    fun updateUser(): Thread {
        data class AccountRes(val _id: String, val name: String, val email: String)
        val accountUrl = URL(BuildConfig.API_URL + "/account")
        val thread = Thread {
            val account = ScrabbleHttpClient.get(accountUrl, AccountRes::class.java)
                ?: throw RuntimeException("No account linked to this cookie")
            name = account.name
            email = account.email
            _id = account._id
        }
        return thread
    }
 }
