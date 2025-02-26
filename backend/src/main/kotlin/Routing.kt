package com.example

import com.example.model.CodeRequest
import com.example.service.CodeExecutorService
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        route("/health-check") {
            get {
                call.respondText("Hello World!")
            }
        }

        route("/run-code") {
            post {
                try {
                    println("Received POST request for /run-code")
                    val request = call.receive<String>()
                    val result = CodeExecutorService.executeCode(request)
                    call.respondText(result)
                } catch (e: Exception) {
                    call.respondText("Error: ${e.message}")
                }
            }
        }
    }
}
