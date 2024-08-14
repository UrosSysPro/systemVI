package com.systempro.systemvi.compose_app.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.systempro.systemvi.compose_app.utils.Logger

@Composable fun AboutView() {
    DisposableEffect(Unit){
        Logger.debug("enter about page")
        onDispose {
            Logger.debug("leave about page")
        }
    }
    Box(modifier = Modifier.fillMaxSize()){
        LazyColumn(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxSize()
        ){
            for(i in 0 until 100)item {
                Text("item $i")
            }
        }
    }
}