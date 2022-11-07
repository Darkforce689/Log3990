package com.example.polyscrabbleclient.game.sources

import androidx.compose.runtime.mutableStateListOf
import com.example.polyscrabbleclient.game.domain.TileCreator
import com.example.polyscrabbleclient.game.model.TileModel

data class Player(
    var name: String,
    var points: Int = 0,
    var letters: List<TileModel> = listOf()
) {
    companion object {
        fun fromLightPlayer(player: LightPlayer): Player {
            val letters = player.letterRack.map { letter ->
                TileCreator.createTileFromLetter(letter.char[0], letter.value)
            }.toTypedArray()
            return Player(
                player.name,
                player.points,
                mutableStateListOf(*letters)
            )
        }
    }
}
