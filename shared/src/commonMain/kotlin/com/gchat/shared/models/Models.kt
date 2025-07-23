package com.gchat.shared.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val phoneNumber: String,
    val hashedPassword: String,
    val displayName: String,
    val username: String? = null,
    val email: String? = null,
    val profilePictureURL: String? = null,
    val createdAt: Long,
    val updatedAt: Long,
    val lastSeenAt: Long,
    val isDeleted: Boolean = false
)

@Serializable
data class Conversation(
    val conversationID: String,
    val type: ConversationType,
    val createdAt: Long,
    val updatedAt: Long,
    val lastMessageID: String? = null
)

@Serializable
enum class ConversationType {
    ONE_TO_ONE, GROUP
}

@Serializable
data class ConversationParticipant(
    val conversationParticipantID: String,
    val conversationID: String,
    val userPhoneNumber: String,
    val joinedAt: Long
)

@Serializable
data class Message(
    val messageID: String,
    val conversationID: String,
    val senderPhoneNumber: String,
    val messageType: MessageType,
    val textContent: String? = null,
    val mediaURL: String? = null,
    val mediaMimeType: String? = null,
    val mediaFileSize: Long? = null,
    val linkURL: String? = null,
    val thumbnailURL: String? = null,
    val linkTitle: String? = null,
    val linkDescription: String? = null,
    val linkImageURL: String? = null,
    val linkSiteName: String? = null,
    val sentAt: Long
)

@Serializable
enum class MessageType {
    TEXT, IMAGE, VIDEO, LINK
}
