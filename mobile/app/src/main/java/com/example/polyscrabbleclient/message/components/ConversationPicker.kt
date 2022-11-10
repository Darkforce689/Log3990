package com.example.polyscrabbleclient.message.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.polyscrabbleclient.message.model.Conversation


@Composable()
fun ConversationPicker(
    conversations: SnapshotStateList<Conversation>,
    selectedConvoIndex: Int,
    onSelectedConvo: (Int) -> Unit,
    onConvoLeave: (Int) -> Unit,
    modifier: Modifier,
) {
    if (conversations.size === 0) {
        return
    }

    ScrollableTabRow(
        backgroundColor = MaterialTheme.colors.background,
        selectedTabIndex = selectedConvoIndex,
        modifier = modifier
    ) {
        conversations.forEachIndexed { index, conversation ->
            Tab(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(10.dp),
                selected = selectedConvoIndex === index,
                onClick = {
                    onSelectedConvo(index)
                }
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(conversation.name)
                }
            }
        }
    }
}


@SuppressLint("UnrememberedMutableState")
@Preview()
@Composable
fun ConversationPickerPreview() {
    var state = mutableStateOf(0)
    ConversationPicker(
        conversations = mutableStateListOf<Conversation>(
            Conversation("123", "general"),
            Conversation("123", "asdfsdfsdf"),
            Conversation("123", "asdfasdf")
        ),
        onSelectedConvo = { index -> state.value = index },
        onConvoLeave = {},
        modifier = Modifier.height(50.dp),
        selectedConvoIndex = state.value
    )
}
