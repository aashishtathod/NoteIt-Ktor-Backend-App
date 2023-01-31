package aashishtathod.dev.controllers

import aashishtathod.dev.data.dao.NoteDao
import aashishtathod.dev.data.dao.UserDao
import aashishtathod.dev.entity.User
import aashishtathod.dev.utils.requests.NoteRequest
import aashishtathod.dev.utils.responses.AuthResponse
import aashishtathod.dev.utils.responses.NoteResponse
import io.ktor.http.*
import io.ktor.server.plugins.*

class NoteController(
    private val noteDao: NoteDao
) {

    suspend fun addNote(user: User, note: NoteRequest): NoteResponse {
        return try {
            val noteTitle = note.title.trim()
            val noteText = note.note.trim()

            validateNoteOrThrowException(noteTitle, noteText)

            val noteId =  noteDao.add(user.userId, noteTitle, noteText)

            NoteResponse.Success(HttpStatusCode.Created.value, noteId!!)
        } catch (e : Exception) {
            NoteResponse.Failure(
                HttpStatusCode.FailedDependency.value,
                e.localizedMessage)
        }
    }


    private fun validateNoteOrThrowException(title: String, note: String) {
        val message = when {
            (title.isBlank() or note.isBlank()) -> "Title and Note should not be blank"
            (title.length !in (4..30)) -> "Title should be of min 4 and max 30 character in length"
            else -> return
        }

        throw BadRequestException(message)
    }

}