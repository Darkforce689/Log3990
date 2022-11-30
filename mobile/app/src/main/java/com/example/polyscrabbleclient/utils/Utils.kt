package com.example.polyscrabbleclient.utils

import com.example.polyscrabbleclient.utils.constants.BOT_AVATARS
import kotlin.math.abs

fun getBotAvatar(name: String): String {
    return BOT_AVATARS[hashCode(name) % BOT_AVATARS.size]
}

fun hashCode(s: String): Int {
    var hash = 0
    if (s.isEmpty()) return 0
    for (element in s) {
        val char = element.code
        hash = hash * 32 - hash + char
        hash = hash or 0
    }
    return abs(hash)
}
