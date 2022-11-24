package com.example.polyscrabbleclient.lobby.view.createGame

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AdminPanelSettings
import androidx.compose.material.icons.rounded.StarRate
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.polyscrabbleclient.account.components.SideNavigation
import com.example.polyscrabbleclient.lobby.domain.ActionButton
import com.example.polyscrabbleclient.lobby.domain.ModalActions
import com.example.polyscrabbleclient.lobby.sources.GameMode
import com.example.polyscrabbleclient.lobby.viewmodels.CreateGameMenu
import com.example.polyscrabbleclient.lobby.viewmodels.CreateGameViewModel
import com.example.polyscrabbleclient.ui.theme.create_game
import com.example.polyscrabbleclient.ui.theme.game_parameters
import com.example.polyscrabbleclient.ui.theme.game_settings
import com.example.polyscrabbleclient.ui.theme.magic_cards

@Composable
fun CreateGameModalContent(
    createGameViewModel: CreateGameViewModel,
    modalButtons: @Composable (
        modalActions: ModalActions
    ) -> Unit
) {
    val contentHeight = if (createGameViewModel.gameMode.value == GameMode.Magic) 300.dp else 275.dp
    Row(
        Modifier
            .height(contentHeight)
            .width(530.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        val selectedPage = remember { mutableStateOf(CreateGameMenu.Settings.name) }
        var createGameMenuList = listOf(
            Triple(game_settings, Icons.Rounded.AdminPanelSettings, CreateGameMenu.Settings.name),
            Triple(game_parameters, Icons.Rounded.Tune, CreateGameMenu.Parameters.name),
        )
        if (createGameViewModel.gameMode.value == GameMode.Magic) {
            createGameMenuList = createGameMenuList.plus(
                Triple(magic_cards, Icons.Rounded.StarRate, CreateGameMenu.MagicCards.name)
            )
        }
        Box(
            Modifier
                .fillMaxWidth(0.45f)
                .padding(end = 40.dp)
        ) {
            Row {
                SideNavigation(
                    Modifier
                        .fillMaxWidth()
                        .padding(0.dp),
                    navigationList = createGameMenuList,
                    onSelected = { page: String -> selectedPage.value = page }
                )
                Divider(
                    Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                )
            }
        }
        when (selectedPage.value) {
            CreateGameMenu.Settings.name -> {
                NewGameVisibilitySettings(createGameViewModel)
            }
            CreateGameMenu.Parameters.name -> {
                NewGameForm(createGameViewModel)
            }
            CreateGameMenu.MagicCards.name -> {
                MagicCards(createGameViewModel)
            }
        }
    }
    Box(
        Modifier
            .fillMaxWidth(0.45f)
            .padding(end = 40.dp)
    ) {
        modalButtons(
            ModalActions(
                ActionButton(
                    label = { create_game },
                    canAction = { createGameViewModel.canCreateGame() },
                    action = { createGameViewModel.sendCreateGameRequest() }
                ),
                ActionButton(
                    action = { createGameViewModel.resetForm() }
                )
            )
        )
    }
}
