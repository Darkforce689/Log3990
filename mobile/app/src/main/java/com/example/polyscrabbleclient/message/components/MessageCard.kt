package com.example.polyscrabbleclient.message.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.polyscrabbleclient.message.model.*

@Composable
fun MessageCard(messageInfo: Message) {
    val isMessageFromMe = messageInfo.type == MessageType.ME
    Column(modifier = Modifier.padding(horizontal = 5.dp, vertical = 4.dp)) {
    var padding = if(isMessageFromMe)
        Modifier.padding(250.dp, 5.dp, 5.dp, 5.dp)
    else
        Modifier.padding(5.dp, 5.dp, 250.dp, 5.dp)

    Text(
        text = messageInfo.from,
        modifier = padding,
        style = MaterialTheme.typography.body1.copy(fontSize = 15.sp)
    )
    Card(modifier = Modifier
        .fillMaxWidth()
        .then(padding),
        backgroundColor = if(isMessageFromMe) MaterialTheme.colors.primary
        else MaterialTheme.colors.secondary) {
            Text(
                text = messageInfo.content,
                modifier = Modifier.padding(15.dp),
                style = MaterialTheme.typography.body1.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    color = Color.White
                )
            )
        }
    }
}
