package com.gchat.backend.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction
// Add this import if not already present, though PrimaryKey is often accessible via Table.PrimaryKey
// import org.jetbrains.exposed.sql.statements.api.Descriptor

object Users : Table("users") {
    val phoneNumber = varchar("phone_number", 20).uniqueIndex() // Removed .primaryKey()
    val hashedPassword = varchar("hashed_password", 255)
    val displayName = varchar("display_name", 100)
    val createdAt = long("created_at")
    val updatedAt = long("updated_at")
    val lastSeenAt = long("last_seen_at")
    val isDeleted = bool("is_deleted").default(false)

    // Define primary key explicitly
    override val primaryKey = PrimaryKey(phoneNumber, name = "PK_Users_PhoneNumber") // It's good practice to name PK constraints
}

fun initDatabase() {
    Database.connect("jdbc:h2:mem:gchat_db;DB_CLOSE_DELAY=-1;IGNORECASE=TRUE", driver = "org.h2.Driver")
    transaction {
        SchemaUtils.create(Users)
    }
}
