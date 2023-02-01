package aashishtathod.dev.data.daoImpl

import aashishtathod.dev.data.dao.NoteDao
import aashishtathod.dev.data.db.DatabaseFactory
import aashishtathod.dev.data.db.tables.NotesTable
import aashishtathod.dev.data.db.tables.UsersTable
import aashishtathod.dev.entity.Note
import aashishtathod.dev.entity.User
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.UpdateStatement

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
        val insertedNote = rowToNote(statement?.resultedValues?.get(0))
        return insertedNote?.noteId
    }

    override suspend fun getAllByUser(userId: Int): List<Note> =
        DatabaseFactory.dbQuery {
            NotesTable.select { NotesTable.userId.eq(userId) }
                .mapNotNull { rowToNote(it) }
        }

    override suspend fun update(noteId: Int, title: String, note: String): Boolean =
        DatabaseFactory.dbQuery {
            NotesTable.update({ NotesTable.noteId.eq(noteId) }) {
                it[NotesTable.noteId] = noteId
                it[NotesTable.title] = title
                it[NotesTable.note] = note
            }
        } > 0


    override suspend fun deleteById(noteId: Int): Boolean = DatabaseFactory.dbQuery {
        NotesTable.deleteWhere { NotesTable.noteId.eq(noteId) }
    } > 0

    override suspend fun isNoteOwnedByUser(noteId: Int, userId: Int): Boolean {
        val note = DatabaseFactory.dbQuery {
            NotesTable.select { NotesTable.noteId.eq(noteId) and NotesTable.userId.eq(userId) }
                .map { rowToNote(it) }
                .singleOrNull()
        }
        return note != null
    }

    override suspend fun exists(noteId: Int): Boolean {
        val note = DatabaseFactory.dbQuery {
            NotesTable.select { NotesTable.noteId.eq(noteId) }
                .map { rowToNote(it) }
                .singleOrNull()
        }
        return note != null
    }

    override suspend fun updateNotePinById(noteId: Int, isPinned: Boolean): Boolean =
        DatabaseFactory.dbQuery {
            NotesTable.update({ NotesTable.noteId.eq(noteId) }) {
                it[NotesTable.isPinned] = isPinned
            }
        } > 0


    private fun rowToNote(row: ResultRow?): Note? {
        return if (row == null) null
        else Note(
            noteId = row[NotesTable.noteId],
            userId = row[NotesTable.userId],
            title = row[NotesTable.title],
            note = row[NotesTable.note],
            createdAt = row[NotesTable.createdAt].millis,
            isPinned = row[NotesTable.isPinned],
            updatedAt = row[NotesTable.updatedAt].millis,
        )
    }


}