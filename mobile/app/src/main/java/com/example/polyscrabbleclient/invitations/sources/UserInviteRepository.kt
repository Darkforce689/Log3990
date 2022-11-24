package com.example.polyscrabbleclient.invitations.sources

import android.net.Uri
import com.example.polyscrabbleclient.BuildConfig
import com.example.polyscrabbleclient.account.model.Pagination
import com.example.polyscrabbleclient.invitations.models.BaseInvitation
import com.example.polyscrabbleclient.invitations.models.GameInviteArgs
import com.example.polyscrabbleclient.invitations.models.GameInviteDTO
import com.example.polyscrabbleclient.user.model.UserDTO
import com.example.polyscrabbleclient.utils.httprequests.ScrabbleHttpClient
import java.net.URL

data class GetUsersRes(val users: ArrayList<UserDTO>)

object UserInviteRepository {
    fun searchUsers(
        userName: String,
        pagination: Pagination,
        callback: (List<UserDTO>) -> Unit,
    ): Thread {
        val uriBuilder = Uri.parse("${BuildConfig.API_URL}/users")
            .buildUpon()
            .appendQueryParameter("perPage", pagination.perPage.toString())
            .appendQueryParameter("page", pagination.page.toString())
        if (userName.isNotEmpty()) {
            uriBuilder.appendQueryParameter("search", userName)
        }

        val builtUri = uriBuilder.build()

        val url = URL(builtUri.toString())
        val req = Thread {
            val res = ScrabbleHttpClient.get(url, GetUsersRes::class.java)
            if (res === null) {
                return@Thread
            }

            callback(res.users)
        }
        return req
    }

    fun invite(user: UserDTO, baseInvitation: BaseInvitation, callback: () -> Unit): Thread {
        val to = user._id
        val url = URL("${BuildConfig.API_URL}/users/${to}/invite")
        val req = Thread {
            val res = ScrabbleHttpClient.post(url, baseInvitation, GameInviteDTO::class.java)
            if (res === null) {
                return@Thread
            }
            callback()
        }
        return req
    }
}
