package com.example.polyscrabbleclient.game.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.polyscrabbleclient.game.model.BoardDimension
import com.example.polyscrabbleclient.game.viewmodels.BoardViewModel

const val GridDimension = BoardDimension + 1
// TODO : CHANGE DP <-> Float
const val GridSize = 740f
const val GridDivisionSize = GridSize / GridDimension

@Composable
fun BoardView () {
    val viewModel: BoardViewModel = viewModel()

    val themeColors = listOf(MaterialTheme.colors.primary, MaterialTheme.colors.secondary, MaterialTheme.colors.secondary)
    val thickDividerIndexes = listOf(0,1,GridDimension);

    Canvas(
        modifier = Modifier
            // TODO : CHANGE DP <-> Float
            .size(300.dp)
            .padding(16.dp)
    ) {
        for(gridDivisionIndex in 0..GridDimension)  {
            val strokeWidth =
                if(gridDivisionIndex in thickDividerIndexes)
                    Stroke.DefaultMiter
                else
                    Stroke.HairlineWidth
            val gridDivisionOffset = gridDivisionIndex * GridDivisionSize
            drawLine(
                brush = Brush.linearGradient(colors = themeColors),
                start = Offset(gridDivisionOffset, 0f),
                end = Offset(gridDivisionOffset, GridSize),
                strokeWidth = strokeWidth
            )
            drawLine(
                brush = Brush.linearGradient(colors = themeColors),
                start = Offset(0f, gridDivisionOffset),
                end = Offset(GridSize, gridDivisionOffset),
                strokeWidth = strokeWidth
            )

        }
    }
}


@Preview(showBackground = true)
@Composable
fun BoardPreview() {
    BoardView()
}
