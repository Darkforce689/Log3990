package com.example.polyscrabbleclient.game.viewmodels

import androidx.lifecycle.ViewModel
import com.example.polyscrabbleclient.user.UserRepository
import com.example.polyscrabbleclient.utils.constants.NoAvatar

class PlayerInfoViewModel : ViewModel() {
    fun getAvatar(name: String): String {
        var avatar: String = NoAvatar
        val thread = Thread {
            UserRepository.getUserByName(name) {
                avatar = it.avatar

            }
        }
        thread.start()
        thread.join()
        return avatar
    }
}
