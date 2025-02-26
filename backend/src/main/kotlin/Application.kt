package com.example

import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
    println("Server running at http://localhost:8080/")
}

fun Application.module() {
    configureHTTP()
    configureSerialization()
    configureRouting()
}
