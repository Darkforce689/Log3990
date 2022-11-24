package com.example.polyscrabbleclient.lobby.view.createGame

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Password
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.polyscrabbleclient.account.components.DisabledInput
import com.example.polyscrabbleclient.account.viewmodel.SEC_IN_MIN
import com.example.polyscrabbleclient.lobby.sources.BotDifficulty
import com.example.polyscrabbleclient.lobby.viewmodels.*
import com.example.polyscrabbleclient.roundDownToMultipleOf
import com.example.polyscrabbleclient.ui.theme.*
import com.example.polyscrabbleclient.utils.TextView
import com.example.polyscrabbleclient.utils.constants.magic_card_map
import kotlin.math.floor

@Preview(showBackground = true)
@Composable
fun Preview() {
    NewGameForm(createGameViewModel = CreateGameViewModel())
}

@Preview(showBackground = true)
@Composable
fun PreviewMagicCards() {
    MagicCards(createGameViewModel = CreateGameViewModel())
}

@Composable
fun NewGameVisibilitySettings(createGameViewModel: CreateGameViewModel) {
    Column(Modifier.fillMaxSize()) {
        TextView(
            "$choose_game_settings:",
            isBold = true,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        Column(
            Modifier.padding(0.dp, 0.dp, 5.dp, 0.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row {
                Checkbox(checked = createGameViewModel.isGamePrivate.value,
                    onCheckedChange = { value ->
                        createGameViewModel.isGamePrivate.value = value
                    }
                )
                Text(text = private_game)
            }
            Row {
                Checkbox(checked = createGameViewModel.isGameProtected.value,
                    onCheckedChange = { value ->
                        createGameViewModel.isGameProtected.value = value
                    }
                )
                Text(text = password_text)
            }
            if (createGameViewModel.isGameProtected.value) {
                GamePasswordInput(
                    password = createGameViewModel.password.value,
                    onPasswordChanged = { password ->
                        createGameViewModel.password.value = password
                    },
                )
            } else {
                DisabledInput(value = password_text)
            }
        }
    }
}

@Composable
fun NewGameForm(createGameViewModel: CreateGameViewModel) {
    Column(
        Modifier.fillMaxSize()
    ) {
        TextView(
            "$choose_game_parameters:",
            isBold = true,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        Column(
            Modifier.fillMaxSize(),
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
            Row(
                Modifier.padding(0.dp, 0.dp, 5.dp, 0.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BotDifficultyMenu(
                    updateBotDifficulty = { newValue ->
                        createGameViewModel.botDifficulty.value = newValue
                    },
                )
            }
        }
    }
}

@Composable
fun MagicCards(createGameViewModel: CreateGameViewModel) {
    val isAllSelected = createGameViewModel.allMagicCardsSelected
    val noCardSelected = createGameViewModel.magicCardIds.size <= 0
    val allCardSelected = createGameViewModel.magicCardIds.size >= magic_card_map.size
    var selectState by remember { mutableStateOf(ToggleableState.Off) }
    Column {
        TextView(
            "$choose_magic_card:",
            isBold = true,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        selectState =
            if (isAllSelected.value && allCardSelected) ToggleableState.On
            else if (!isAllSelected.value && noCardSelected) ToggleableState.Off
            else ToggleableState.Indeterminate

        val onParentClick = {
            if (isAllSelected.value) {
                createGameViewModel.addAllSelected()
            } else {
                createGameViewModel.removeAllSelected()
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            TriStateCheckbox(
                modifier = Modifier.height(30.dp),
                state = selectState,
                onClick = { isAllSelected.value = !isAllSelected.value;onParentClick() },
                enabled = true
            )
            Text(text = select_all)
        }
        Column(
            modifier = Modifier
                .padding(15.dp, 0.dp, 0.dp, 0.dp)
        ) {
            magic_card_map.forEach { entry ->
                Row(
                    Modifier
                        .height(30.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = createGameViewModel.containsMagicCard(entry.key),
                        onCheckedChange = {
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

@Composable
fun TimeSlider(
    progress: Float,
    onSeek: (progress: Double) -> Unit,
) {
    val sliderPosition = remember(progress) { mutableStateOf(progress) }
    Column {
        Text(
            text = "$time_per_turn : ${
                formatTime(sliderPosition.value.roundDownToMultipleOf(30000.0))
            }"
        )
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GamePasswordInput(
    modifier: Modifier = Modifier.fillMaxWidth(),
    password: String,
    onPasswordChanged: (password: String) -> Unit,
) {
    val focusRequester = FocusRequester()
    val keyboardController = LocalSoftwareKeyboardController.current

    TextField(
        value = password,
        onValueChange = { onPasswordChanged(it) },
        modifier = Modifier,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.None
        ),
        keyboardActions = KeyboardActions(
            onDone = { keyboardController?.hide() }
        ),
        label = { Text(password_text) },
        singleLine = true,
        leadingIcon = { Icon(imageVector = Icons.Default.Password, contentDescription = null) }
    )
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
            onValueChange = {},
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
                        updateBotDifficulty(selectedOption.value)
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

fun formatTime(time: Double): String {
    val timeInSec = time / 1000
    val minutes = floor((timeInSec / SEC_IN_MIN)).toInt()
    val seconds = (timeInSec - minutes * SEC_IN_MIN).toInt()
    return "${minutes}m " + "${seconds}s"
}
