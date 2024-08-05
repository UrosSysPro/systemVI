package com.systempro.systemvi.compose_app

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.ktor.client.*
import io.ktor.client.engine.cio.*

class AppState : ViewModel() {
    private var _counter by mutableIntStateOf(0)
    val httpClient: HttpClient = HttpClient(CIO)
    fun counter(): Int = _counter

    init {
        _counter = 10
    }

    fun add() {
        _counter++
    }
}