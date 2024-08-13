package com.systempro.systemvi.compose_app.composables

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.systempro.systemvi.compose_app.utils.Logger

@Composable fun AboutView() {
    DisposableEffect(Unit){
        Logger.debug("enter about page")
        onDispose {
            Logger.debug("leave about page")
        }
    }
    LazyColumn(){
        item {
            Text("About page")
        }
    }
}