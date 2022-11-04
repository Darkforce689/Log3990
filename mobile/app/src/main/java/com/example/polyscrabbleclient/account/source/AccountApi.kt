package com.example.polyscrabbleclient.account.source

import com.example.polyscrabbleclient.BuildConfig
import com.example.polyscrabbleclient.account.model.ConnectionLogResponse
import com.example.polyscrabbleclient.utils.httprequests.ScrabbleHttpClient
import java.net.URL

object AccountApi {
    fun getLogs(page: Int, perPage: Int): ConnectionLogResponse? {
        return ScrabbleHttpClient.get(
            URL(BuildConfig.API_URL + "/account/logHistory?" + "perPage=$perPage&page=$page"),
            ConnectionLogResponse::class.java
        )
    }
}
