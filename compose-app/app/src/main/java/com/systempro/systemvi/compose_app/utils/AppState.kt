package com.systempro.systemvi.compose_app.utils
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.ktor.client.*
import io.ktor.client.engine.cio.*

class AppState : ViewModel() {
    val httpClient: HttpClient = HttpClient(CIO)
    var useLightTheme by mutableStateOf(false)
    var useDynamicTheme by mutableStateOf(false)

}