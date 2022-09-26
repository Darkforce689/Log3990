package com.example.polyscrabbleclient.game.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.polyscrabbleclient.game.viewmodels.GameViewModel

@Composable
fun GameScreen(navController: NavController, gameViewModel: GameViewModel) {
    Column {
        Box (Modifier.background(MaterialTheme.colors.primaryVariant)) {
            BoardView()
        }
        Box (Modifier.background(MaterialTheme.colors.primary)) {
            LetterRackView(navController, gameViewModel.user)
        }
    }

}
