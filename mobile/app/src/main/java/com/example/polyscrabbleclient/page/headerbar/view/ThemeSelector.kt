package com.example.polyscrabbleclient.page.headerbar.view

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.Switch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.polyscrabbleclient.page.headerbar.viewmodels.ThemeSelectorViewModel

@Composable
fun ThemeSelectorView(viewModel: ThemeSelectorViewModel = viewModel()) {
    Row (verticalAlignment = Alignment.CenterVertically){
        Icon(Icons.Filled.LightMode, "Light Mode")
        Switch(
            checked = viewModel.isDarkTheme.value,
            onCheckedChange = { viewModel.toggleTheme() }
        )
        Icon(Icons.Filled.DarkMode, "Dark Mode")
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_C)
@Composable
fun ThemeSelectorPreview() {
    ThemeSelectorView()
}
