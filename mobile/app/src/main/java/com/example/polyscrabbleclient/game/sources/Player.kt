package com.example.polyscrabbleclient.game.sources

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.example.polyscrabbleclient.game.domain.TileCreator
import com.example.polyscrabbleclient.game.model.TileContent
import com.example.polyscrabbleclient.game.model.TileModel


data class Player(
    val name: String,
    val points: Int = 0,
    var letters: MutableList<TileModel> = mutableStateListOf()
) {
    companion object {
        fun fromLightPlayer(player: LightPlayer): Player {
            val letters = player.letterRack.map { letter ->
                TileCreator.createTileFromLetter(letter.char[0])
            }.toTypedArray()
            return Player(
                player.name,
                player.points,
                mutableStateListOf(*letters)
            )
        }
    }
}
