package com.example.polyscrabbleclient.game.view

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.polyscrabbleclient.game.sources.IMagicCard
import com.example.polyscrabbleclient.game.sources.Player
import com.example.polyscrabbleclient.game.viewmodels.GameViewModel
import com.example.polyscrabbleclient.lobby.domain.ActionButton
import com.example.polyscrabbleclient.user.User
import com.example.polyscrabbleclient.utils.constants.*

val MagicCardWidth = 240.dp

@Composable
fun MagicCardsView(viewModel: GameViewModel = GameViewModel()) {

    fun getMagicCards(): List<IMagicCard> {
        return viewModel.game.getDrawnMagicCards()
    }

    val magicCards: List<IMagicCard> = getMagicCards()

    fun getEnabled(id: String): () -> Boolean {
        when (id) {
            split_points_id, exchange_horse_id, exchange_horse_all_id,
            skip_next_turn_id, extra_turn_id, reduce_timer_id -> return { viewModel.canUseMagicCards() }
            exchange_a_letter_id -> return { viewModel.canExchangeMagicCard() }
            place_random_bonus_id -> return { viewModel.canPlaceRandomBonusMagicCard() }
            else -> return { false }
        }
    }

    fun getFun(id: String): Unit {
        return when (id) {
            split_points_id -> viewModel.splitPoints()
            exchange_horse_id -> viewModel.exchangeHorse()
            exchange_horse_all_id -> viewModel.exchangeHorseAll()
            skip_next_turn_id -> viewModel.skipNextTurn()
            extra_turn_id -> viewModel.extraTurn()
            reduce_timer_id -> viewModel.reduceTimer()
            exchange_a_letter_id -> viewModel.exchangeALetter()
            place_random_bonus_id -> viewModel.placeRandomBonus()
            else -> Unit
        }
    }

    @Composable
    fun getGameActionButton(i: Int): Unit {
        return if (magicCards.size < i + 1) {
            GameActionButton(MagicCardWidth, ActionButton({ "" }, { false }))
        } else {
            GameActionButton(
                MagicCardWidth,
                ActionButton(
                    { getName(magicCards[i].id) },
                    getEnabled(magicCards[i].id),
                    { getFun(magicCards[i].id) },
                    getIcon(magicCards[i].id),
                )
            )
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row {
            getGameActionButton(0)
            getGameActionButton(1)
            getGameActionButton(2)
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true, device = Devices.PIXEL_C)
@Composable
fun MagicCardsPreview1() {
    val gmv = GameViewModel()
    User.name = "ABC"
    gmv.game.players = mutableListOf(Player(User.name))
    gmv.game.drawnMagicCards = mutableStateOf(
        listOf(
            listOf(
                IMagicCard(exchange_a_letter_id),
                IMagicCard(split_points_id),
                IMagicCard(place_random_bonus_id)
            )
        )
    )
    gmv.game.drawnMagicCards.value[gmv.game.getUserIndex()]
    MagicCardsView(GameViewModel())
}

@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true, device = Devices.PIXEL_C)
@Composable
fun MagicCardsPreview2() {
    val gmv = GameViewModel()
    User.name = "ABC"
    gmv.game.players = mutableListOf(Player(User.name))
    gmv.game.drawnMagicCards = mutableStateOf(
        listOf(
            listOf(
                IMagicCard(exchange_horse_id),
                IMagicCard(exchange_horse_all_id),
                IMagicCard(skip_next_turn_id)
            )
        )
    )
    gmv.game.drawnMagicCards.value[gmv.game.getUserIndex()]
    MagicCardsView(GameViewModel())
}

@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true, device = Devices.PIXEL_C)
@Composable
fun MagicCardsPreview3() {
    val gmv = GameViewModel()
    User.name = "ABC"
    gmv.game.players = mutableListOf(Player(User.name))
    gmv.game.drawnMagicCards = mutableStateOf(
        listOf(
            listOf(
                IMagicCard(extra_turn_id),
                IMagicCard(reduce_timer_id),
            )
        )
    )
    gmv.game.drawnMagicCards.value[gmv.game.getUserIndex()]
    MagicCardsView(GameViewModel())
}
