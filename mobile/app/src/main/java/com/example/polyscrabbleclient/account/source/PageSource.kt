package com.example.polyscrabbleclient.account.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.polyscrabbleclient.account.model.Pagination
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

const val PAGE_LIMIT = 10

class PageSource<T : Any>(val request: (Pagination) -> List<T>) :
    PagingSource<Int, T>() {
    override val keyReuseSupported: Boolean = true
    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        return try {
            val nextPageNumber = params.key ?: 0
            var data: List<T>? = null
            val thread = Thread {
                data = request(Pagination(nextPageNumber, PAGE_LIMIT, null))
            }
            thread.start()
            withContext(Dispatchers.IO) {
                thread.join()
            }
            LoadResult.Page(
                data = data!!,
                prevKey = null,
                nextKey = if (data!!.size < PAGE_LIMIT) null else nextPageNumber + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
