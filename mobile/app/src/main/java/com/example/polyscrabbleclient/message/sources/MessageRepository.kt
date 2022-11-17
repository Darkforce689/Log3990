package com.example.polyscrabbleclient.message.sources

import android.net.Uri
import com.example.polyscrabbleclient.BuildConfig
import com.example.polyscrabbleclient.account.model.Pagination
import com.example.polyscrabbleclient.message.N_MESSAGE_TO_FETCH
import com.example.polyscrabbleclient.message.model.Message
import com.example.polyscrabbleclient.message.model.MessageDTO
import com.example.polyscrabbleclient.message.utils.MessageFactory
import com.example.polyscrabbleclient.utils.httprequests.ScrabbleHttpClient
import java.net.URL
import kotlin.RuntimeException

object MessageRepository {
    fun fetchMessages(conversationId: String, pagination: Pagination, callback: (messages: List<Message>) -> Unit): Thread {
        val offset = pagination.offset ?: 0

        val builtUri: Uri = Uri.parse("${BuildConfig.API_URL}/conversations/${conversationId}/messages")
            .buildUpon()
            .appendQueryParameter("perPage", pagination.perPage.toString())
            .appendQueryParameter("offset", offset.toString())
            .appendQueryParameter("page", pagination.page.toString())
            .build()
        val url = URL(builtUri.toString())
        val req = Thread {
            data class MessagesGetRes(val messages: ArrayList<MessageDTO>?)
            val res = ScrabbleHttpClient.get(url, MessagesGetRes::class.java)
            if (res === null) {
                throw RuntimeException("Parsing error when fetching message from $conversationId")
            }

            if (res.messages == null) {
                callback(listOf())
                return@Thread
            }

            MessageFactory.createMessages(res.messages) {
                callback(it)
            }
        }
        return req
    }
}
