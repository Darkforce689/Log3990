package com.example.polyscrabbleclient.message.sources

import com.example.polyscrabbleclient.BuildConfig
import com.example.polyscrabbleclient.message.model.Conversation
import com.example.polyscrabbleclient.utils.httprequests.ScrabbleHttpClient
import java.net.URL

data class GetConversationRes(val conversations: ArrayList<Conversation>)

object ConversationSource {

    fun getJoinedConversations(callback: (List<Conversation>) -> Unit): Thread {
        val thread = Thread {
            val url = URL("${BuildConfig.API_URL}/conversations?joined=true")
            val res = ScrabbleHttpClient.get(url, GetConversationRes::class.java)
            if (res === null) {
                throw RuntimeException("A parsing error occurred while fetching joined conversations")
            }
            callback(res.conversations)
        }
        return thread
    }

//    fun getConversations() {
//
//    }

    fun createConversation() {

    }

    fun deleteConversation() {

    }

    fun getConversation() {

    }
}
