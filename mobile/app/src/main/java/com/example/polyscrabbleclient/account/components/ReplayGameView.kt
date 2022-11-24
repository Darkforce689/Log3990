package com.example.polyscrabbleclient.account.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.polyscrabbleclient.account.viewmodel.ReplayViewModel
import com.example.polyscrabbleclient.game.domain.TileCreator.createTileFromLetter
import com.example.polyscrabbleclient.game.model.TileModel
import com.example.polyscrabbleclient.game.sources.IMagicCard
import com.example.polyscrabbleclient.game.sources.Player
import com.example.polyscrabbleclient.game.view.*
import com.example.polyscrabbleclient.game.view.draganddrop.DragState
import com.example.polyscrabbleclient.game.viewmodels.PlayerInfoViewModel
import com.example.polyscrabbleclient.lobby.domain.ActionButton
import com.example.polyscrabbleclient.lobby.domain.ModalActions
import com.example.polyscrabbleclient.ui.theme.cardHolder
import com.example.polyscrabbleclient.ui.theme.leaveGameButtonFR
import com.example.polyscrabbleclient.ui.theme.lettersRemainingFR
import com.example.polyscrabbleclient.utils.constants.getIcon

@Composable
fun ReplayGame(
    viewModel: ReplayViewModel,
    modalButtons: @Composable (
        modalActions: ModalActions
    ) -> Unit
) {
    var index by remember { mutableStateOf(0) }
    Column(Modifier.fillMaxHeight(0.8f)) {
        EvenlySpacedRowContainer {
            Icon(
                modifier = Modifier
                    .clickable {
                        if (viewModel.canGetPreviousPage(index)) {
                            index -= 1
                            viewModel.setPage(index);
                        }
                    },
                imageVector = Icons.Filled.ArrowBackIos, contentDescription = null
            )
            EvenlySpacedRowContainer(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.98f)
            ) {
                EvenlySpacedSubColumn(modifier = Modifier.fillMaxHeight()) {
                    ReplayPlayers(viewModel, viewModel.getLeftPlayers())
                }
                EvenlySpacedSubColumn(modifier = Modifier.fillMaxHeight()) {
                    val dragState = DragState
                    BoardView(dragState, viewModel.getBoard())
                    val letters = viewModel.getLettersRemaining()
                    Text(text = lettersRemainingFR(letters))
                }
                EvenlySpacedSubColumn(modifier = Modifier.fillMaxHeight()) {
                    ReplayPlayers(viewModel, viewModel.getRightPlayers())
                }
            }
            Icon(
                modifier = Modifier
                    .clickable {
                        if (viewModel.canGetNextPage(index)) {
                            index += 1
                            viewModel.setPage(index)
                        }
                    }, imageVector = Icons.Filled.ArrowForwardIos, contentDescription = null
            )
        }
    }
    ActionBar(
        modalButtons,
    )
}

@Composable
fun ActionBar(
    modalButtons: @Composable (
        modalActions: ModalActions
    ) -> Unit,
) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        modalButtons(
            ModalActions(
                cancel = ActionButton(label = { leaveGameButtonFR })
            )
        )
    }
}

@Composable
fun ReplayPlayers(
    viewModel: ReplayViewModel,
    players: List<Player>
) {
    val heightSpan = if (viewModel.isMagicGame()) 230.dp else 175.dp
    val weight = if (viewModel.isMagicGame()) 0.3f else 0.5f
    EvenlySpacedSubColumn(modifier = Modifier.fillMaxHeight()) {
        players.forEach {
            Card(
                Modifier
                    .width(275.dp)
                    .height(heightSpan)
            ) {
                EvenlySpacedSubColumn(
                    Modifier
                        .fillMaxHeight()
                        .padding(5.dp)
                ) {
                    PlayerInfoView(player = it, 200.dp, PlayerInfoViewModel(), { false }) {
                        viewModel.isActivePlayer(it)
                    }
                    ReplayLetterRack(weight = weight, letters = it.letters)
                    if (viewModel.isMagicGame()) {
                        val magicCards = viewModel.getMagicCards(it)
                        ReplayMagicCards(
                            weight = weight,
                            magicCards
                        ) { cardId -> viewModel.isCardActive(cardId) }
                    }
                }
            }
        }
    }
}

@Composable
fun ReplayMagicCards(
    weight: Float,
    cards: List<IMagicCard>, isActive: (cardId: String) -> Boolean
) {
    EvenlySpacedRowContainer(
        Modifier
            .fillMaxHeight(weight)
            .fillMaxWidth()
    ) {
        cards.forEach {
            ReplayMagicCard(it.id, isActive(it.id))
        }
    }

}

@Composable
fun ReplayMagicCard(
    cardId: String,
    isActive: Boolean
) {
    val color =
        if (isActive) MaterialTheme.colors.primary else MaterialTheme.colors.cardHolder
    Card(
        Modifier
            .width(60.dp)
            .height(50.dp)
            .background(Color.Transparent, RoundedCornerShape(10.dp)),
        backgroundColor = color,
    ) {
        if (isActive) {
            val icon = getIcon(cardId)
            if (icon != null) {
                Icon(
                    modifier = Modifier.size(30.dp),
                    imageVector = icon,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun ReplayLetterRack(weight: Float, letters: List<TileModel>) {
    EvenlySpacedRowContainer(
        Modifier
            .fillMaxHeight(weight)
            .fillMaxWidth()
    ) {
        letters.forEach {
            val tile = createTileFromLetter(it.letter, it.points)
            TileView(tile, true, 35.dp, 14.sp) {}
        }
    }
}
