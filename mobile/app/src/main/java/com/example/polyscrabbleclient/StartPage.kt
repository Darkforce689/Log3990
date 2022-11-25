package com.example.polyscrabbleclient

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.example.polyscrabbleclient.auth.model.AuthError
import com.example.polyscrabbleclient.lobby.view.NewGameScreen
import com.example.polyscrabbleclient.lobby.viewmodels.CreateGameViewModel
import com.example.polyscrabbleclient.ui.theme.disconnection
import com.example.polyscrabbleclient.ui.theme.gamePage
import com.example.polyscrabbleclient.ui.theme.new_game
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
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        NewGameScreen(navController, CreateGameViewModel())
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
