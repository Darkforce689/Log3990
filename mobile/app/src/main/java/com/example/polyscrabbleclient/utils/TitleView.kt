package com.example.polyscrabbleclient.utils

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun TitleView(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        fontStyle = MaterialTheme.typography.h1.fontStyle,
        fontWeight = FontWeight.Bold,
        fontSize = 25.sp,
        modifier = modifier
    )
}

@Composable
fun SubTitleView(
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = subtitle,
        fontStyle = MaterialTheme.typography.h1.fontStyle,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        modifier = modifier
    )
}

@Composable
fun TextView(
    text: String,
    fontSize: TextUnit = 14.sp,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        fontStyle = MaterialTheme.typography.h1.fontStyle,
        fontWeight = FontWeight.Bold,
        fontSize = fontSize,
        modifier = modifier
    )
}
