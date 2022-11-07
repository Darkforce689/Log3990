package com.example.polyscrabbleclient.game.view

import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.polyscrabbleclient.game.model.BoardDimension
import com.example.polyscrabbleclient.game.model.BoardRange
import com.example.polyscrabbleclient.game.model.GridTileModel
import com.example.polyscrabbleclient.game.model.RowChar
import com.example.polyscrabbleclient.game.view.draganddrop.DragState
import com.example.polyscrabbleclient.game.viewmodels.BoardViewModel
import com.example.polyscrabbleclient.game.viewmodels.TileCoordinates
import com.example.polyscrabbleclient.ui.theme.grayedOutTileBackground
import com.example.polyscrabbleclient.ui.theme.tileBackground
import com.example.polyscrabbleclient.ui.theme.transientTileBackground
import kotlin.properties.Delegates

const val ThickDividerWidth = Stroke.DefaultMiter
const val GridDimension = BoardDimension + 1
const val SoftBackgroundAlpha = 0.2f
const val HardBackgroundAlpha = 0.7f
val BoardSize = 550.dp
val BoardPadding = 10.dp
val GridSize = BoardSize - BoardPadding.times(2)
val GridDivisionSize = GridSize / GridDimension
val HeaderRange = (BoardRange.first + 1)..(BoardRange.last + 1)
val HeaderTextSize = BoardSize.div(GridDimension).div(1.8f)
val DivisionCenterOffset = GridDivisionSize.times(0.3f)
const val DoubleMultiplierLabel = "x2"
const val TripleMultiplierLabel = "x3"
const val LetterMultiplierLabel = "Lettre"
const val WordMultiplierLabel = "Mot"

@Composable
fun BoardCanvasView(dragState: DragState, viewModel: BoardViewModel) {
    val themeColors = listOf(
        MaterialTheme.colors.primary,
        MaterialTheme.colors.secondary,
        MaterialTheme.colors.secondary
    )
    val thickDividerIndexes = listOf(0, 1, GridDimension)

    val rowChars = RowChar.values()
    val rowCharsColor = MaterialTheme.colors.primary
    val tileTextColor = MaterialTheme.colors.onBackground
    val tileBackground = MaterialTheme.colors.tileBackground
    val transientTileBackground = MaterialTheme.colors.transientTileBackground

    fun DrawScope.drawColumnDivider(
        currentDivisionOffset: Float,
        strokeWidth: Float
    ) {
        drawLine(
            brush = Brush.linearGradient(colors = themeColors),
            start = Offset(currentDivisionOffset, 0f),
            end = Offset(currentDivisionOffset, GridSize.toPx()),
            strokeWidth = strokeWidth
        )
    }

    fun DrawScope.drawRowDivider(
        currentDivisionOffset: Float,
        strokeWidth: Float
    ) {
        drawLine(
            brush = Brush.linearGradient(colors = themeColors),
            start = Offset(0f, currentDivisionOffset),
            end = Offset(GridSize.toPx(), currentDivisionOffset),
            strokeWidth = strokeWidth
        )
    }

    fun DrawScope.drawColumnHeader(
        gridDivisionIndex: Int,
        currentDivisionOffset: Float,
        headerTextPaint: NativePaint,
    ) {
        val horizontalTextOffset =
            currentDivisionOffset +
                DivisionCenterOffset.toPx() -
                // WARNING -> TWEAK
                (gridDivisionIndex / 10) * (HeaderTextSize.toPx() / 3)
        drawIntoCanvas {
            it.nativeCanvas.drawText(
                gridDivisionIndex.toString(),
                horizontalTextOffset,
                // WARNING -> TWEAK
                2.4f * DivisionCenterOffset.toPx(),
                headerTextPaint
            )
        }
    }

    fun DrawScope.drawRowHeader(
        gridDivisionIndex: Int,
        currentDivisionOffset: Float,
        headerTextPaint: NativePaint,
    ) {
        val rowCharIndex = gridDivisionIndex - 2
        val rowHeaderChar = rowChars[rowCharIndex].toString()
        drawIntoCanvas {
            it.nativeCanvas.drawText(
                rowHeaderChar,
                DivisionCenterOffset.toPx(),
                currentDivisionOffset - DivisionCenterOffset.toPx(),
                headerTextPaint
            )
        }
    }

    fun DrawScope.drawGridBackground() {
        drawRect(
            brush = Brush.linearGradient(colors = themeColors),
            topLeft = Offset(0f, 0f),
            size = Size(GridSize.toPx(), GridSize.toPx()),
            alpha = 0.1f
        )
    }

    fun DrawScope.drawGridLayout() {
        val headerTextPaint = Paint().asFrameworkPaint().apply {
            isAntiAlias = true
            textSize = HeaderTextSize.toPx()
            color = rowCharsColor.toArgb()
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        }

        drawGridBackground()

        for (gridDivisionIndex in 0..GridDimension) {
            val strokeWidth =
                if (gridDivisionIndex in thickDividerIndexes)
                    ThickDividerWidth
                else
                    Stroke.HairlineWidth
            val currentDivisionOffset = gridDivisionIndex * GridDivisionSize.toPx()

            drawColumnDivider(currentDivisionOffset, strokeWidth)
            drawRowDivider(currentDivisionOffset, strokeWidth)
            if (gridDivisionIndex in BoardRange) {
                drawColumnHeader(gridDivisionIndex, currentDivisionOffset, headerTextPaint)
            }
            if (gridDivisionIndex in HeaderRange) {
                drawRowHeader(gridDivisionIndex, currentDivisionOffset, headerTextPaint)
            }
        }
    }

    fun DrawScope.drawTileBackground(
        color: Color,
        column: Int,
        row: Int,
        alpha: Float = 1f
    ) {
        val rowOffset = row * GridDivisionSize.toPx()
        val columnOffset = column * GridDivisionSize.toPx()
        drawRect(
            color = color,
            topLeft = Offset(columnOffset, rowOffset),
            size = Size(GridDivisionSize.toPx(), GridDivisionSize.toPx()),
            alpha = alpha
        )
    }

    fun DrawScope.drawTileContent(
        tile: GridTileModel,
        columnIndex: Int,
        rowIndex: Int,
    ) {
        if (tile.content.value === null) {
            return
        }

        val lettersPaint = Paint().asFrameworkPaint().apply {
            isAntiAlias = true
            textSize = HeaderTextSize.toPx()
            color = tileTextColor.toArgb()
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        }

        val pointsPaint = Paint().asFrameworkPaint().apply {
            isAntiAlias = true
            textSize = HeaderTextSize.toPx() * 0.5f
            color = tileTextColor.toArgb()
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        }

        val column = columnIndex + 1
        val row = rowIndex + 1

        val tileBackgroundColor =
            if (viewModel.areCoordinatesTransient(TileCoordinates(row, column))) {
                transientTileBackground
            } else {
                tileBackground
            }
        drawTileBackground(tileBackgroundColor, column, row)
        val horizontalOffset = column * GridDivisionSize.toPx()
        val verticalOffset = (row + 1) * GridDivisionSize.toPx()

        drawIntoCanvas {
            it.nativeCanvas.drawText(
                tile.content.value?.displayedLetter?.uppercaseChar().toString(),
                horizontalOffset + DivisionCenterOffset.toPx(),
                verticalOffset - DivisionCenterOffset.toPx(),
                lettersPaint
            )
        }
        drawIntoCanvas {
            it.nativeCanvas.drawText(
                tile.content.value?.points.toString(),
                // WARNING -> TWEAK
                horizontalOffset + 2.2f * DivisionCenterOffset.toPx(),
                verticalOffset - 0.5f * DivisionCenterOffset.toPx(),
                pointsPaint
            )
        }
    }

    fun DrawScope.drawTileHighlight(
        tile: GridTileModel,
        columnIndex: Int,
        rowIndex: Int,
    ) {
        if (!tile.isHighlighted.value) {
            return
        }
        val column = columnIndex + 1
        val row = rowIndex + 1
        val color =
            if (viewModel.canPlaceTile(TileCoordinates(row, column))) {
                Color.Green
            } else {
                Color.Red
            }
        drawTileBackground(color, column, row, HardBackgroundAlpha)
    }

    fun DrawScope.drawTiles() {
        viewModel.board.tileGrid.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { columnIndex, tile ->
                drawTileContent(tile, columnIndex, rowIndex)
                drawTileHighlight(tile, columnIndex, rowIndex)
            }
        }
    }

    fun DrawScope.drawTileMultiplierIndicator(
        column: Int,
        row: Int,
        type: Char,
        value: Int
    ) {
        val horizontalOffset = column * GridDivisionSize.toPx()
        val verticalOffset = (row + 1) * GridDivisionSize.toPx()

        val multiplierPaint = Paint().asFrameworkPaint().apply {
            isAntiAlias = true
            textSize = HeaderTextSize.toPx() * 0.45f
            color = tileTextColor.toArgb()
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            alpha = 150
        }

        val typeLabel =
            if (type == 'l') LetterMultiplierLabel else WordMultiplierLabel
        val valueLabel =
            if (value == 2) DoubleMultiplierLabel else TripleMultiplierLabel

        drawIntoCanvas {
            it.nativeCanvas.drawText(
                typeLabel,
                // WARNING -> TWEAK
                horizontalOffset + (1.75f - 0.27f * typeLabel.length) * DivisionCenterOffset.toPx(),
                verticalOffset - 1.8f * DivisionCenterOffset.toPx(),
                multiplierPaint
            )
        }
        drawIntoCanvas {
            it.nativeCanvas.drawText(
                valueLabel,
                // WARNING -> TWEAK
                horizontalOffset + 1.2f * DivisionCenterOffset.toPx(),
                verticalOffset - 0.7f * DivisionCenterOffset.toPx(),
                multiplierPaint
            )
        }
    }

    fun DrawScope.drawMultipliers() {
        viewModel.board.tileGrid.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { columnIndex, tile ->
                if (tile.letterMultiplier != tile.wordMultiplier) {
                    var color = themeColors[0]
                    var alpha = SoftBackgroundAlpha
                    var type = 'l'
                    var value = 2
                    if (tile.letterMultiplier == 3) {
                        alpha = HardBackgroundAlpha
                        value = 3
                    }
                    else if (tile.wordMultiplier == 2) {
                        color = themeColors[1]
                        type = 'w'
                    }
                    else if (tile.wordMultiplier == 3) {
                        color = themeColors[1]
                        alpha = HardBackgroundAlpha
                        type = 'w'
                        value = 3
                    }
                    drawTileBackground(color, columnIndex + 1, rowIndex + 1, alpha)
                    drawTileMultiplierIndicator(
                        columnIndex + 1,
                        rowIndex + 1,
                        type,
                        value
                    )
                }
            }
        }
    }

    var gridDivisionSize by Delegates.notNull<Float>()
    var boardPadding by Delegates.notNull<Float>()

    Canvas(
        modifier = Modifier
            .size(BoardSize)
            .padding(BoardPadding)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { tapOffset ->
                        viewModel.touchBoard(gridDivisionSize, tapOffset)
                    }
                )
            }
    ) {
        gridDivisionSize = GridDivisionSize.toPx()
        boardPadding = BoardPadding.toPx()
        drawMultipliers()
        drawTiles()
        // drawGrid should be called after all others
        drawGridLayout()

        val boardHoverOffset = boardPadding
        viewModel.hoverBoard(
            gridDivisionSize,
            dragState.currentLocalPosition.minus(
                Offset(boardHoverOffset, boardHoverOffset)
            )
        )
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_C)
@Composable
fun BoardPreview() {
    val v: BoardViewModel = viewModel()
    val d = DragState()
    BoardCanvasView(d, v)
}
