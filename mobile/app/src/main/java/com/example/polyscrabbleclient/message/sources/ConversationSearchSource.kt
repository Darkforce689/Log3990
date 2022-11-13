package com.example.polyscrabbleclient.message.sources

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.polyscrabbleclient.account.model.Pagination
import com.example.polyscrabbleclient.message.model.Conversation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ConversationSearchSource(val conversationName: String): PagingSource<Int, Conversation>() {
    private val defaultPageSize = 5;

    override fun getRefreshKey(state: PagingState<Int, Conversation>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Conversation> {
        return try {
            val nextPageNumber = params.key ?: 0
            var conversations: List<Conversation>? = null
            val request = ConversationRepository.searchConversation(conversationName, Pagination(nextPageNumber, defaultPageSize, null)) {
                conversations = it
            }

            request.start()
            withContext(Dispatchers.IO) {
                request.join()
            }
            LoadResult.Page(
                data = conversations!!,
                prevKey = null,
                nextKey = if (conversations!!.size < defaultPageSize) null else nextPageNumber + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
