package com.example.polyscrabbleclient.message.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.polyscrabbleclient.message.model.Conversation
import com.example.polyscrabbleclient.ui.theme.create_menu_option
import com.example.polyscrabbleclient.ui.theme.delete_menu_option
import com.example.polyscrabbleclient.ui.theme.join_menu_option

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

    var expanded by remember {
        mutableStateOf(false)
    }

    var createModalOpened by remember {
        mutableStateOf(false)
    }

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Box(Modifier.fillMaxWidth(0.95f)) {
            ScrollableTabRow(
                backgroundColor = MaterialTheme.colors.background,
                selectedTabIndex = selectedConvoIndex,
                modifier = Modifier.fillMaxHeight()
            ) {
                conversations.forEachIndexed { index, conversation ->
                    Tab(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(vertical = 5.dp, horizontal = 15.dp),
                        selected = selectedConvoIndex == index,
                        onClick = {
                            onSelectedConvo(index)
                        }
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .width(150.dp),
                        ) {
                            Box(Modifier.fillMaxWidth(0.8f)) {
                                Text(conversation.name, overflow = TextOverflow.Ellipsis, maxLines = 1)
                            }

                            Icon(
                                Icons.Default.Close,
                                null,
                                modifier = Modifier.clickable {
                                    onConvoLeave(index)
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(
            Modifier
                .weight(1f)
                .background(Color.Green))
        Box(modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.TopStart)
        ) {
            Button(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.background),
                onClick = { expanded = true }
            ) {
                Icon(Icons.Default.MoreVert, null)
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(onClick = { /* TODO */ }) {
                    Text(join_menu_option)
                }
                DropdownMenuItem(onClick = { createModalOpened = true }) {
                    Text(create_menu_option)
                }
                DropdownMenuItem(onClick = { /* TODO */ }) {
                    Text(delete_menu_option)
                }
            }
        }

        CreateConversationModal(
            opened = createModalOpened,
            onClose = { createModalOpened = false },
        )
    }
}


@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true)
@Composable
fun ConversationPickerPreview() {
    var state = mutableStateOf(0)
    ConversationPicker(
        conversations = mutableStateListOf<Conversation>(
            Conversation("123", "general"),
            Conversation("123", "asdfsdf"),
            Conversation("123", "asdfasdf")
        ),
        onSelectedConvo = { index -> state.value = index },
        onConvoLeave = {},
        modifier = Modifier.height(50.dp),
        selectedConvoIndex = state.value
    )
}
