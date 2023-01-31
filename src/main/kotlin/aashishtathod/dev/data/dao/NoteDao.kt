package aashishtathod.dev.data.dao

import aashishtathod.dev.entity.Note

interface NoteDao {
    suspend fun add(userId: Int, title: String, note: String): Int?
    fun getAllByUser(userId: String): List<Note>
    fun update(id: String, title: String, note: String): String
    fun deleteById(id: String): Boolean
    fun isNoteOwnedByUser(id: String, userId: String): Boolean
    fun exists(id: String): Boolean
    fun updateNotePinById(id: String, isPinned: Boolean): String}