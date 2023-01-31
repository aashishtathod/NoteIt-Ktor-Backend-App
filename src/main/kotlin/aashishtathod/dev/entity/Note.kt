package aashishtathod.dev.entity

import org.joda.time.DateTime

data class Note(
    val noteId: Int,
    val userId: Int,
    val title: String,
    val note: String,
    val createdAt: DateTime,
    val isPinned: Boolean,
    val updatedAt: DateTime
)
