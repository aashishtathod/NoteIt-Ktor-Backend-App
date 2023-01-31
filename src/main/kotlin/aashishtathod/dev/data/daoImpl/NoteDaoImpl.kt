package aashishtathod.dev.data.daoImpl

import aashishtathod.dev.data.dao.NoteDao
import aashishtathod.dev.data.db.DatabaseFactory
import aashishtathod.dev.data.db.tables.NotesTable
import aashishtathod.dev.data.db.tables.UsersTable
import aashishtathod.dev.entity.Note
import aashishtathod.dev.entity.User
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.statements.InsertStatement

class NoteDaoImpl : NoteDao {
    override suspend fun add(userId: Int, title: String, note: String): Int? {
        var statement: InsertStatement<Number>? = null
        DatabaseFactory.dbQuery {
            statement = NotesTable.insert {
                it[NotesTable.userId] = userId
                it[NotesTable.title] = title
                it[NotesTable.note] = note
            }
        }

        val note = rowToNote(statement?.resultedValues?.get(0))

        return note?.noteId
    }

    private fun rowToNote(row: ResultRow?): Note? {
        return if (row == null) null
        else Note(
            noteId = row[NotesTable.noteId],
            userId = row[NotesTable.userId],
            title = row[NotesTable.title],
            note = row[NotesTable.note],
            createdAt = row[NotesTable.createdAt],
            isPinned = row[NotesTable.isPinned],
            updatedAt = row[NotesTable.updatedAt],
        )
    }

    override fun getAllByUser(userId: String): List<Note> {
        TODO("Not yet implemented")
    }

    override fun update(id: String, title: String, note: String): String {
        TODO("Not yet implemented")
    }

    override fun deleteById(id: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun isNoteOwnedByUser(id: String, userId: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun exists(id: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun updateNotePinById(id: String, isPinned: Boolean): String {
        TODO("Not yet implemented")
    }

}