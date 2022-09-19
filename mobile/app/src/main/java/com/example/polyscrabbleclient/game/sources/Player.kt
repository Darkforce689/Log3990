package com.example.polyscrabbleclient.game.sources

data class Player (
    // TODO : List should not be initialized to default values
    val letters: List<Char> = listOf('A','B','C','D','E','F','G')
)
