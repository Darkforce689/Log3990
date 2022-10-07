package com.example.polyscrabbleclient.game.view

import android.graphics.Typeface
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.polyscrabbleclient.game.domain.MultiplierType
import com.example.polyscrabbleclient.game.domain.Multipliers
import com.example.polyscrabbleclient.game.model.*
import com.example.polyscrabbleclient.game.viewmodels.BoardViewModel
import com.example.polyscrabbleclient.ui.theme.TileBackgroundColor

const val GridDimension = BoardDimension + 1
val BoardSize = 550.dp
val GridPadding = 10.dp
val GridSize = BoardSize - GridPadding.times(2)
val GridDivisionSize = GridSize / GridDimension
val HeaderRange = (BoardRange.first+1)..(BoardRange.last+1)
val HeaderTextSize = BoardSize.div(GridDimension).div(1.8f)
val DivisionCenterOffset = GridDivisionSize.times(0.3f);


@Composable
fun BoardView() {
    val viewModel: BoardViewModel = viewModel()

    val themeColors = listOf(MaterialTheme.colors.primary, MaterialTheme.colors.secondary, MaterialTheme.colors.secondary)
    val thickDividerIndexes = listOf(0,1,GridDimension);

    val rowChars = RowChar.values();
    val rowCharsColor = MaterialTheme.colors.primary;
    val tileTextColor = MaterialTheme.colors.onBackground;

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
            // TODO : WARNING -> TWEAK
            (gridDivisionIndex / 10) * (HeaderTextSize.toPx() / 3)
        drawIntoCanvas {
            it.nativeCanvas.drawText(
                gridDivisionIndex.toString(),
                horizontalTextOffset,
                // TODO : WARNING -> TWEAK
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

        drawTileBackground(TileBackgroundColor, column, row)
        val horizontalOffset = column * GridDivisionSize.toPx();
        val verticalOffset = (row + 1) * GridDivisionSize.toPx();
        drawIntoCanvas {
            it.nativeCanvas.drawText(
                tile.content.value?.letter.toString(),
                horizontalOffset + DivisionCenterOffset.toPx(),
                verticalOffset - DivisionCenterOffset.toPx(),
                lettersPaint
            )
        }
        drawIntoCanvas {
            it.nativeCanvas.drawText(
                tile.content.value?.points.toString(),
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
        drawTileBackground(Color.Green, column, row, 0.7f)
    }

    fun DrawScope.drawTiles() {
        viewModel.board.tileGrid.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { columnIndex, tile ->
                drawTileContent(tile, columnIndex, rowIndex)
                drawTileHighlight(tile, columnIndex, rowIndex)
            }
        }
    }

    fun DrawScope.drawMultipliers() {
        for (multiplier in Multipliers) {
            val color =
                if (multiplier.type === MultiplierType.Letter) themeColors[0] else themeColors[1]
            val alpha = if (multiplier.value == 3) 0.7f else 0.2f
            drawTileBackground(color, multiplier.column, multiplier.row.ordinal + 1, alpha)
        }
    }

    lateinit var localDimensions: LocalDimensions

    Canvas(
        modifier = Modifier
            .size(BoardSize)
            .padding(BoardPadding)
            .pointerInput(Unit) {
                detectTapGestures (
                    onTap = { tapOffset ->
                        viewModel.touchBoard(localDimensions, tapOffset)
                    }
                )
            }
    ) {
        localDimensions = LocalDimensions(BoardSize.toPx(), BoardPadding.toPx(), GridDivisionSize.toPx())
        drawMultipliers()
        drawTiles()
        // WARNING -> drawGrid should be called after all others
        drawGridLayout()
    }

    viewModel.updateBoard()
}
}
