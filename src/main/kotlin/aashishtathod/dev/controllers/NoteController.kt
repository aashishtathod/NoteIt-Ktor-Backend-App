package aashishtathod.dev.controllers

import aashishtathod.dev.data.dao.NoteDao
import aashishtathod.dev.data.dao.UserDao
import aashishtathod.dev.entity.Note
import aashishtathod.dev.entity.User
import aashishtathod.dev.utils.exceptions.FailureMessages
import aashishtathod.dev.utils.exceptions.NoteNotFoundException
import aashishtathod.dev.utils.exceptions.UnauthorizedActivityException
import aashishtathod.dev.utils.requests.NoteRequest
import aashishtathod.dev.utils.requests.PinRequest
import aashishtathod.dev.utils.responses.AuthResponse
import aashishtathod.dev.utils.responses.NoteResponse
import io.ktor.http.*
import io.ktor.server.plugins.*

class NoteController(
    private val noteDao: NoteDao
) {

    suspend fun getNotesByUser(user: User): NoteResponse {
        return try {
            val notes = noteDao.getAllByUser(user.userId)

            NoteResponse.Success(HttpStatusCode.Created.value ,notes.map { Note(it.noteId, it.userId,it.title, it.note, it.createdAt, it.isPinned,it.updatedAt) })
        } catch (uae: UnauthorizedActivityException) {
            NoteResponse.Failure(HttpStatusCode.Unauthorized.value ,uae.message)
        }
    }

    suspend fun addNote(user: User, note: NoteRequest): NoteResponse {
        return try {
            val noteTitle = note.title.trim()
            val noteText = note.note.trim()

            validateNoteOrThrowException(noteTitle, noteText)

            val noteId = noteDao.add(user.userId, noteTitle, noteText)

            NoteResponse.Success(HttpStatusCode.Created.value)
        } catch (e: Exception) {
            NoteResponse.Failure(
                HttpStatusCode.FailedDependency.value, e.localizedMessage
            )
        }
    }

    suspend fun updateNote(user: User, noteId: Int, note: NoteRequest): NoteResponse {
        return try {
            val noteTitle = note.title.trim()
            val noteText = note.note.trim()

            validateNoteOrThrowException(noteTitle, noteText)
            checkNoteExistsOrThrowException(noteId)
            checkOwnerOrThrowException(user.userId, noteId)

            if (noteDao.update(noteId, noteTitle, noteText)) {
                NoteResponse.Success(HttpStatusCode.OK.value)
            } else {
                NoteResponse.Failure(HttpStatusCode.FailedDependency.value, "Error Occurred")
            }

        } catch (e: UnauthorizedActivityException) {
            NoteResponse.Failure(HttpStatusCode.Unauthorized.value, e.localizedMessage)
        } catch (e: BadRequestException) {
            NoteResponse.Failure(HttpStatusCode.BadRequest.value, e.localizedMessage)
        } catch (e: NoteNotFoundException) {
            NoteResponse.Failure(HttpStatusCode.NotFound.value, e.localizedMessage)
        }
    }


    suspend fun deleteNote(user: User, noteId: Int): NoteResponse {
        return try {
            checkNoteExistsOrThrowException(noteId)
            checkOwnerOrThrowException(user.userId, noteId)

            if (noteDao.deleteById(noteId)) {
                NoteResponse.Success(HttpStatusCode.OK.value)
            } else {
                NoteResponse.Failure(HttpStatusCode.FailedDependency.value, "Error Occurred")
            }

        } catch (e: UnauthorizedActivityException) {
            NoteResponse.Failure(HttpStatusCode.Unauthorized.value, e.localizedMessage)
        } catch (e: BadRequestException) {
            NoteResponse.Failure(HttpStatusCode.BadRequest.value, e.localizedMessage)
        } catch (e: NoteNotFoundException) {
            NoteResponse.Failure(HttpStatusCode.NotFound.value, e.localizedMessage)
        }
    }


    suspend fun updateNotePin(user: User, noteId: Int, pinRequest: PinRequest): NoteResponse {
        return try {
            checkNoteExistsOrThrowException(noteId)
            checkOwnerOrThrowException(user.userId, noteId)

            if (noteDao.updateNotePinById(noteId, pinRequest.isPinned)) {
                NoteResponse.Success(HttpStatusCode.OK.value)
            } else {
                NoteResponse.Failure(HttpStatusCode.FailedDependency.value, "Error Occurred")
            }
        } catch (e: UnauthorizedActivityException) {
            NoteResponse.Failure(HttpStatusCode.Unauthorized.value, e.localizedMessage)
        } catch (e: BadRequestException) {
            NoteResponse.Failure(HttpStatusCode.BadRequest.value, e.localizedMessage)
        } catch (e: NoteNotFoundException) {
            NoteResponse.Failure(HttpStatusCode.NotFound.value, e.localizedMessage)
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

    private suspend fun checkNoteExistsOrThrowException(noteId: Int) {
        if (!noteDao.exists(noteId)) {
            throw NoteNotFoundException("Note not exist with ID '$noteId'")
        }
    }

    private suspend fun checkOwnerOrThrowException(userId: Int, noteId: Int) {
        if (!noteDao.isNoteOwnedByUser(noteId, userId)) {
            throw UnauthorizedActivityException("Access denied")
        }
    }

}