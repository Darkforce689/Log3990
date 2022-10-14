package com.example.polyscrabbleclient.game.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.material.Icon
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.polyscrabbleclient.game.viewmodels.GameViewModel
import com.example.polyscrabbleclient.ui.theme.lettersRemainingFR

@Composable
fun GameInfoView(viewModel: GameViewModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.padding(30.dp),
            contentAlignment = Alignment.Center
        ) {
            val remainingTimeFraction =
                viewModel.game.turnRemainingTime.value.toFloat() / viewModel.game.turnTotalTime.value
            CircularProgressIndicator(
                modifier = Modifier
                    .size(100.dp)
                    .alpha(0.4f),
                progress = 1f,
                strokeWidth = 5.dp
            )
            CircularProgressIndicator(
                modifier = Modifier.size(100.dp),
                progress = remainingTimeFraction,
                strokeWidth = 5.dp
            )
            Box(contentAlignment = Alignment.Center) {
                val time = viewModel.game.turnRemainingTime.value
                Text(
                    text = time.toString(),
                    color = if (time < 5)
                        MaterialTheme.colors.error
                    else
                        Color.Unspecified,

                    )
            }
        }
        Text(lettersRemainingFR(viewModel.game.remainingLettersCount.value))
    }
}

@Preview(showBackground = true)
@Composable
fun GameInfoPreview() {
    GameInfoView(GameViewModel())
}
