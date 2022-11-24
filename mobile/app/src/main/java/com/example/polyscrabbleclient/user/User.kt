package com.example.polyscrabbleclient.user

import com.example.polyscrabbleclient.BuildConfig
import com.example.polyscrabbleclient.utils.constants.EXP_PER_LEVEL
import com.example.polyscrabbleclient.utils.constants.MAX_LEVEL
import com.example.polyscrabbleclient.utils.httprequests.ScrabbleHttpClient
import java.net.URL

object User {
    var name: String = ""
    var email: String = ""
    var _id: String = ""
    var avatar: String = "avatardefault"
    var averagePoints: Double = 0.0
    var nGamePlayed: Double = 0.0
    var nGameWon: Double = 0.0
    var averageTimePerGame: Double = 0.0
    var totalExp: Double = 0.0

    fun updateUser(): Thread {
        data class AccountRes(
            val _id: String, val name: String, val email: String, val avatar: String,
            val averagePoints: Double,
            val nGamePlayed: Double,
            val nGameWon: Double,
            val averageTimePerGame: Double,
            val totalExp: Double,
        )

        val accountUrl = URL(BuildConfig.API_URL + "/account")
        val thread = Thread {
            val account = ScrabbleHttpClient.get(accountUrl, AccountRes::class.java)
                ?: throw RuntimeException("No account linked to this cookie")
            name = account.name
            email = account.email
            _id = account._id
            avatar = account.avatar
            averagePoints = account.averagePoints
            nGamePlayed = account.nGamePlayed
            nGameWon = account.nGameWon
            averageTimePerGame = account.averageTimePerGame
            totalExp = account.totalExp
        }
        return thread
    }

    fun resetUserInfo() {
        name = ""
        email = ""
        _id = ""
        avatar = "avatardefault"
        averagePoints = 0.0
        nGamePlayed = 0.0
        nGameWon = 0.0
        averageTimePerGame = 0.0
        totalExp = 0.0
    }

    fun currentLevel(): Int {
        var level = Math.floor(Math.sqrt(totalExp / EXP_PER_LEVEL)).toInt()
        return if (level <= MAX_LEVEL) level else MAX_LEVEL
    }

    fun getNextLevel(): Int {
        return if (currentLevel() < MAX_LEVEL) currentLevel() + 1 else MAX_LEVEL
    }

    fun getProgressValue(): Float {
        var level = Math.sqrt(totalExp / EXP_PER_LEVEL)
        var decimal = level - Math.floor(level)
        return decimal.toFloat()
    }
}
