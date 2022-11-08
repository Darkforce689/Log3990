package com.example.polyscrabbleclient.account.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.polyscrabbleclient.account.model.ConnectionLog
import com.example.polyscrabbleclient.account.model.LogType
import com.example.polyscrabbleclient.ui.theme.connection
import com.example.polyscrabbleclient.ui.theme.disconnection
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun Log(data: ConnectionLog, columnWeights: List<Float>) {
    val simpleDateFormat = SimpleDateFormat("MMM d, y, h:mm:ss a", Locale.CANADA)
    val dateFormatted = simpleDateFormat.format(data.date.toLong())

    val fields = listOf(
        dateFormatted,
        if (data.type == LogType.CONNECTION.label) connection else disconnection
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        fields.forEachIndexed { index, field ->
            ConnectionCell(text = field, weight = columnWeights[index], Color.LightGray)
        }
    }
}

@Composable
fun RowScope.ConnectionCell(
    text: String,
    weight: Float,
    color: Color
) {
    Box(
        Modifier
            .border(1.dp, color)
            .weight(weight)
            .padding(10.dp, 10.dp)
    ) {
        Text(text = text)
    }
}

@Preview(showBackground = true)
@Composable
fun UserPreview() {
    Log(data = ConnectionLog("", "", "1667092129854", LogType.CONNECTION.label), listOf(0.5f, 0.5f))
}
