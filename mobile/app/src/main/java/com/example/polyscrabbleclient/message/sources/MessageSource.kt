package com.example.polyscrabbleclient.message.sources


import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.polyscrabbleclient.account.model.Pagination
import com.example.polyscrabbleclient.message.N_MESSAGE_TO_FETCH
import com.example.polyscrabbleclient.message.model.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.RuntimeException

class MessageSource(private val conversationId: String?) : PagingSource<Int, Message>() {
    var offset = 0

    override val keyReuseSupported: Boolean = true

    override fun getRefreshKey(state: PagingState<Int, Message>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Message> {
        if (conversationId === null) {
            return LoadResult.Page(
                data = listOf(),
                prevKey = null,
                nextKey = null
            )
        }
        return try {
            val nextPageNumber = params.key ?: 0
            var messages: List<Message>? = null
            val request = MessageRepository.fetchMessages(conversationId, Pagination(nextPageNumber, N_MESSAGE_TO_FETCH, offset)) {
                messages = it
            }

            request.start()
            withContext(Dispatchers.IO) {
                request.join()
            }
            LoadResult.Page(
                data = messages!!,
                prevKey = null,
                nextKey = if (messages!!.size < N_MESSAGE_TO_FETCH) null else nextPageNumber + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
