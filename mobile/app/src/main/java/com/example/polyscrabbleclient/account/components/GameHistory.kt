package com.example.polyscrabbleclient.account.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.example.polyscrabbleclient.account.model.GameHistoryInfo
import com.example.polyscrabbleclient.account.model.GameStateHistory
import com.example.polyscrabbleclient.account.viewmodel.GamesHistoryViewModel
import com.example.polyscrabbleclient.account.viewmodel.ReplayViewModel
import com.example.polyscrabbleclient.lobby.sources.GameMode
import com.example.polyscrabbleclient.lobby.view.ModalView
import com.example.polyscrabbleclient.ui.theme.*
import com.example.polyscrabbleclient.user.User
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun GameHistoryView(viewModel: GamesHistoryViewModel) {
    val games = viewModel.gamesPager.collectAsLazyPagingItems()
    var canOpen by remember { mutableStateOf(false) }

    fun isGameSelected(game: GameHistoryInfo): Boolean {
        return viewModel.selectedGame.value == game
    }

    fun setSelected(game: GameHistoryInfo) {
        if (viewModel.selectedGame.value === game) {
            return
        }
        viewModel.selectedGame.value = game
        viewModel.getGameStates {
            viewModel.gameStateHistory = it
            canOpen = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .fillMaxHeight(0.9f)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        ) {
            StatisticsContent(title = game_played) {
                GamesListView(
                    games,
                    { setSelected(it) },
                ) { isGameSelected(it) }
            }
        }
    }

    if (canOpen) {
        viewModel.gameStateHistory?.let {
            ReplayGameModal(
                onClose = { viewModel.selectedGame.value = null; canOpen = false },
                gameStates = it,
                mode = viewModel.selectedGame.value!!.gameMode
            )
        }
    }
}

@Composable
fun ReplayGameModal(
    onClose: () -> Unit,
    gameStates: List<GameStateHistory>,
    mode: GameMode,
) {
    ModalView(
        closeModal = { onClose() },
        title = "",
        minWidth = 800.dp,
        maxWidth = 1200.dp
    ) { modalButtons ->
        ReplayGame(ReplayViewModel(gameStates, mode)) { modalActions ->
            modalButtons(modalActions)
        }
    }
}

@Composable
private fun GamesListView(
    games: LazyPagingItems<GameHistoryInfo>,
    setSelected: (game: GameHistoryInfo) -> Unit,
    isGameSelected: (game: GameHistoryInfo) -> Boolean,
) {
    val weights = listOf(0.5f, 0.3f, 0.2f)
    val headers = listOf(date, game_result, replay)
    Column(Modifier.padding(25.dp, 0.dp)) {
        Row {
            headers.forEachIndexed { index, text ->
                GamesHeaderCell(text = text, weight = weights[index])
            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(games) { game ->
                if (game != null) {
                    GameView(
                        game = game,
                        weights = weights,
                        onClick = { setSelected(game) }
                    ) { isGameSelected(game) }
                }
            }
        }
    }
}


@Composable
private fun GameView(
    game: GameHistoryInfo,
    weights: List<Float>,
    onClick: () -> Unit,
    isSelected: () -> Boolean
) {
    val targetColor by animateColorAsState(
        targetValue =
        if (isSelected())
            MaterialTheme.colors.primary
        else
            MaterialTheme.colors.background,
        animationSpec = tween(durationMillis = 200)
    )

    val fields = listOf(
        formatDate(game.date),
        formatStatus(game),
        formatVisibility(game)
    )

    Row(
        Modifier
            .background(targetColor),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        fields.forEachIndexed { index, field ->
            val text = if (index >= fields.size - 1) null else field
            val icon = if (index >= fields.size - 1) field else null
            GamesCell(
                text = text as String?,
                weight = weights[index],
                icon = icon as ImageVector?,
                onClick,
                isSelected()
            )
        }
    }
}

@Composable
fun RowScope.GamesCell(
    text: String?,
    weight: Float,
    icon: ImageVector?,
    onClick: () -> Unit,
    isClicked: Boolean
) {

    Box(
        Modifier
            .weight(weight)
            .padding(8.dp)
    ) {
        if (icon != null) {
            Row(
                Modifier.fillMaxSize(),
                Arrangement.Center,
                Alignment.CenterVertically
            ) {
                IconAnimation(onClick, isClicked)
            }
        }
        if (text !== null) {
            Text(text = text)
        }
    }
}

@Composable
fun RowScope.GamesHeaderCell(
    text: String,
    weight: Float,
) {
    Box(
        Modifier
            .border(1.dp, MaterialTheme.colors.onBackground)
            .weight(weight)
            .padding(8.dp)
    ) {
        Text(text = text)
    }
}

@Composable
fun IconAnimation(
    onClick: () -> Unit,
    isLoading: Boolean
) {
    val interactionSource = MutableInteractionSource()
    val coroutineScope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }
    if (isLoading) {
        SpinnerView(size = 20.dp, padding = 5.dp)
        return
    }
    Icon(
        imageVector = Icons.Filled.Visibility,
        contentDescription = null,
        modifier = Modifier
            .scale(scale = scale.value)
            .size(size = 30.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                coroutineScope.launch {
                    scale.animateTo(
                        0.8f,
                        animationSpec = tween(100),
                    )
                    scale.animateTo(
                        1f,
                        animationSpec = tween(100),
                    )
                }
                onClick()
            }
    )
}

private fun formatStatus(game: GameHistoryInfo): String {
    if (game.winnerIds?.contains(User._id) == true) {
        return game_winner
    }
    if (game.forfeitedIds?.contains(User._id) == true) {
        return game_forfeiter
    }
    return game_loser
}

private fun formatDate(date: Double): String {
    val simpleDateFormat = SimpleDateFormat("MMM d, y, h:mm:ss a", Locale.CANADA)
    return simpleDateFormat.format(date.toLong())
}

private fun formatVisibility(game: GameHistoryInfo): ImageVector? {
    return if (game.forfeitedIds?.contains(User._id) == true) null else Icons.Filled.Visibility
}
