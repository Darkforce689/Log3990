package com.example.polyscrabbleclient.user

import com.example.polyscrabbleclient.BuildConfig
import com.example.polyscrabbleclient.user.model.UserDTO
import com.example.polyscrabbleclient.user.model.UserGetRes
import com.example.polyscrabbleclient.user.model.UserStatus
import com.example.polyscrabbleclient.utils.constants.NoAvatar
import com.example.polyscrabbleclient.utils.constants.botNames
import com.example.polyscrabbleclient.utils.getBotAvatar
import com.example.polyscrabbleclient.utils.httprequests.ScrabbleHttpClient
import java.net.URL
import java.util.concurrent.locks.ReentrantLock

object UserRepository {
    private val users: MutableMap<String, UserDTO> = HashMap()      // id -> user
    private val usersByName: MutableMap<String, String> = HashMap() // name -> id
    private val usersLock = ReentrantLock()

    fun getUser(userId: String, callback: (UserDTO) -> Unit) {
        val url = createGetUserUrl(userId)
        val thread = getUserByIdThread(userId, url, callback)
        thread.start()
        thread.join()
    }

    fun getUserByName(name: String, callback: (UserDTO) -> Unit) {
        if (isBotName(name)) {
            return callback(createBotUser(name))
        }
        val url = createGetUserWithNameUrl(name)
        val thread = getUserByNameThread(name, url, callback)
        thread.start()
        thread.join()
    }

    fun isBotName(name: String): Boolean {
        return botNames.contains(name)
    }

    fun getUsers(userIds: List<String>, callback: (List<UserDTO>) -> Unit) {
        val userCollectorLock = ReentrantLock()
        val userCollector = HashMap<Int, UserDTO>()
        val tasks = userIds.mapIndexed { index: Int, userId ->
            val url = createGetUserUrl(userId)
            getUserByIdThread(userId, url) { user ->
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

    private fun getUserByIdThread(userId: String, url: URL, callback: (UserDTO) -> Unit): Thread {
        if (User._id == userId) {
            return selfUser(callback)
        }

        val userInCache = users[userId]
        if (userInCache != null) {
            return Thread {
                callback(userInCache)
            }
        }

        return getUserInternal(url, callback)
    }

    private fun getUserByNameThread(name: String, url: URL, callback: (UserDTO) -> Unit): Thread {
        if (User.name == name) {
            return selfUser(callback)
        }

        val isUserNameInCache = usersByName.contains(name)
        if (isUserNameInCache) {
            val userIdInCache = usersByName[name]
            val userInCache = users[userIdInCache]
            if (userInCache != null) {
                return Thread {
                    callback(userInCache)
                }
            }
        }
        return getUserInternal(url, callback)
    }

    private fun getUserInternal(
        url: URL,
        callback: (UserDTO) -> Unit
    ) = Thread {
        val res = ScrabbleHttpClient.get(url, UserGetRes::class.java)
        val user: UserDTO = res?.user ?: createInexistantUser()
        addToCache(user)
        callback(user)
    }

    private fun selfUser(callback: (UserDTO) -> Unit) =
        Thread {
            callback(
                UserDTO(
                    _id = User._id,
                    email = User.email,
                    name = User.name,
                    avatar = User.avatar,
                    status = UserStatus.Online,
                )
            )
        }

    private fun createGetUserUrl(userId: String): URL {
        val apiUrl = BuildConfig.API_URL
        return URL("${apiUrl}/users/${userId}")
    }

    private fun createGetUserWithNameUrl(name: String): URL {
        return URL("${BuildConfig.API_URL}/users?name=${name}")
    }

    private fun addToCache(user: UserDTO) {
        usersLock.lock()
        users[user._id] = user
        usersByName[user.name] = user._id
        usersLock.unlock()
    }

    private fun createInexistantUser(userId: String = ""): UserDTO {
        return UserDTO(userId, "empty", "InexistantUser", NoAvatar, UserStatus.Online)
    }

    private fun createBotUser(name: String = ""): UserDTO {
        return UserDTO("", "empty", name, getBotAvatar(name), UserStatus.Online)
    }
}
