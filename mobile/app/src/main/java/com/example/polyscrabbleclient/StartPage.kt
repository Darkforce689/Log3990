package com.example.polyscrabbleclient

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.example.polyscrabbleclient.auth.model.AuthError
import com.example.polyscrabbleclient.game.domain.TileCreator
import com.example.polyscrabbleclient.game.view.TileView
import com.example.polyscrabbleclient.lobby.view.NewGameScreen
import com.example.polyscrabbleclient.lobby.viewmodels.CreateGameViewModel
import com.example.polyscrabbleclient.ui.theme.*
import com.example.polyscrabbleclient.user.User
import com.example.polyscrabbleclient.utils.PhysicalButtons
import com.example.polyscrabbleclient.utils.httprequests.ScrabbleHttpClient
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.URL

@Composable
fun StartView(
    navController: NavController,
    startViewModel: StartViewModel,
) {
    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_STOP -> {
                Log.d("Lifecycle", "OnStop View")
            }
            Lifecycle.Event.ON_START -> {
                Log.d("Lifecycle", "OnStart View")
                try {
                    val thread = Thread {
                        val response = ScrabbleHttpClient.get(
                            URL(BuildConfig.COMMUNICATION_URL + "/auth/validatesession"),
                            ValidationResponse::class.java
                        )
                        if (response == null) { // No response from server
                            startViewModel.disconnect(navController)
                            return@Thread
                        }
                        if (response.errors == null) {  // No errors = validation good
                            return@Thread
                        }

                        if (AuthError.find(response.errors[0]) != null) { // errors = validation bad
                            startViewModel.disconnect(navController)
                        }
                    }
                    thread.start()
                    thread.join()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            else -> {}
        }
    }
    PhysicalButtons.reset()
    Box() {
        WordOfTheDay(startViewModel = startViewModel)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            NewGameScreen(navController, CreateGameViewModel())
    }
    }
}

@Composable
fun OnLifecycleEvent(onEvent: (owner: LifecycleOwner, event: Lifecycle.Event) -> Unit) {
    val eventHandler = rememberUpdatedState(onEvent)
    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)

    DisposableEffect(lifecycleOwner.value) {
        val lifecycle = lifecycleOwner.value.lifecycle
        val observer = LifecycleEventObserver { owner, event ->
            eventHandler.value(owner, event)
        }

        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun Prototype(navController: NavController) {
    Button(modifier = Modifier.padding(20.dp),
        onClick = {
            GlobalScope.launch {
                val updateThread = User.updateUser()
                updateThread.start()
                updateThread.join()
            }
            navController.navigate(NavPage.Room.label) {
                popUpTo(NavPage.MainPage.label) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }) {
        Text(text = "Prototype")
    }
}

@Composable
fun Disconnection(navController: NavController, startViewModel: StartViewModel) {
    Button(modifier = Modifier.padding(20.dp),
        onClick = {
            startViewModel.disconnect(navController)
        }) {
        Text(text = disconnection)
    }
}

@Composable
fun GamePage(navController: NavController) {
    Button(
        modifier = Modifier.padding(20.dp),
        onClick = {
            navController.navigate(NavPage.GamePage.label) {
                popUpTo(NavPage.MainPage.label) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    )
    {
        Text(text = gamePage)
    }
}

@Composable
fun NewGamePage(navController: NavController) {
    Button(
        modifier = Modifier.padding(20.dp),
        onClick = {
            navController.navigate(NavPage.NewGame.label) {
                launchSingleTop = true
            }
        }
    )
    {
        Text(text = new_game)
    }
}

@Composable
fun WordOfTheDay(startViewModel: StartViewModel) {
    var wordInfo = WordOfTheDayInfo()
    startViewModel.getWordOfTheDay { wordInfo = it }
    val tiles = wordInfo.word.map { TileCreator.createTileFromLetter(it) }
    Card(
        modifier = Modifier
            .padding(20.dp)
            .widthIn(200.dp, 400.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(start = 5.dp, top = 5.dp, end = 10.dp, bottom = 10.dp)
        ) {
            Text(word_of_the_day_title)

            Row(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                tiles.forEach {
                    TileView(it, false, 35.dp, 14.sp) {}
                }
            }
            Text(points + "${wordInfo.points}")
            Text(definition + wordInfo.definition, lineHeight = 25.sp)
        }
    }
}


