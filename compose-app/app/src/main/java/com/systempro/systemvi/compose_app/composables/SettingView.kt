package com.systempro.systemvi.compose_app.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable fun SettingView() {
    LazyColumn(verticalArrangement = Arrangement.Top){
        item {
            ListItem(headlineContent = { Text("Mensal") },)
        }
    }
}