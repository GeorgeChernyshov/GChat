package com.gchat.backend.routes

import com.gchat.backend.security.JwtConfig
import com.gchat.backend.services.UserService
import com.gchat.shared.data.model.auth.AuthResponse
import com.gchat.shared.data.model.auth.LoginRequest
import com.gchat.shared.data.model.auth.RegisterRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRoutes(userService: UserService) {
    route("/v1/auth") {
        post("/register") {
            val request = call.receive<RegisterRequest>()

            // Basic validation
            if (request.phoneNumber.isBlank() || request.password.isBlank()) {
                return@post call.respond(
                    HttpStatusCode.BadRequest,
                    "Phone number and password cannot be empty"
                )
            }
            if (request.password.length < 6) {
                return@post call.respond(
                    HttpStatusCode.BadRequest,
                    "Password must be at least 6 characters long"
                )
            }
            // TODO: Add more robust phone number and display name validation/sanitization

            val user = userService.registerUser(request.phoneNumber, request.password)

            if (user == null) {
                call.respond(HttpStatusCode.Conflict, "User with this phone number already exists")
            } else {
                val accessToken = JwtConfig.generateAccessToken(user)
                val refreshToken = JwtConfig.generateRefreshToken(user)
                call.respond(HttpStatusCode.Created, AuthResponse(accessToken, refreshToken, user))
            }
        }

        post("/login") {
            val request = call.receive<LoginRequest>()

            // Basic validation
            if (request.phoneNumber.isBlank() || request.password.isBlank()) {
                return@post call.respond(HttpStatusCode.BadRequest, "Phone number and password cannot be empty")
            }

            val user = userService.findUserByPhoneNumber(request.phoneNumber)

            if (user == null || !userService.checkPassword(request.password, user.hashedPassword)) {
                call.respond(HttpStatusCode.Unauthorized, "Invalid phone number or password")
            } else {
                val accessToken = JwtConfig.generateAccessToken(user)
                val refreshToken = JwtConfig.generateRefreshToken(user)
                call.respond(HttpStatusCode.OK, AuthResponse(accessToken, refreshToken, user))
            }
        }
    }
}
