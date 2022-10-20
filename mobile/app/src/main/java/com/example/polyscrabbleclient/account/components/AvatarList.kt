package com.example.polyscrabbleclient.account.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.polyscrabbleclient.getAssetsId
import com.example.polyscrabbleclient.ui.theme.avatars

val defaultAvatarList =
    listOf("elephant", "fox", "hippopotamus", "polarbear", "stag", "panther", "koala")

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AvatarList(
    onAvatarClick: (value: String) -> Unit,
) {
    val selectedIndex = remember { mutableStateOf(-1) }
    Card(
        Modifier
            .padding(horizontal = 50.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                avatars,
                modifier = Modifier.padding(0.dp,10.dp,0.dp,0.dp),
                style = MaterialTheme.typography.subtitle1,
                fontWeight = FontWeight.Bold
            )
            LazyVerticalGrid(
                cells = GridCells.Fixed(4),
                contentPadding = PaddingValues(10.dp)
            ) {
                itemsIndexed(defaultAvatarList) { index, item ->
                    Column {
                        Avatar(
                            modifier = Modifier
                                .height(80.dp)
                                .padding(10.dp)
                                .border(
                                    BorderStroke(
                                        2.dp,
                                        if (selectedIndex.value == index) Color.DarkGray else Color.Transparent
                                    ),
                                    shape = CircleShape
                                )
                                .selectable(
                                    selected = selectedIndex.value == index,
                                    onClick = {
                                        if (selectedIndex.value != index) {
                                            selectedIndex.value = index
                                            onAvatarClick(item)
                                        } else selectedIndex.value = -1
                                    }
                                ),
                            avatarId = getAssetsId(name = item)
                        )
                    }
                }
            }
        }
    }
}

