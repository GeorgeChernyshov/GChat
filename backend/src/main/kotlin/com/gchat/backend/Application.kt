package com.gchat.backend

import com.gchat.backend.database.initDatabase
import com.gchat.backend.routes.userRoutes
import com.gchat.backend.security.JwtConfig
import com.gchat.backend.services.UserService // Import UserService
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun main() {
    embeddedServer(CIO, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    initDatabase()

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }

    install(Authentication) {
        jwt("auth-jwt") {
            verifier(JwtConfig.verifier)
            validate { credential ->
                if (credential.payload.getClaim("phoneNumber").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { _, _ ->
                call.respond(io.ktor.http.HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
        }
    }

    val userService = UserService() // Create an instance of UserService

    configureRouting(userService) // Pass userService to configureRouting
}

fun Application.configureRouting(userService: UserService) { // Accept userService as a parameter
    routing {
        get("/") {
            call.respondText("Hello Backend!")
        }
        userRoutes(userService) // Integrate user routes
    }
}
