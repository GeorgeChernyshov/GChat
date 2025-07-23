package com.gchat.backend.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.gchat.shared.models.User
import java.util.*

object JwtConfig {
    private const val secret = "your-super-secret-jwt-key-please-change-this-in-production"
    private const val issuer = "com.gchat.backend"
    private const val audience = "com.gchat.users"
    private const val ACCESS_TOKEN_EXPIRATION = 1000L * 60 * 60 // 1 hour
    private const val REFRESH_TOKEN_EXPIRATION = 1000L * 60 * 60 * 24 * 7 // 7 days

    private val algorithm = Algorithm.HMAC256(secret)

    fun generateAccessToken(user: User): String = JWT.create()
        .withAudience(audience)
        .withIssuer(issuer)
        .withClaim("phoneNumber", user.phoneNumber)
        .withExpiresAt(Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
        .sign(algorithm)

    fun generateRefreshToken(user: User): String = JWT.create()
        .withAudience(audience)
        .withIssuer(issuer)
        .withClaim("phoneNumber", user.phoneNumber)
        .withExpiresAt(Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
        .sign(algorithm)

    val verifier = JWT.require(algorithm)
        .withAudience(audience)
        .withIssuer(issuer)
        .build()
}
