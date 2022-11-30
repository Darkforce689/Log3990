package com.example.polyscrabbleclient.account.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.polyscrabbleclient.NavPage
import com.example.polyscrabbleclient.account.model.AccountPage
import com.example.polyscrabbleclient.account.viewmodel.AccountViewmodel
import com.example.polyscrabbleclient.account.viewmodel.GamesHistoryViewModel
import com.example.polyscrabbleclient.getAssetsId
import com.example.polyscrabbleclient.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AccountView(viewmodel: AccountViewmodel, navController: NavController) {
    val inputFocusRequester = LocalFocusManager.current
    val selectedPage = remember { mutableStateOf(AccountPage.Profil.name) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    if (viewmodel.updateSuccessful.value) {
        viewmodel.updateSuccessful.value = false
        LaunchedEffect("") {
            scope.launch {
                snackbarHostState.showSnackbar(
                    "Modification réussie!",
                    duration = SnackbarDuration.Short
                )
            }
        }
    }
    Row(Modifier.clickable { inputFocusRequester.clearFocus() }) {
        Surface {
            AccountSideNavigation(
                name = viewmodel.userName.value,
                avatar = viewmodel.avatar.value,
                navController = navController,
                onSelected = { page: String -> selectedPage.value = page }
            )
            Divider(
                Modifier
                    .fillMaxHeight()
                    .width(1.dp)
            )
        }
        when (selectedPage.value) {
            AccountPage.Profil.name -> {
                AccountContent(my_profil) {
                    ProfilContent(
                        fields = viewmodel.userInfoField.value,
                        usernameError = viewmodel.usernameError.value,
                        updateUsername = { name -> viewmodel.updateUsername(name) },
                        validateUsername = { name -> viewmodel.validateUsername(name) },
                        updateInfoRequest = { viewmodel.updateInfoRequest() },
                        updateAvatar = { avatarName -> viewmodel.updateAvatar(avatarName) }
                    )
                }
            }
            AccountPage.Statistics.name -> {
                AccountContent(statistics) {
                    UserStatistics()
                }
            }
            AccountPage.Games.name -> {
                AccountContent(game_history) {
                    GameHistoryView(GamesHistoryViewModel())
                }
            }
        }
        SnackbarHost(
            modifier = Modifier.fillMaxWidth(),
            hostState = snackbarHostState,
            snackbar = { snackbarData: SnackbarData ->
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(2.dp, Color.White),
                        modifier = Modifier
                            .padding(25.dp)
                            .wrapContentSize()
                    ) {
                        Text(text = snackbarData.message, modifier = Modifier.padding(8.dp))
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun AccountSideNavigation(
    name: String,
    avatar: String,
    navController: NavController,
    onSelected: (page: String) -> Unit,
) {
    val sideNavList = listOf(
        Triple(my_profil, Icons.Filled.AccountCircle, AccountPage.Profil.name),
        Triple(my_statistics, Icons.Filled.Numbers, AccountPage.Statistics.name),
        Triple(my_games, Icons.Filled.Analytics, AccountPage.Games.name)
    )
    val keyboard = LocalSoftwareKeyboardController.current
    Column(
        modifier = Modifier
            .fillMaxWidth(0.2f)
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(imageVector = Icons.Default.ArrowBack,
            contentDescription = null,
            modifier = Modifier
                .padding(10.dp, 10.dp, 0.dp, 0.dp)
                .align(Alignment.Start)
                .clickable {
                    navController.navigate(NavPage.MainPage.label) {
                        keyboard?.hide()
                        popUpTo(NavPage.Account.label) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
        )
        Avatar(Modifier.padding(vertical = 25.dp), getAssetsId(avatar))
        Text(text = name, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(15.dp))
        Divider(Modifier.fillMaxWidth(0.8f))
        Spacer(Modifier.height(20.dp))
        SideNavigation(
            navigationList = sideNavList
        ) { page -> onSelected(page) }
    }
}

@Composable
fun SideNavigation(
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp),
    navigationList: List<Triple<String, ImageVector, String>>,
    onSelected: (index: String) -> Unit
) {
    val clickedIndex = remember { mutableStateOf(0) }
    Column(
        modifier
    ) {
        navigationList.forEachIndexed { index, triple ->
            val label = triple.first
            val imageVector = triple.second
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(if (clickedIndex.value == index) Color.LightGray else Color.Transparent)
                    .padding(10.dp)
                    .clickable {
                        onSelected(triple.third)
                        clickedIndex.value = index
                    }
            ) {
                Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                    Icon(imageVector, contentDescription = null)
                    Text(text = label)
                }
            }
            Spacer(modifier = Modifier.height(15.dp))
        }
    }
}

@Composable
fun AccountContent(title: String, content: @Composable () -> Unit) {
    Column(
        Modifier
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        Text(
            title,
            style = MaterialTheme.typography.h4,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxHeight(0.22f)
                .padding(vertical = 50.dp, horizontal = 20.dp)
        )
        content()
    }
}
