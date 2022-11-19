package com.example.polyscrabbleclient.lobby.view.createGame

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.polyscrabbleclient.account.viewmodel.SEC_IN_MIN
import com.example.polyscrabbleclient.auth.components.PasswordInput
import com.example.polyscrabbleclient.auth.components.Requirement
import com.example.polyscrabbleclient.lobby.sources.BotDifficulty
import com.example.polyscrabbleclient.lobby.sources.GameMode
import com.example.polyscrabbleclient.lobby.viewmodels.*
import com.example.polyscrabbleclient.roundDownToMultipleOf
import com.example.polyscrabbleclient.ui.theme.*
import com.example.polyscrabbleclient.utils.constants.magic_card_map
import kotlin.math.floor

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
            Row(
                Modifier.padding(0.dp, 0.dp, 5.dp, 0.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = createGameViewModel.privateGame.value,
                    onCheckedChange = { value ->
                        createGameViewModel.privateGame.value = value
                    }
                )
                Text(text = private_game)
            }
            Row(
                Modifier.padding(0.dp, 0.dp, 5.dp, 0.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = createGameViewModel.isPassword.value,
                    onCheckedChange = { value ->
                        createGameViewModel.isPassword.value = value
                    }
                )
                Text(text = password_text)
            }
            if (createGameViewModel.isPassword.value) {
                Row(
                    Modifier.padding(0.dp, 0.dp, 5.dp, 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GamePasswordInput(
                        password = createGameViewModel.password.value,
                        onPasswordChanged = { password ->
                            createGameViewModel.password.value = password
                        },
                    )
                }
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
