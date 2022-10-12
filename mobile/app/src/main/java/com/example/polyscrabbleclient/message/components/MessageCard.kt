package com.example.polyscrabbleclient.message.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.polyscrabbleclient.message.model.*
import java.time.format.DateTimeFormatter

@Composable
fun MessageCard(messageInfo: Message) {
    val dateFormatter = DateTimeFormatter.ofPattern("hh:mm:ss")
    val dateStr = messageInfo.date?.format(dateFormatter) ?: ""

    val isMessageFromMe = messageInfo.type == MessageType.ME
    val padding = if (isMessageFromMe)
        Modifier.padding(250.dp, 5.dp, 5.dp, 5.dp)
    else
        Modifier.padding(5.dp, 5.dp, 250.dp, 5.dp)

    Column(modifier = Modifier.padding(horizontal = 5.dp, vertical = 4.dp)) {
        Text(
            text = dateStr,
            modifier = padding,
            style = MaterialTheme.typography.body1.copy(fontSize = 15.sp)
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .then(padding),
            backgroundColor = if (isMessageFromMe) MaterialTheme.colors.secondary
            else MaterialTheme.colors.primary
        ) {
            Row(Modifier.padding(10.dp, 15.dp,15.dp, 15.dp)) {
                Text(
                    text = "${messageInfo.from}: ",

                    style = MaterialTheme.typography.body1.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                )
                Text(
                    text = messageInfo.content,
                    style = MaterialTheme.typography.body1.copy(
                        fontWeight = FontWeight.Normal,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                )
            }
        }
    }
}
