package com.example.polyscrabbleclient.account.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.polyscrabbleclient.BuildConfig
import com.example.polyscrabbleclient.account.model.ConnectionLogResponse
import com.example.polyscrabbleclient.account.source.PageSource
import com.example.polyscrabbleclient.utils.httprequests.ScrabbleHttpClient
import java.net.URL

const val TIME_BASE = 10
const val MILLI_TO_SECONDS = 1000
const val SECONDS_TO_HOUR = 3600
const val MIN_IN_HOUR = 60
const val SEC_IN_MIN = 60

class StatisticsViewModel : ViewModel() {
    private val source = PageSource { pagination ->
        val builtUri: Uri =
            Uri.parse("${BuildConfig.API_URL}/account/logHistory")
                .buildUpon()
                .appendQueryParameter("perPage", pagination.perPage.toString())
                .appendQueryParameter("page", pagination.page.toString())
                .build()
        return@PageSource ScrabbleHttpClient.get(
            URL(builtUri.toString()),
            ConnectionLogResponse::class.java
        )?.logs ?: listOf()
    }

    val logsPager = Pager(PagingConfig(pageSize = 20)) {
        source
    }.flow.cachedIn(viewModelScope)
}
