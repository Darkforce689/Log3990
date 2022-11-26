package com.example.polyscrabbleclient.account.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.polyscrabbleclient.getAssetsId
import com.example.polyscrabbleclient.ui.theme.avatars
import com.example.polyscrabbleclient.user.User
import java.util.concurrent.locks.Lock

val defaultAvatarList =
    listOf("elephant", "fox", "hippopotamus", "polarbear", "stag", "lynx")
val extendedAvatarList =
    listOf("koala", "bear", "frog", "giraffe", "hippo", "lion", "monkey", "owl", "raccoon", "eagle", "wolf")
val unlockLevel = listOf(0,0,0,0,0,0,1,1,2,3,4,5,6,7,8,9,10)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AvatarList(
    onAvatarClick: (value: String) -> Unit,
) {
    val selectedIndex = remember { mutableStateOf(-1) }
    Card(
        Modifier
            .fillMaxHeight(0.51f)
            .fillMaxWidth(1f)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                avatars,
                modifier = Modifier.padding(0.dp, 10.dp, 0.dp, 0.dp),
                style = MaterialTheme.typography.subtitle1,
                fontWeight = FontWeight.Bold
            )
            LazyVerticalGrid(
                cells = GridCells.Fixed(6),
                contentPadding = PaddingValues(10.dp)
            ) {
                itemsIndexed(defaultAvatarList) { index, item ->
                    Column {
                        Avatar(
                            modifier = Modifier
                                .height(60.dp)
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
                itemsIndexed(extendedAvatarList) { index, item ->
                    Column {
                        Avatar(
                            modifier = Modifier
                                .alpha(if (User.currentLevel() >= unlockLevel[defaultAvatarList.size + index]) 1f else 0.2f)

                                .background(color = if (User.currentLevel() >= unlockLevel[defaultAvatarList.size + index]) Color.Transparent else Color.Gray, shape = CircleShape)
                                .height(60.dp)
                                .padding(10.dp)
                                .border(
                                    BorderStroke(
                                        2.dp,
                                        if (selectedIndex.value == defaultAvatarList.size + index) Color.DarkGray else Color.Transparent
                                    ),
                                    shape = CircleShape
                                )
                                .selectable(
                                    enabled =
                                    User.currentLevel() >= unlockLevel[defaultAvatarList.size + index],
                                    selected = selectedIndex.value == defaultAvatarList.size + index,
                                    onClick = {
                                        if (selectedIndex.value != defaultAvatarList.size + index) {
                                            selectedIndex.value = defaultAvatarList.size + index
                                            onAvatarClick(item)
                                        } else selectedIndex.value = -1
                                    }
                                ),
                            avatarId = getAssetsId(name = item)
                        )
                        if (User.currentLevel() < unlockLevel[defaultAvatarList.size + index]) {
                            Row() {
                                Icon(imageVector = Icons.Rounded.Lock, contentDescription = null, modifier = Modifier.offset((20).dp,(-43).dp).height(15.dp))
                                Text("Niveau: " + unlockLevel[defaultAvatarList.size + index], modifier = Modifier.offset((-9).dp, (-28).dp).height(15.dp), fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NewAccountAvatarList(
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
                modifier = Modifier.padding(0.dp, 10.dp, 0.dp, 0.dp),
                style = MaterialTheme.typography.subtitle1,
                fontWeight = FontWeight.Bold
            )
            LazyVerticalGrid(
                cells = GridCells.Fixed(3),
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

