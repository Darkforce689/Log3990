package com.example.polyscrabbleclient.game.view

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.polyscrabbleclient.game.viewmodels.GameViewModel
import com.example.polyscrabbleclient.ui.theme.PolyScrabbleClientTheme
import com.example.polyscrabbleclient.ui.theme.formatTurnTime
import com.example.polyscrabbleclient.ui.theme.lettersRemainingFR

const val LowTimerBound = 5000

@Composable
fun GameInfoView(viewModel: GameViewModel) {
    Card {
        Column(
            modifier = Modifier.padding(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.padding(30.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(100.dp)
                        .alpha(0.4f),
                    progress = 1f,
                    strokeWidth = 5.dp
                )
                CircularProgressIndicator(
                    modifier = Modifier.size(100.dp),
                    progress = viewModel.getRemainingTimeFraction(),
                    strokeWidth = 5.dp
                )
                Box(contentAlignment = Alignment.Center) {
                    val time = viewModel.turnRemainingTime.value
                    Text(
                        text = formatTurnTime(time.toLong()),
                        color = if (time <= LowTimerBound)
                            MaterialTheme.colors.error
                        else
                            Color.Unspecified,
                    )
                }
            }
            Text(lettersRemainingFR(viewModel.remainingLettersCount.value))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GameInfoPreview() {
    GameInfoView(GameViewModel())
}

@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true)
@Composable
fun DarkGameInfoPreview() {
    PolyScrabbleClientTheme(isDarkTheme = mutableStateOf(true)) {
        GameInfoView(GameViewModel())
    }
}
