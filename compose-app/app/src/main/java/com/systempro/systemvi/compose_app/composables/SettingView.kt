package com.systempro.systemvi.compose_app.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.systempro.systemvi.compose_app.utils.AppState

@Composable fun SettingView() {
    val appState = viewModel<AppState>()
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.Start){
            item {
                ListItem(
                    headlineContent = { Text("Dynamic Colors") },
                    trailingContent = { Switch(
                        checked = appState.useDynamicTheme,
                        onCheckedChange = {appState.useDynamicTheme=it}
                    ) }
                )
                ListItem(
                    headlineContent = { Text("Light Theme") },
                    trailingContent = { Switch(
                        checked = appState.useLightTheme,
                        onCheckedChange = {appState.useLightTheme=it}
                    ) }
                )
            }
        }
    }
}