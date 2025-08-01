package com.gchat.backend

import co.touchlab.kermit.Logger
import com.gchat.backend.database.Database
import com.gchat.backend.database.initDatabase
import com.gchat.backend.routes.userRoutes
import com.gchat.backend.security.JwtConfig
import com.gchat.backend.services.UserService // Import UserService
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerContentNegotiation
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun main() {
    embeddedServer(CIO, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    // Kermit logger for this module
    val logger = Logger // Creates a logger instance with default configuration

    logger.i { "Application module starting..." }

    // Initialize database
    logger.d { "Connecting to database..." }
    Database.connect()
    logger.i { "Database connected." }


    // Conditionally install ContentNegotiation
    if (pluginOrNull(ContentNegotiation) == null) {
        logger.d { "Installing ContentNegotiation plugin." }
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    } else {
        logger.d { "ContentNegotiation plugin already installed." }
    }

    // Conditionally install Authentication
    if (pluginOrNull(Authentication) == null) {
        logger.d { "Installing Authentication plugin." }
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
    } else {
        logger.d { "Authentication plugin already installed." }
    }

    logger.d { "Initializing UserService and configuring routing..." }
    val userService = UserService()
    configureRouting(userService)

    logger.i { "Application module configuration complete." }
}

fun Application.configureRouting(userService: UserService) { // Accept userService as a parameter
    routing {
        get("/") {
            call.respondText("Hello Backend!")
        }
        userRoutes(userService) // Integrate user routes
    }
}
