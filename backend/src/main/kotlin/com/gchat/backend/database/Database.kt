package com.gchat.backend.database

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import com.gchat.backend.security.JwtConfig // Assuming this is where hashPassword is

// Existing Users table definition remains unchanged
object Users : Table("users") {
    val phoneNumber = varchar("phone_number", 20).uniqueIndex()
    val hashedPassword = varchar("hashed_password", 255)
    val displayName = varchar("display_name", 100)
    val createdAt = long("created_at")
    val updatedAt = long("updated_at")
    val lastSeenAt = long("last_seen_at")
    val isDeleted = bool("is_deleted").default(false)

    override val primaryKey = PrimaryKey(phoneNumber, name = "PK_Users_PhoneNumber")
}

// NEW: Define a Database object to manage the connection and provide helper methods
object Database {
    lateinit var db: org.jetbrains.exposed.sql.Database // Use Exposed's Database type
    var isTesting: Boolean = false // Flag for test environment

    fun connect() {
        db = if (isTesting) {
            org.jetbrains.exposed.sql.Database.connect(
                "jdbc:h2:mem:test_gchat_db;DB_CLOSE_DELAY=-1;IGNORECASE=TRUE",
                driver = "org.h2.Driver"
            )
        } else {
            org.jetbrains.exposed.sql.Database.connect(
                "jdbc:h2:mem:gchat_db;DB_CLOSE_DELAY=-1;IGNORECASE=TRUE",
                driver = "org.h2.Driver"
            )
        }
        transaction(db) {
            SchemaUtils.create(Users)
        }
    }

    // Existing createUser, modified to use the new 'db' and return String ID
    fun createUser(displayName: String, phoneNumber: String, passwordRaw: String): String {
        return transaction(db) {
            val hashedPassword = JwtConfig.hashPassword(passwordRaw) // Assuming JwtConfig is accessible
            val newUserId = Users.insert {
                it[Users.displayName] = displayName
                it[Users.phoneNumber] = phoneNumber
                it[Users.hashedPassword] = hashedPassword
                it[createdAt] = System.currentTimeMillis()
                it[updatedAt] = System.currentTimeMillis()
                it[lastSeenAt] = System.currentTimeMillis()
                it[isDeleted] = false
            } get Users.phoneNumber // Assuming primary key (phoneNumber) is the ID
            newUserId
        }
    }

    // Existing isUserExist, modified to use the new 'db'
    fun isUserExist(phoneNumber: String): Boolean {
        return transaction(db) {
            Users.select { Users.phoneNumber eq phoneNumber }.count() > 0
        }
    }

    // Existing getUserByPhoneNumber, modified to use the new 'db'
    // Note: The original getUserByPhoneNumber in your file was `fun getUserByPhoneNumber(phoneNumber: String): Transaction.() -> Unit {`
    // I'm simplifying its return to be the ResultRow directly or null for better usability in the new context.
    fun getUserByPhoneNumber(phoneNumber: String): org.jetbrains.exposed.sql.ResultRow? {
        return transaction(db) {
            Users.select { Users.phoneNumber eq phoneNumber }.singleOrNull()
        }
    }
}

// The top-level initDatabase function can now call Database.connect()
fun initDatabase() {
    Database.connect()
}
