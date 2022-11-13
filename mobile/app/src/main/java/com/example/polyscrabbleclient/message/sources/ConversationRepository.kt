package com.example.polyscrabbleclient.message.sources

import android.net.Uri
import com.example.polyscrabbleclient.BuildConfig
import com.example.polyscrabbleclient.account.model.Pagination
import com.example.polyscrabbleclient.message.model.Conversation
import com.example.polyscrabbleclient.utils.httprequests.ScrabbleHttpClient
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.URL

data class GetConversationRes(val conversations: ArrayList<Conversation>)
data class LeaveConversationRes(val message: String, val errors: ArrayList<String>)
data class JoinConversationRes(val message: String, val errors: ArrayList<String>)
data class CreateConversationRes(val conversation: Conversation, val errors: ArrayList<String>?)
data class CreateConversationBody(val name: String)
data class DeleteConversationRes(val conversation: Conversation, val errors: ArrayList<String>?)

object ConversationRepository {

    @OptIn(DelicateCoroutinesApi::class)
    fun getJoinedConversations(callback: (List<Conversation>) -> Unit): Thread {
        val thread = Thread {
            val url = URL("${BuildConfig.API_URL}/conversations?joined=true")
            val res = ScrabbleHttpClient.get(url, GetConversationRes::class.java)
            if (res === null) {
                throw RuntimeException("A parsing error occurred while fetching joined conversations")
            }
            try {
                GlobalScope.launch(Dispatchers.Main) {
                    callback(res.conversations)
                }
            } catch (e: Exception) {
                println("ConversationSource -> getJoinedConversations -> Error")
                e.printStackTrace()
            }
        }
        return thread
    }

    fun leaveConversation(conversationId: String, callback: () -> Unit): Thread {
        val thread = Thread {
            val url = URL("${BuildConfig.API_URL}/conversations/${conversationId}/quit")
            val res = ScrabbleHttpClient.get(url, LeaveConversationRes::class.java)
            if (res === null) {
                throw RuntimeException("A parsing error occurred while leaving a conversation")
            }
            callback()
        }
        return thread
    }

    fun createConversation(
        conversationName: String,
        callback: (CreateConversationRes) -> Unit,
    ): Thread {
        val thread = Thread {
            val body = CreateConversationBody(name = conversationName)
            val url = URL("${BuildConfig.API_URL}/conversations")
            val res = ScrabbleHttpClient.post(url, body, CreateConversationRes::class.java)
            if (res === null) {
                throw RuntimeException("A parsing error occurred while leaving a conversation")
            }
            callback(res)
        }
        return thread
    }

    fun joinConversation(
        conversationId: String,
        callback: () -> Unit,
    ): Thread {
        val thread = Thread {
            val url = URL("${BuildConfig.API_URL}/conversations/${conversationId}/join")
            val res = ScrabbleHttpClient.get(url, JoinConversationRes::class.java)
            if (res === null) {
                throw RuntimeException("A parsing error occured while joining a conversation")
            }
            callback()
        }
        return thread
    }

    fun searchConversation(
        conversationName: String,
        pagination: Pagination,
        callback: (List<Conversation>) -> Unit,
    ): Thread {
        val uriBuilder = Uri.parse("${BuildConfig.API_URL}/conversations")
            .buildUpon()
            .appendQueryParameter("perPage", pagination.perPage.toString())
            .appendQueryParameter("page", pagination.page.toString())

        if (conversationName.isNotEmpty()) {
            uriBuilder.appendQueryParameter("search", conversationName)
        }

        val builtUri = uriBuilder.build()

        val url = URL(builtUri.toString())
        val req = Thread {
            val res = ScrabbleHttpClient.get(url, GetConversationRes::class.java)
            if (res === null) {
                return@Thread
            }

            callback(res.conversations)
        }
        return req
    }

    fun deleteConversation(
        conversationId: String,
        callback: (Conversation) -> Unit,
    ): Thread {
        val url = URL("${BuildConfig.API_URL}/conversations/${conversationId}")
        val req = Thread {
            val res = ScrabbleHttpClient.delete(url, DeleteConversationRes::class.java)
            if (res === null) {
                return@Thread
            }
            callback(res.conversation)
        }
        return req
    }

    fun getCreatedConversation(callback: (List<Conversation>) -> Unit): Thread {
        val url = URL("${BuildConfig.API_URL}/conversations?created=true")
        val req = Thread {
            val res = ScrabbleHttpClient.get(url, GetConversationRes::class.java)
            if (res === null) {
                return@Thread
            }
            callback(res.conversations)
        }
        return req
    }
}
