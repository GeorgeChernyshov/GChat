package com.gchat.backend.routes

import com.gchat.backend.database.Database
import com.gchat.backend.database.Users
import com.gchat.backend.module
import com.gchat.backend.security.JwtConfig
import com.gchat.backend.utils.PasswordUtil.hashPassword
import com.gchat.shared.data.model.auth.AuthResponse
import com.gchat.shared.data.model.auth.LoginRequest
import com.gchat.shared.data.model.auth.RegisterRequest
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.select
import kotlin.test.*

class UserRoutesRegistrationTest {

    @BeforeTest
    fun setupDatabase() {
        Database.isTesting = true // Ensure H2 is used
        Database.connect() // Connects to H2 in-memory
        transaction(Database.db) {
            SchemaUtils.create(Users) // Create tables
        }
    }

    @AfterTest
    fun tearDownDatabase() {
        transaction(Database.db) {
            SchemaUtils.drop(Users) // Clean up tables
        }
    }

    @Test
    fun testServerIsReachableAndBasicRoutingWorks() = testApplication {
        application {
            // This calls your main Ktor module setup from Application.kt
            module()
        }

        // This client doesn't need special content negotiation for a simple text response.
        val response = client.get("/") // We'll define this route next

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Hello Backend!", response.bodyAsText())
    }

    @Test
    fun testSuccessfulRegistration() = testApplication {
        application {
            module()
        }

        val client = createClient {
            install(ClientContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true // Important for robust clients
                })
            }
        }

        val uniquePhoneNumber = "1234567890"
        val registerRequest = RegisterRequest(
            phoneNumber = uniquePhoneNumber,
            password = "password123"
        )

        // Perform the registration request
        val response = client.post("/v1/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(registerRequest)
        }

        // Assertions for the response
        assertEquals(HttpStatusCode.Created, response.status)
        val successResponse = response.body<AuthResponse>()
        assertNotNull(successResponse.user, "User should not be null")
        assertNotNull(successResponse.token, "Token should not be null")

        // Assertions for database state (optional but good)
        transaction(Database.db) {
            val dbUser = Users.select { Users.phoneNumber eq uniquePhoneNumber }.singleOrNull()
            assertNotNull(dbUser, "User should be created in the database")
        }
    }

    @Test
    fun testRegistrationUserAlreadyExists() = testApplication {
        application {
            module()
        }
         val client = createClient {
            install(ClientContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }

        val existingPhoneNumber = "0987654321"
        val initialRegisterRequest = RegisterRequest(
            phoneNumber = existingPhoneNumber,
            password = "password123"
        )

        // First, register the user successfully
        val initialResponse = client.post("/v1/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(initialRegisterRequest)
        }
        assertEquals(HttpStatusCode.Created, initialResponse.status, "Initial registration should succeed")

        // Now, attempt to register the same user again
        val duplicateRegisterRequest = RegisterRequest(
            phoneNumber = existingPhoneNumber, // Phone number is the same
            password = "anotherPassword"
        )

        val conflictResponse = client.post("/v1/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(duplicateRegisterRequest)
        }

        // Assertions for the conflict response
        assertEquals(HttpStatusCode.Conflict, conflictResponse.status)
    }

    @Test
    fun testRegistrationMissingPhoneNumber() = testApplication {
        application {
            module()
        }
        val client = createClient {
            install(ClientContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }

        // phoneNumber is non-nullable in RegisterRequest
        // Sending a JSON that omits it, or is malformed for it.
        // Ktor should ideally return 400 Bad Request if deserialization fails.
        // Note: The exact error message might depend on Ktor's internals or specific configurations.
        val malformedRequestBody = """
            {
                "username": "Test User Missing Phone",
                "password": "password123"
            }
        """.trimIndent() // phoneNumber is missing

        val response = client.post("/v1/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(malformedRequestBody)
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        // You might want to assert parts of the error message if Ktor provides a consistent one
        // For example: val errorBody = response.bodyAsText(); assertTrue(errorBody.contains("phoneNumber"))
    }

    @Test
    fun testSuccessfulLogin() = testApplication {
        application {
            module()
        }
        val client = createClient {
            install(ClientContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        val userPhoneNumber = "1234567890"
        val userPassword = "passwordSecure123"
        val userDisplayName = "Login Test User"
        // Directly insert the user with a hashed password
        val password = hashPassword(userPassword) // Assumes JwtConfig is set up

        transaction(Database.db) {
            Users.insert {
                it[phoneNumber] = userPhoneNumber
                it[hashedPassword] = password
                it[displayName] = userDisplayName
                it[createdAt] = System.currentTimeMillis()
                it[updatedAt] = System.currentTimeMillis()
                it[lastSeenAt] = System.currentTimeMillis()
            }
        }

        val loginRequest = LoginRequest(phoneNumber = userPhoneNumber, password = userPassword)

        val response = client.post("/v1/auth/login") { // Assuming this is your login endpoint
            contentType(ContentType.Application.Json)
            setBody(loginRequest)
        }

        assertEquals(HttpStatusCode.OK, response.status, "Login failed with status: ${response.bodyAsText()}")
        val authResponse = response.body<AuthResponse>()
        assertNotNull(authResponse.token, "Login token should not be null")
        assertTrue(authResponse.token!!.isNotBlank(), "Login token should not be blank")
        assertNotNull(authResponse.user, "User details should not be null after login")
        assertEquals(userPhoneNumber, authResponse.user?.phoneNumber)
        assertEquals(userDisplayName, authResponse.user?.displayName)
    }

    @Test
    fun testLoginWithIncorrectPassword() = testApplication {
        application {
            module()
        }
        val client = createClient {
            install(ClientContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }

        val userPhoneNumber = "1234567890"
        val correctPassword = "passwordCorrect"
        val incorrectPassword = "passwordIncorrect"
        val userDisplayName = "Login Fail User"
        val password = hashPassword(correctPassword)

        transaction(Database.db) {
            Users.insert {
                it[phoneNumber] = userPhoneNumber
                it[hashedPassword] = password
                it[displayName] = userDisplayName
                it[createdAt] = System.currentTimeMillis()
                it[updatedAt] = System.currentTimeMillis()
                it[lastSeenAt] = System.currentTimeMillis()
            }
        }

        val loginRequest = LoginRequest(phoneNumber = userPhoneNumber, password = incorrectPassword)

        val response = client.post("/v1/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(loginRequest)
        }

        assertEquals(
            HttpStatusCode.Unauthorized,
            response.status,
            "Login with incorrect password should return 401 Unauthorized."
        )
    }
}