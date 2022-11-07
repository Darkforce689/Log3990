package com.example.polyscrabbleclient.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class FlexArrangement {
    Horizontal,
    Vertical
}

@Composable
fun <T> FlexedSquaredContainer (
    flexArrangement: FlexArrangement = FlexArrangement.Horizontal,
    forceAllSameSize: Boolean = false,
    contentCount: Int,
    contents: List<T>,
    size: Dp = 270.dp,
    view: @Composable (size: Dp, content: T) -> Unit
) {
    val chunksOfContent = contents.chunked(contentCount)
    if (flexArrangement == FlexArrangement.Horizontal) {
        ColumnedFlexRow(chunksOfContent, forceAllSameSize, size, view)
    } else {
        RowedFlexColumn(chunksOfContent, forceAllSameSize, size, view)
    }
}

@Composable
private fun <T> ColumnedFlexRow(
    chunksOfContent: List<List<T>>,
    forceAllSameSize: Boolean,
    size: Dp,
    view: @Composable (size: Dp, content: T) -> Unit
) {
    Column {
        chunksOfContent.forEach { subContent ->
            val numberOfViews = if (forceAllSameSize) chunksOfContent[0].size else subContent.size
            FlexedRow(size.div(numberOfViews), subContent, view)
        }
    }
}

@Composable
private fun <T> RowedFlexColumn(
    chunksOfContent: List<List<T>>,
    forceAllSameSize: Boolean,
    size: Dp,
    view: @Composable (size: Dp, content: T) -> Unit
) {
    Row {
        chunksOfContent.forEach { subContent ->
            val numberOfViews = if (forceAllSameSize) chunksOfContent[0].size else subContent.size
            FlexedColumn(size.div(numberOfViews), subContent, view)
        }
    }
}

@Composable
fun <T> FlexedRow (
    size: Dp,
    contents: List<T>,
    view: @Composable (size: Dp, content: T) -> Unit,
) {
    Row {
        contents.forEach {
            view(size, it)
        }
    }
}

@Composable
fun <T> FlexedColumn (
    size: Dp,
    contents: List<T>,
    view: @Composable (size: Dp, content: T) -> Unit,
) {
    Column {
        contents.forEach {
            view(size, it)
        }
    }
}
@Preview(showBackground = true, device = Devices.PIXEL_C)
@Composable
fun FlexSquaredContainerPreview() {
    val contents = listOf('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K')
    val view: @Composable (Dp, Char) -> Unit = { size, content ->
        Button(modifier = Modifier.width(size), onClick = {}) {
            Text(text = content.toString())
        }
    }
    val contentCount = 3

    FlexedSquaredContainer(
        contentCount = contentCount,
        contents = contents,
        view = view,
    )
}

@Preview(showBackground = true, device = Devices.PIXEL_C)
@Composable
fun FlexForcedSquaredContainerPreview() {
    val contents = listOf('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K')
    val view: @Composable (Dp, Char) -> Unit = { size, content ->
        Button(modifier = Modifier.width(size), onClick = {}) {
            Text(text = content.toString())
        }
    }
    val contentCount = 3

    FlexedSquaredContainer(
        forceAllSameSize = true,
        contentCount = contentCount,
        contents = contents,
        view = view,
    )
}

@Preview(showBackground = true, device = Devices.PIXEL_C)
@Composable
fun VerticalFlexSquaredContainerPreview() {
    val contents = listOf('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K')
    val view: @Composable (Dp, Char) -> Unit = { size, content ->
        Button(modifier = Modifier.width(size), onClick = {}) {
            Text(text = content.toString())
        }
    }
    val contentCount = 4

    FlexedSquaredContainer(
        flexArrangement = FlexArrangement.Vertical,
        forceAllSameSize = true,
        contentCount = contentCount,
        contents = contents,
        view = view,
    )
}
