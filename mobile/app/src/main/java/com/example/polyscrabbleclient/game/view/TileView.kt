package com.example.polyscrabbleclient.game.view

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.example.polyscrabbleclient.game.model.TileModel
import com.example.polyscrabbleclient.ui.theme.grayedOutTileBackground
import com.example.polyscrabbleclient.ui.theme.tileBackground

val subscript = SpanStyle(
    baselineShift = BaselineShift.Subscript,
    fontSize = 16.sp,
)

@OptIn(ExperimentalUnitApi::class)
@Composable
fun TileView(
    tileModel: TileModel,
    displayPoint: Boolean = true,
    size: Dp = 60.dp,
    select: () -> Unit,
) {
    val targetColor by animateColorAsState(
        targetValue =
        if (tileModel.isSelected.value)
            MaterialTheme.colors.secondary
        else
            MaterialTheme.colors.primary,
        animationSpec = tween(durationMillis = 200)
    )

    Surface(
        color =
            if (tileModel.isUsedOnBoard.value) {
                MaterialTheme.colors.grayedOutTileBackground
            } else {
                MaterialTheme.colors.tileBackground
            },
        modifier = Modifier
            .selectable(
                selected = tileModel.isSelected.value,
                onClick = select
            )
            .size(size)
            .border(width = 4.dp, targetColor)
    ) {
        val paddingTop = if (displayPoint) 13.dp else 18.dp

        Text(modifier = Modifier
            .padding(top = paddingTop),
            style = TextStyle(
                textAlign = TextAlign.Center,
                fontSize = TextUnit(18f, TextUnitType.Sp),
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            ),
            text = tileInternalContent(tileModel, displayPoint)
        )
    }
}

@Composable
private fun tileInternalContent(
    tileModel: TileModel,
    displayPoint: Boolean
) = buildAnnotatedString {
    append(tileModel.letter.uppercaseChar())
    if (displayPoint) {
        withStyle(subscript) {
            append(tileModel.points.toString())
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NormalTilePreview() {
    val tileModel = TileModel('I', 1)
    TileView(
        tileModel,
        select = { tileModel.isSelected.value = !tileModel.isSelected.value }
    )
}

@Preview(showBackground = true)
@Composable
fun SelectedTilePreview() {
    val tileModel = TileModel('B', 2)
    tileModel.isSelected.value = true
    TileView(
        tileModel,
        select = { tileModel.isSelected.value = !tileModel.isSelected.value }
    )
}

@Preview(showBackground = true)
@Composable
fun OnBoardTilePreview() {
    val tileModel = TileModel('C', 10)
    tileModel.isUsedOnBoard.value = true
    TileView(
        tileModel,
        select = { tileModel.isSelected.value = !tileModel.isSelected.value }
    )
}

@Preview(showBackground = true)
@Composable
fun TilePreviewWithoutPoints() {
    val tileModel = TileModel('D', 0)
    TileView(
        tileModel,
        select = { tileModel.isSelected.value = !tileModel.isSelected.value },
        displayPoint = false
    )
}
