package com.systempro.systemvi.compose_app

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.ktor.client.*
import io.ktor.client.engine.cio.*

class AppState : ViewModel() {
    var counter by mutableIntStateOf(0)
    val httpClient: HttpClient = HttpClient(CIO)

    init {
        counter = 10
    }

    fun add() {
        counter++
    }
}