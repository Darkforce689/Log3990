package com.example.polyscrabbleclient.user

import android.support.v4.os.IResultReceiver
import com.example.polyscrabbleclient.BuildConfig
import com.example.polyscrabbleclient.user.model.UserDTO
import com.example.polyscrabbleclient.user.model.UserGetRes
import com.example.polyscrabbleclient.utils.httprequests.ScrabbleHttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.lang.RuntimeException
import java.net.URL
import java.util.concurrent.locks.ReentrantLock

object UserRepository {
    private val users: MutableMap<String, UserDTO> = HashMap()
    private val usersLock = ReentrantLock()

    fun getUser(userId: String, callback: (UserDTO) -> Unit) {
        val thread = getUserThread(userId, callback)
        thread.start()
        thread.join()
    }

    fun getUsers(userIds: List<String>, callback: (List<UserDTO>) -> Unit) {
        val userCollectorLock = ReentrantLock()
        val userCollector = HashMap<Int, UserDTO>()
        val tasks = userIds.mapIndexed { index: Int, userId ->
            getUserThread(userId) { user ->
                userCollectorLock.lock()
                userCollector[index] = user
                userCollectorLock.unlock()
            }
        }

        tasks.forEach { task -> task.start() }
        tasks.forEach { task -> task.join() }

        val users: List<UserDTO> = userIds.mapIndexed { index: Int, _: String ->
                val user = userCollector[index]
                if (user === null) {
                    throw RuntimeException("User not collected in collector")
                }
                user as UserDTO
        }
        callback(users as List<UserDTO>)
    }

    private fun getUserThread(userId: String, callback: (UserDTO) -> Unit): Thread {
        val userInCache = users[userId]
        if (User._id == userId) {
            return Thread {
                callback(
                    UserDTO(
                        _id = User._id,
                        email = User.email,
                        name = User.name,
                        avatar = User.avatar,
                    )
                )
            }
        }

        if (userInCache != null) {
            return Thread {
                callback(userInCache)
            }
        }
        val url = createGetUserUrl(userId)
        return Thread {
            val res = ScrabbleHttpClient.get(url, UserGetRes::class.java) ?: UserGetRes(createInexistantUser(userId))
            val user: UserDTO = res.user
            addToCache(user)
            callback(user)
        }
    }

    private fun createGetUserUrl(userId: String): URL {
        val apiUrl = BuildConfig.API_URL
        return URL("${apiUrl}/users/${userId}")
    }

    private fun addToCache(user: UserDTO) {
        usersLock.lock()
        users[user._id] = user
        usersLock.unlock()
    }

    private fun createInexistantUser(userId: String): UserDTO {
        return UserDTO(userId, "empty", "InexistantUser", "avatardefault")
    }
}
