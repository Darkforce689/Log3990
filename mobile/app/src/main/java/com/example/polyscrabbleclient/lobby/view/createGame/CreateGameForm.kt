package com.example.polyscrabbleclient.lobby.view.createGame

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.polyscrabbleclient.lobby.sources.BotDifficulty
import com.example.polyscrabbleclient.lobby.sources.GameMode
import com.example.polyscrabbleclient.lobby.viewmodels.*
import com.example.polyscrabbleclient.roundDownToMultipleOf
import com.example.polyscrabbleclient.ui.theme.*
import com.example.polyscrabbleclient.utils.constants.magic_card_map

@Preview(showBackground = true)
@Composable
fun Preview() {
    NewGameForm(createGameViewModel = CreateGameViewModel())
}

@Composable
fun NewGameForm(createGameViewModel: CreateGameViewModel) {
    Row(
        Modifier.padding(0.dp, 0.dp, 5.dp, 0.dp),
        verticalAlignment = Alignment.CenterVertically
    )
    {
        Column(
            Modifier.width(200.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.Start
        ) {
            PlayerSlider(
                progress = createGameViewModel.numberOfPlayer.value.toFloat(),
                onSeek = { value -> createGameViewModel.numberOfPlayer.value = value.toInt() }
            )
            TimeSlider(
                progress = createGameViewModel.timePerTurn.value.toFloat(),
                onSeek = { value -> createGameViewModel.timePerTurn.value = value.toInt() }
            )
            Row(
                Modifier.padding(0.dp, 0.dp, 5.dp, 0.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = createGameViewModel.randomBonus.value,
                    onCheckedChange = { value ->
                        createGameViewModel.randomBonus.value = value
                    }
                )
                Text(text = random_bonus)
            }
            BotDifficultyMenu(
                updateBotDifficulty = { newValue ->
                    createGameViewModel.botDifficulty.value = newValue
                },
            )
        }
        if (createGameViewModel.gameMode.value == GameMode.Magic) {
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .width(400.dp)
            ) {
                magic_card_map.forEach { entry ->
                    Row(
                        Modifier
                            .padding(0.dp, 0.dp, 5.dp, 0.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = createGameViewModel.containsMagicCard(entry.key),
                            onCheckedChange = { _ ->
                                if (createGameViewModel.containsMagicCard(entry.key))
                                    createGameViewModel.magicCardIds.remove(entry.key)
                                else
                                    createGameViewModel.magicCardIds.add(entry.key)
                            }
                        )
                        Text(text = entry.value)
                    }
                }
            }
        }
    }
}

@Composable
fun TimeSlider(
    progress: Float,
    onSeek: (progress: Double) -> Unit,
) {
    val sliderPosition = remember(progress) { mutableStateOf(progress) }
    Column {
        Text(
            text = "$time_per_turn : ${
                (sliderPosition.value / DEFAULT_TIMER).roundDownToMultipleOf(0.5)
            }"
        ) // TODO display time in min sec
        Slider(
            modifier = Modifier.fillMaxWidth(0.5f),
            value = sliderPosition.value,
            onValueChange = { progress ->
                sliderPosition.value = progress
            },
            onValueChangeFinished = {
                onSeek(sliderPosition.value.roundDownToMultipleOf(30000.0))
            },
            valueRange = MIN_TIMER.toFloat()..MAX_TIMER.toFloat(),
            steps = 8
        )
    }
}

@Composable
fun PlayerSlider(
    progress: Float,
    onSeek: (progress: Float) -> Unit,
) {
    val sliderPosition = remember(progress) { mutableStateOf(progress) }
    Column {
        Text(
            text = "$number_of_player : ${
                sliderPosition.value.roundDownToMultipleOf(1.0).toInt()
            }"
        )
        Slider(
            modifier = Modifier.fillMaxWidth(0.5f),
            value = sliderPosition.value,
            onValueChange = { progress ->
                sliderPosition.value = progress
            },
            onValueChangeFinished = {
                onSeek(sliderPosition.value)
            },
            valueRange = MIN_PLAYER_NUMBER.toFloat()..MAX_PLAYER_NUMBER.toFloat(),
            steps = 1
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BotDifficultyMenu(
    updateBotDifficulty: (value: BotDifficulty) -> Unit
) {
    val options = BotDifficulty.values()
    val expanded = remember { mutableStateOf(false) }
    val selectedOption = remember { mutableStateOf(options[0]) }

    ExposedDropdownMenuBox(
        expanded = expanded.value,
        onExpandedChange = {
            expanded.value = !expanded.value
        }
    ) {
        TextField(
            readOnly = true,
            value = selectedOption.value.value,
            onValueChange = { updateBotDifficulty(selectedOption.value) },
            label = { Text(choose_bot_difficulty) },
            trailingIcon = { icon(expanded.value) },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )
        ExposedDropdownMenu(
            expanded = expanded.value,
            onDismissRequest = {
                expanded.value = false
            }
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    onClick = {
                        selectedOption.value = selectionOption
                        expanded.value = false
                    }
                ) {
                    Text(text = selectionOption.value)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun icon(
    expanded: Boolean
) {
    ExposedDropdownMenuDefaults.TrailingIcon(
        expanded = expanded
    )
}
