package com.example.polyscrabbleclient.account.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource


@Composable
fun Avatar(modifier: Modifier, avatarId: Int) {
    Image(
        modifier = modifier,
        painter = painterResource(id = avatarId),
        contentDescription = "Avatar",
    )
}
