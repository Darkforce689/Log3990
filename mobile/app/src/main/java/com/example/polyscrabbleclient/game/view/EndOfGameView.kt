package com.example.polyscrabbleclient.game.view

import androidx.compose.foundation.layout.*
import com.example.polyscrabbleclient.ui.theme.Ok
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.polyscrabbleclient.game.sources.GameRepository
import com.example.polyscrabbleclient.game.viewmodels.GameViewModel
import com.example.polyscrabbleclient.lobby.domain.ActionButton
import com.example.polyscrabbleclient.lobby.domain.ModalActions
import com.example.polyscrabbleclient.user.User
import com.example.polyscrabbleclient.utils.constants.LOSS_EXP_BONUS
import com.example.polyscrabbleclient.utils.constants.WIN_EXP_BONUS
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
import kotlin.math.exp

@Composable
fun EndOfGameView (
    viewModel: GameViewModel = viewModel(),
    modalButtons: @Composable (
        modalActions: ModalActions
    ) -> Unit
) {
    Column(horizontalAlignment = Alignment.Start) {

        Text(
            modifier = Modifier.padding(vertical = 2.dp),
            text = viewModel.getFormattedWinners(),
        )
        if (!GameRepository.model.isUserAnObserver()) {
            var expEarned = GameRepository.model.players[0].points
            expEarned = if (expEarned > 0) expEarned else 0
            expEarned = if (GameRepository.model.isUserWinner()) expEarned + WIN_EXP_BONUS else expEarned + LOSS_EXP_BONUS
            User.totalExp = User.totalExp + expEarned
            Text("Vous avez gagné $expEarned points d'expérience.")
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth().padding(0.dp, 10.dp, 0.dp, 0.dp)) {
            Text(User.currentLevel().toString(), modifier = Modifier.padding(0.dp, 0.dp, 5.dp, 0.dp))
            LinearProgressIndicator(progress = User.getProgressValue(), modifier = Modifier
                .width(300.dp)
                .padding(horizontal = 5.dp))

            Text(User.getNextLevel().toString(), modifier = Modifier.padding(5.dp, 0.dp, 0.dp, 0.dp))
        }

        modalButtons(
            ModalActions(
                cancel = ActionButton(
                    label = { Ok }
                )
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EndOfGamePreview() {
    EndOfGameView {}
}

