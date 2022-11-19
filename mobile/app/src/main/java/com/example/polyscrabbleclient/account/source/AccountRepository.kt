package com.example.polyscrabbleclient.account.source

import android.net.Uri
import com.example.polyscrabbleclient.BuildConfig
import com.example.polyscrabbleclient.account.model.GameHistory
import com.example.polyscrabbleclient.account.model.GameStateHistory
import com.example.polyscrabbleclient.account.model.Pagination
import com.example.polyscrabbleclient.utils.httprequests.ScrabbleHttpClient
import java.net.URL

data class GameStatesResponse(val gameStates: List<GameStateHistory>)

object AccountRepository {
    fun getGamesHistory(pagination: Pagination): GameHistory {
        val builtUri: Uri =
            Uri.parse("${BuildConfig.API_URL}/account/gamesHistory")
                .buildUpon()
                .appendQueryParameter("perPage", pagination.perPage.toString())
                .appendQueryParameter("page", pagination.page.toString())
                .build()
        val response = ScrabbleHttpClient.get(
            URL(builtUri.toString()),
            GameHistory::class.java
        )
        if (response === null) {
            throw RuntimeException("A parsing error occurred while fetching user's game history")
        }
        return response
    }

    fun getGameStates(gameToken: String, callback: (List<GameStateHistory>) -> Unit): Thread {
        val builtUri: Uri =
            Uri.parse("${BuildConfig.API_URL}/account/gameStates")
                .buildUpon()
                .appendQueryParameter("gameToken", gameToken)
                .build()
        val thread = Thread {
            val response = ScrabbleHttpClient.get(
                URL(builtUri.toString()),
                GameStatesResponse::class.java
            )
            if (response === null) {
                throw RuntimeException("A parsing error occurred while fetching gamestates")
            }
            callback(response.gameStates)
        }
        return thread
    }
}
