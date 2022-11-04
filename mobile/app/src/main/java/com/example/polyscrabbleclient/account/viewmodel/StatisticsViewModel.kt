package com.example.polyscrabbleclient.account.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.polyscrabbleclient.account.source.AccountSource

const val TIME_BASE = 10
const val MILLI_TO_SECONDS = 1000
const val SECONDS_TO_HOUR = 3600
const val MIN_IN_HOUR = 60
const val SEC_IN_MIN = 60

class StatisticsViewModel : ViewModel() {
    private val source = AccountSource()
    val logsPager = Pager(PagingConfig(pageSize = 20)) {
        source
    }.flow.cachedIn(viewModelScope)
}
