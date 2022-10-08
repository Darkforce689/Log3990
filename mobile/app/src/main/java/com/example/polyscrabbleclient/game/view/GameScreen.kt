package com.example.polyscrabbleclient.game.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.example.polyscrabbleclient.game.view.draganddrop.DraggedShadow
import com.example.polyscrabbleclient.game.viewmodels.GameViewModel

// From https://github.com/microsoft/surface-duo-compose-sdk/blob/main/DragAndDrop/library/src/main/java/com/microsoft/device/dualscreen/draganddrop/DragContainer.kt
class DragState {
    var isDragging: Boolean by mutableStateOf(false)
    var dragPosition by mutableStateOf(Offset.Zero)
    var dragOffset by mutableStateOf(Offset.Zero)
    var draggableContent by mutableStateOf<(@Composable () -> Unit)?>(null)
    var dragData by mutableStateOf<DragData?>(null)
}

// From https://github.com/microsoft/surface-duo-compose-sdk/blob/main/DragAndDrop/library/src/main/java/com/microsoft/device/dualscreen/draganddrop/DragData.kt
class DragData(
    val type: MimeType = MimeType.TEXT_PLAIN,
    val data: Any? = null
)

// From https://github.com/microsoft/surface-duo-compose-sdk/blob/main/DragAndDrop/library/src/main/java/com/microsoft/device/dualscreen/draganddrop/MimeType.kt
enum class MimeType(val value: String) {
    IMAGE_JPEG("image/jpeg"),
    TEXT_PLAIN("text/plain"),
    UNKNOWN_TYPE("unknown")
}

@Composable
fun GameScreen(navController: NavController?, gameViewModel: GameViewModel?) {
    val dragState = DragState()
    var dragText by remember { mutableStateOf<String?>(null) }
    var dragImage by remember { mutableStateOf<Painter?>(null) }
    val updateDragText: (String?) -> Unit = { newValue -> dragText = newValue }
    val updateDragImage: (Painter?) -> Unit = { newValue -> dragImage = newValue }
    var isDroppingItem by remember { mutableStateOf(false) }
    var isItemInBounds by remember { mutableStateOf(false) }
    val dragData by remember { mutableStateOf<DragData>(DragData()) }

    Column {
        Box {
            BoardView(
                dragState = dragState,
                onDrag = { inBounds, isDragging ->
                    isDroppingItem = isDragging
                    isItemInBounds = inBounds
                },
            )
        }
        Box {
            LetterRackView(dragData, dragState)
        }
        DraggedShadow(
            dragState = dragState
        )
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_C)
@Composable
fun GameScreenPreview() {
    GameScreen(null, null)
}
