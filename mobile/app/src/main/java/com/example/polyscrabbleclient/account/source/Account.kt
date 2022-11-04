package com.example.polyscrabbleclient.account.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.polyscrabbleclient.account.model.ConnectionLog
import com.example.polyscrabbleclient.account.model.ConnectionLogResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AccountSource : PagingSource<Int, ConnectionLog>() {
    override val keyReuseSupported: Boolean = true

    override fun getRefreshKey(state: PagingState<Int, ConnectionLog>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ConnectionLog> {
        return try {
            val nextPageNumber = params.key ?: 0
            var response: ConnectionLogResponse? = null
            val request = Thread {
                response = AccountRepository.getLogs(nextPageNumber, 10)
            }
            request.start()
            withContext(Dispatchers.IO) {
                request.join()
            }
            LoadResult.Page(
                data = response!!.logs,
                prevKey = null,
                nextKey = if (response!!.logs.size < 10) null else response!!.pagination.page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
