package com.example.polyscrabbleclient.account.source

import com.example.polyscrabbleclient.account.model.ConnectionLogResponse

object AccountRepository {
    fun getLogs(page: Int, limit: Int): ConnectionLogResponse? {
        return AccountApi.getLogs(page, limit)
    }
}
