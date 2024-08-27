package com.systemvi.plugins

import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        staticResources("html","html")
        staticResources("images","images")

        get("/") {
            call.respondText("[GET] root")
        }
        post("/"){
            call.respondText("[POST] root aaa")
        }
        get("/about"){
            call.respondText("[GET] about")
        }
    }
}
