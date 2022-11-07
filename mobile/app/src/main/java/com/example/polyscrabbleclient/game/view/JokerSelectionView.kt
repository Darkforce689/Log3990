package com.example.polyscrabbleclient.game.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.polyscrabbleclient.game.domain.TileCreator
import com.example.polyscrabbleclient.game.domain.alphabetRange
import com.example.polyscrabbleclient.game.model.TileContent
import com.example.polyscrabbleclient.game.model.TileModel
import com.example.polyscrabbleclient.game.viewmodels.GameViewModel
import com.example.polyscrabbleclient.lobby.domain.ActionButton
import com.example.polyscrabbleclient.lobby.domain.ModalActions
import com.example.polyscrabbleclient.lobby.view.ModalView
import com.example.polyscrabbleclient.ui.theme.FlexedSquaredContainer
import com.example.polyscrabbleclient.ui.theme.chooseJokerFR
import com.example.polyscrabbleclient.ui.theme.confirmButtonFR

@Composable
fun JokerSelectionView(
    viewModel: GameViewModel = viewModel(),
    modalButtons: @Composable (
        modalActions: ModalActions
    ) -> Unit
) {
    var selectedTile by remember {
        mutableStateOf<TileContent>(null)
    }

    fun toggleSelect(tileModel: TileModel) {
        selectedTile?.isSelected?.value = false
        selectedTile =
            if (selectedTile == tileModel) {
                null
            } else {
                tileModel
            }
        selectedTile?.isSelected?.value = true
    }

    val flexedTileView: @Composable (Dp, TileModel) -> Unit = { size, content ->
        TileView(tileModel = content, size = size, displayPoint = false) {
            toggleSelect(content)
        }
    }

    EvenlySpacedSubColumn (modifier = Modifier.padding(30.dp)) {

        EvenlySpacedRowContainer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(30.dp)
        ) {

            TileView(
                tileModel = TileCreator.createTileFromLetter('*'),
                displayPoint = false
            ) {}

            Icon(Icons.Rounded.ChevronRight, "Transform Joker")

            TileView(
                tileModel = selectedTile ?: TileCreator.createTileFromLetter('?'),
                displayPoint = false
            ) {}

        }

        val contentCount = 7
        FlexedSquaredContainer(
            contentCount = contentCount,
            contents = (alphabetRange).toList()
                .map { TileCreator.createTileFromLetter(it) },
            view = flexedTileView,
            size = 60.dp.times(contentCount),
            forceAllSameSize = true
        )
    }

    modalButtons(
        ModalActions(
            primary = ActionButton(
                label = { confirmButtonFR },
                canAction = { selectedTile !== null },
                action = { viewModel.chooseJoker(selectedTile) }
            ),
            cancel = ActionButton(
                action = { viewModel.removeJoker() }
            )
        )
    )
}



@Preview(showBackground = true, device = Devices.PIXEL_C)
@Composable
fun JokerSelectionPreview() {
    ModalView(
        closeModal = {},
        title = chooseJokerFR,
        minWidth = 600.dp
    ) { modalButtons ->
        JokerSelectionView { modalActions ->
            modalButtons(modalActions)
        }
    }
}
