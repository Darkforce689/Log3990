package com.example.polyscrabbleclient.game.view

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.polyscrabbleclient.game.sources.IMagicCard
import com.example.polyscrabbleclient.game.viewmodels.GameViewModel
import com.example.polyscrabbleclient.lobby.domain.ActionButton
import com.example.polyscrabbleclient.utils.constants.*

@Composable
fun MagicCardsView(viewModel: GameViewModel = GameViewModel()) {

    fun getMagicCards(): List<IMagicCard> {
        return viewModel.game.drawnMagicCards.value[viewModel.game.getUserIndex()]
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
            split_points_id ->  viewModel.splitPoints()
            exchange_horse_id ->  viewModel.exchangeHorse()
            exchange_horse_all_id ->  viewModel.exchangeHorseAll()
            skip_next_turn_id ->  viewModel.skipNextTurn()
            extra_turn_id ->  viewModel.extraTurn()
            reduce_timer_id ->  viewModel.reduceTimer()
            exchange_a_letter_id ->  viewModel.exchangeALetter()
            place_random_bonus_id ->  viewModel.placeRandomBonus()
            else ->  Unit
        }
    }

    @Composable
    fun getGameActionButton(i: Int): Unit {
        return if (magicCards.size < i + 1) {
            GameActionButton(270.dp, ActionButton({ "" }, { false }))
        } else {
            GameActionButton(
                270.dp,
                ActionButton(
                    { getName(magicCards[i].id) },
                    getEnabled(magicCards[i].id),
                    { getFun(magicCards[i].id) }
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

@Preview(showBackground = true)
@Composable
fun MagicCardsPreview() {
    MagicCardsView(GameViewModel())
}
