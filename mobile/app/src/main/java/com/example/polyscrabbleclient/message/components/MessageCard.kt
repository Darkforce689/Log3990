package com.example.polyscrabbleclient.message.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.polyscrabbleclient.message.model.Message
import com.example.polyscrabbleclient.message.model.MessageType
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun MessageCard(messageInfo: Message) {
    val dateFormatter = DateTimeFormatter.ofPattern("MMM d, y, HH:mm")
    val dateStr = messageInfo.date?.format(dateFormatter) ?: ""

    val isMessageFromMe = messageInfo.type == MessageType.ME

    @Composable
    fun getColor(messageType: MessageType): Color {
        return when (messageType) {
            MessageType.ME -> MaterialTheme.colors.primary;
            MessageType.OTHER -> MaterialTheme.colors.secondary;
            MessageType.SYSTEM -> Color.Gray // TODO COLOR UNIFORMITY THEME
            MessageType.ERROR -> MaterialTheme.colors.error // TODO COLOR UNIFORMITY THEME
        }
    }

    @Composable
    fun getFontColor(messageType: MessageType): Color {
        return when (messageType) {
            MessageType.ME -> MaterialTheme.colors.onPrimary;
            MessageType.OTHER -> MaterialTheme.colors.onSecondary;
            MessageType.SYSTEM -> Color.White
            MessageType.ERROR -> Color.White
        }
    }

    Column(modifier = Modifier.padding(horizontal = 5.dp, vertical = 4.dp)) {
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = dateStr,
                modifier = Modifier.padding(5.dp),
                style = MaterialTheme.typography.body1.copy(fontSize = 15.sp)
            )
        }

        Row {
            if (isMessageFromMe) {
                Spacer(modifier = Modifier.weight(1f))
            }
            Card(
                modifier = Modifier
                    .widthIn(0.dp, 750.dp)
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                shape = RoundedCornerShape(25.dp),
                backgroundColor = getColor(messageType = messageInfo.type),
            ) {
                Row(Modifier.padding(horizontal = 17.dp, vertical = 12.dp)) {
                    Text(
                        text = "${messageInfo.from}: ",

                        style = MaterialTheme.typography.body1.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = getFontColor(messageType = messageInfo.type)
                        )
                    )
                    Text(
                        text = messageInfo.content,
                        style = MaterialTheme.typography.body1.copy(
                            fontWeight = FontWeight.Normal,
                            fontSize = 20.sp,
                            color = getFontColor(messageType = messageInfo.type)
                        )
                    )
                }
                if (!isMessageFromMe) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun MessageCardPreview() {
    MessageCard(messageInfo = Message(content = "salut je suis olivier", from = "olivier1", date = ZonedDateTime.now(), type = MessageType.ME, conversation = "abcd"))
}
