package com.example.polyscrabbleclient.account.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.polyscrabbleclient.account.model.GameHistoryInfo
import com.example.polyscrabbleclient.account.model.GameStateHistory
import com.example.polyscrabbleclient.account.source.AccountRepository
import com.example.polyscrabbleclient.account.source.AccountRepository.getGamesHistory
import com.example.polyscrabbleclient.account.source.PageSource

class GamesHistoryViewModel : ViewModel() {
    val gamesPager = Pager(PagingConfig(pageSize = 20)) {
        PageSource { pagination ->
            return@PageSource getGamesHistory(pagination).games
        }
    }.flow.cachedIn(viewModelScope)

    var selectedGame = mutableStateOf<GameHistoryInfo?>(null)
    var gameStateHistory: List<GameStateHistory>? = null

    fun getGameStates(callback: (List<GameStateHistory>) -> Unit) {
        if (selectedGame.value === null) {
            callback(emptyList())
            return
        }
        val gameToken = selectedGame.value!!.gameToken
        val task = AccountRepository.getGameStates(gameToken) {
            callback(it)
        }
        task.start()
    }
}
