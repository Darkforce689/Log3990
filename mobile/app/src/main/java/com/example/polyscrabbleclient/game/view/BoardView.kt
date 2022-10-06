package com.example.polyscrabbleclient.game.view

import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.polyscrabbleclient.game.domain.MultiplierType
import com.example.polyscrabbleclient.game.domain.Multipliers
import com.example.polyscrabbleclient.game.model.*
import com.example.polyscrabbleclient.game.viewmodels.BoardViewModel

const val GridDimension = BoardDimension + 1
val BoardSize = 300.dp
val GridPadding = 4.dp
val GridSize = BoardSize - GridPadding.times(2)
val GridDivisionSize = GridSize / GridDimension
val HeaderRange = (BoardRange.first+1)..(BoardRange.last+1)
val HeaderTextSize = BoardSize.div(GridDimension).div(1.8f)

@Composable
fun BoardView() {
    val viewModel: BoardViewModel = viewModel()

    val themeColors = listOf(MaterialTheme.colors.primary, MaterialTheme.colors.secondary, MaterialTheme.colors.secondary)
    val thickDividerIndexes = listOf(0,1,GridDimension);

    val rowChars = RowChar.values();
    val rowCharsColor = MaterialTheme.colors.primary;

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
        divisionCenterOffset: Float
    ) {
        val horizontalTextOffset =
            currentDivisionOffset +
            divisionCenterOffset -
            // TODO : WARNING -> TWEAK
            (gridDivisionIndex / 10) * (HeaderTextSize.toPx()/4)
        drawIntoCanvas {
            it.nativeCanvas.drawText(
                gridDivisionIndex.toString(),
                horizontalTextOffset,
                // TODO : WARNING -> TWEAK
                2.4f * divisionCenterOffset,
                headerTextPaint
            )
        }
    }

    fun DrawScope.drawRowHeader(
        gridDivisionIndex: Int,
        currentDivisionOffset: Float,
        headerTextPaint: NativePaint,
        divisionCenterOffset: Float
    ) {
        val rowCharIndex = gridDivisionIndex - 2
        val rowHeaderChar = rowChars[rowCharIndex].toString()
        drawIntoCanvas {
            it.nativeCanvas.drawText(
                rowHeaderChar,
                divisionCenterOffset,
                currentDivisionOffset - divisionCenterOffset,
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

    fun DrawScope.drawGridLayout(headerTextPaint: NativePaint) {
        // TODO : WARNING -> TWEAK
        val divisionCenterOffset = 0.3f * GridDivisionSize.toPx();

        drawGridBackground()

        for (gridDivisionIndex in 0..GridDimension) {
            val strokeWidth =
                if (gridDivisionIndex in thickDividerIndexes)
                    Stroke.DefaultMiter
                else
                    Stroke.HairlineWidth
            val currentDivisionOffset = gridDivisionIndex * GridDivisionSize.toPx()

            drawColumnDivider(currentDivisionOffset, strokeWidth)
            drawRowDivider(currentDivisionOffset, strokeWidth)
            if (gridDivisionIndex in BoardRange) {
                drawColumnHeader(gridDivisionIndex, currentDivisionOffset, headerTextPaint, divisionCenterOffset)
            }
            if (gridDivisionIndex in HeaderRange) {
                drawRowHeader(gridDivisionIndex, currentDivisionOffset, textPaint, divisionCenterOffset)
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

    Canvas(
        modifier = Modifier
            .size(BoardSize)
            .padding(GridPadding)
    ) {
        val headerTextPaint = Paint().asFrameworkPaint().apply {
            isAntiAlias = true
            textSize = HeaderTextSize.toPx()
            color = rowCharsColor.toArgb()
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        }

        drawMultipliers()
        // WARNING -> drawGrid should be called after all others
        drawGridLayout(headerTextPaint)
    }
}


@Preview(showBackground = true)
@Composable
fun BoardPreview() {
    BoardView()
}
