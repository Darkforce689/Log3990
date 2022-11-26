package com.example.polyscrabbleclient.utils.audio

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.runtime.Composable
import com.example.polyscrabbleclient.R

object AudioPlayer {
    var mediaPlayers: Map<String, MediaPlayer> = mapOf()
    var lastActionWasAPlace: Boolean = false
    var lastNumberOfLetterRemaining: Int = -1

    const val MAGIC_SONG = "magic"
    const val CORRECT_SONG = "correct"
    const val INCORRECT_SONG = "incorrect"
    const val ALARM_CLOCK_CONG = "alarm_clock"
    const val EXCHANGE_SONG = "exchange"
    const val START_TURN_SONG = "start_turn"
    const val WIN_SONG = "win"
    const val LOSE_SONG = "lose"

    @Composable
    fun CreateSongs(context: Context) {
        if (mediaPlayers.isNotEmpty()) return
        mediaPlayers = mapOf(
            MAGIC_SONG to MediaPlayer.create(context, R.raw.magic),
            CORRECT_SONG to MediaPlayer.create(context, R.raw.correct),
            INCORRECT_SONG to MediaPlayer.create(context, R.raw.incorrect),
            ALARM_CLOCK_CONG to MediaPlayer.create(context, R.raw.alarm_clock),
            EXCHANGE_SONG to MediaPlayer.create(context, R.raw.exchange),
            START_TURN_SONG to MediaPlayer.create(context, R.raw.start_turn),
            WIN_SONG to MediaPlayer.create(context, R.raw.win),
            LOSE_SONG to MediaPlayer.create(context, R.raw.lose),
        )
    }

    fun playSong(songId: String) {
        val mediaPlayer = mediaPlayers[songId]
        if (mediaPlayer !== null) mediaPlayer.start()
    }

}
