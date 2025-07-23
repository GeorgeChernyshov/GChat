package com.gchat.backend.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction

object Users : Table("users") {
    val phoneNumber = varchar("phone_number", 20).uniqueIndex().primaryKey()
    val hashedPassword = varchar("hashed_password", 255)
    val displayName = varchar("display_name", 100)
    val createdAt = long("created_at")
    val updatedAt = long("updated_at")
    val lastSeenAt = long("last_seen_at")
    val isDeleted = bool("is_deleted").default(false)
}

fun initDatabase() {
    Database.connect("jdbc:h2:mem:gchat_db;DB_CLOSE_DELAY=-1;IGNORECASE=TRUE", driver = "org.h2.Driver")
    transaction {
        SchemaUtils.create(Users)
    }
}
