package com.example.polyscrabbleclient.page.headerbar.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class ThemeSelectorViewModel : ViewModel() {

    var isDarkTheme: MutableState<Boolean> = mutableStateOf(false)

    fun toggleTheme() {
        isDarkTheme.value = !isDarkTheme.value
    }
}
