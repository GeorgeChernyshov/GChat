package com.gchat.backend.services

import com.gchat.backend.database.Users
import com.gchat.backend.utils.PasswordUtil
import com.gchat.shared.models.User
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

class UserService {

    private fun resultRowToUser(row: ResultRow) = User(
        phoneNumber = row[Users.phoneNumber],
        hashedPassword = row[Users.hashedPassword],
        displayName = row[Users.displayName],
        username = null, // Not yet implemented in DB/registration
        email = null, // Not yet implemented in DB/registration
        profilePictureURL = null, // Not yet implemented in DB/registration
        createdAt = row[Users.createdAt],
        updatedAt = row[Users.updatedAt],
        lastSeenAt = row[Users.lastSeenAt],
        isDeleted = row[Users.isDeleted]
    )

    fun registerUser(phoneNumber: String, password: String): User? {
        return transaction {
            if (Users.select { Users.phoneNumber eq phoneNumber }.count() > 0) {
                // User with this phone number already exists
                return@transaction null
            }

            val hashedPassword = PasswordUtil.hashPassword(password)
            val currentTime = System.currentTimeMillis()

            val insertStatement = Users.insert {
                it[Users.phoneNumber] = phoneNumber
                it[Users.hashedPassword] = hashedPassword
                it[Users.displayName] = phoneNumber
                it[Users.createdAt] = currentTime
                it[Users.updatedAt] = currentTime
                it[Users.lastSeenAt] = currentTime
                it[Users.isDeleted] = false
            }

            insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToUser)
        }
    }

    fun findUserByPhoneNumber(phoneNumber: String): User? {
        return transaction {
            Users.select { Users.phoneNumber eq phoneNumber }
                .mapNotNull(::resultRowToUser)
                .singleOrNull()
        }
    }

    fun checkPassword(password: String, hashed: String): Boolean {
        return PasswordUtil.checkPassword(password, hashed)
    }
}
