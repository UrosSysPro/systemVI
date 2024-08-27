package com.systemvi

import com.systemvi.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(
        Netty,
        port = 8080,
        host = "0.0.0.0",
        watchPaths = listOf("classes"),
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    configureRouting()
}
