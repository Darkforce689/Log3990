package com.example.polyscrabbleclient.account.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.example.polyscrabbleclient.account.model.ConnectionLog
import com.example.polyscrabbleclient.account.viewmodel.*
import com.example.polyscrabbleclient.round
import com.example.polyscrabbleclient.ui.theme.*
import com.example.polyscrabbleclient.user.User
import kotlin.math.floor

@Preview(showBackground = true)
@Composable
fun Preview() {
    UserStatistics()
}

@Composable
fun UserStatistics(viewModel: StatisticsViewModel = StatisticsViewModel()) {
    val connectionHistory = viewModel.logsPager.collectAsLazyPagingItems()
    Row(
        Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.45f)
                .fillMaxHeight(0.6f)
                .padding(25.dp)
        ) {
            StatisticsContent(game_statistics) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(25.dp, 0.dp),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(text = "$gamePlayed : ${User.nGamePlayed.toInt()}")
                    Text(text = "$gameWinned : ${User.nGameWinned.toInt()}")
                    Text(text = "$averagePointsPerGame : ${User.averagePoints.round(2)}")
                    Text(text = "$averageTimePerGame : ${formatTime(User.averageTimePerGame)}")
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
            ) {
                StatisticsContent(title = connection_history) {
                    ConnectionLogs(connectionHistory)
                }
            }
        }
    }
}

@Composable
private fun ConnectionLogs(userLogs: LazyPagingItems<ConnectionLog>) {
    val columnsWeights = listOf(0.5f, 0.5f)
    val columnsHeaders = listOf(date, connection_type)
    Column(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(25.dp, 0.dp)
    ) {
        Row {
            columnsHeaders.forEachIndexed { index, header ->
                ConnectionCell(text = header, weight = columnsWeights[index], Color.Black)
            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(userLogs) { log ->
                log?.let {
                    Log(data = it, columnWeights = columnsWeights)
                }
            }
        }
    }
}

@Composable
fun StatisticsContent(title: String, content: @Composable () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.subtitle1,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(0.dp, 10.dp)
        )
        Divider(
            Modifier
                .fillMaxWidth(0.8f)
                .padding(0.dp, 0.dp, 0.dp, 10.dp)
        )
        content()
    }
}

fun formatTime(time: Double): String {
    val totalSeconds = time / MILLI_TO_SECONDS
    val hours = (floor(totalSeconds / SECONDS_TO_HOUR)).toInt()
    val minutes = (floor((totalSeconds - hours * SECONDS_TO_HOUR) / MIN_IN_HOUR)).toInt()
    val seconds = (totalSeconds - hours * SECONDS_TO_HOUR - minutes * SEC_IN_MIN).toInt()
    val hoursDisplay = if (hours < TIME_BASE) "0${hours}h " else "${hours}h "
    val minutesDisplay = if (minutes < TIME_BASE) "0${minutes}m " else "${minutes}m "
    val secondsDisplay = if (seconds < TIME_BASE) "0${seconds}s" else "${seconds}s"
    return hoursDisplay + minutesDisplay + secondsDisplay
}
