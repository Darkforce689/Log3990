package com.example.polyscrabbleclient.user

import com.example.polyscrabbleclient.BuildConfig
import com.example.polyscrabbleclient.user.model.UserDTO
import com.example.polyscrabbleclient.user.model.UserGetRes
import com.example.polyscrabbleclient.utils.httprequests.ScrabbleHttpClient
import java.net.URL
import java.util.concurrent.locks.ReentrantLock

object UserRepository {
    private val users: MutableMap<String, UserDTO> = HashMap()
    private val usersLock = ReentrantLock()

    fun getUser(userId: String, callback: (UserDTO) -> Unit) {
        val url = createGetUserUrl(userId)
        val thread = getUserThread(userId, url, callback)
        thread.start()
        thread.join()
    }

    fun getUserWithName(name: String, callback: (UserDTO) -> Unit) {
        val url = createGetUserWithNameUrl(name)
        val thread = getUserThread(name, url, callback)
        thread.start()
        thread.join()
    }

    fun getUsers(userIds: List<String>, callback: (List<UserDTO>) -> Unit) {
        val userCollectorLock = ReentrantLock()
        val userCollector = HashMap<Int, UserDTO>()
        val tasks = userIds.mapIndexed { index: Int, userId ->
            val url = createGetUserUrl(userId)
            getUserThread(userId, url) { user ->
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

    private fun getUserThread(userId: String, url: URL, callback: (UserDTO) -> Unit): Thread {
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
        return Thread {
            val res = ScrabbleHttpClient.get(url, UserGetRes::class.java) ?: UserGetRes(
                createInexistantUser(userId)
            )
            val user: UserDTO = res.user
            addToCache(user)
            callback(user)
        }
    }

    private fun createGetUserUrl(userId: String): URL {
        val apiUrl = BuildConfig.API_URL
        return URL("${apiUrl}/users/${userId}")
    }

    private fun createGetUserWithNameUrl(name: String): URL {
        val apiUrl = BuildConfig.API_URL
        return URL("${apiUrl}/users?name=${name}")
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
