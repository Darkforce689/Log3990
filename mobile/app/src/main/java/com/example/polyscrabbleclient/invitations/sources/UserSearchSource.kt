package com.example.polyscrabbleclient.invitations.sources

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.polyscrabbleclient.account.model.Pagination
import com.example.polyscrabbleclient.user.model.UserDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserSearchSource(val userName: String): PagingSource<Int, UserDTO>() {
    private val defaultPageSize = 10;

    override fun getRefreshKey(state: PagingState<Int, UserDTO>): Int? {
        return null
    }

    override suspend fun load(params: PagingSource.LoadParams<Int>): PagingSource.LoadResult<Int, UserDTO> {
        return try {
            val nextPageNumber = params.key ?: 0
            var users: List<UserDTO>? = null
            val request = UserInviteRepository.searchUsers(userName, Pagination(nextPageNumber, defaultPageSize, null)) {
                users = it
            }

            request.start()
            withContext(Dispatchers.IO) {
                request.join()
            }
            PagingSource.LoadResult.Page(
                data = users!!,
                prevKey = null,
                nextKey = if (users!!.size < defaultPageSize) null else nextPageNumber + 1
            )
        } catch (e: Exception) {
            PagingSource.LoadResult.Error(e)
        }
    }
}
