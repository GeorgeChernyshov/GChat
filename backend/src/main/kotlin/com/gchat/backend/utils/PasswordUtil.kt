package com.gchat.backend.utils

import org.mindrot.jbcrypt.BCrypt

object PasswordUtil {
    fun hashPassword(password: String): String = BCrypt.hashpw(password, BCrypt.gensalt())
    fun checkPassword(password: String, hashed: String): Boolean = BCrypt.checkpw(password, hashed)
}
