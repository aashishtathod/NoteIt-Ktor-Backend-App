package aashishtathod.dev.utils.responses

import aashishtathod.dev.entity.Note
import io.ktor.http.*
import kotlinx.serialization.Serializable

sealed class NoteResponse {
    @Serializable
    data class Success(
        val statusCode: Int,
        val notes: List<Note> = emptyList()
    ) : NoteResponse()

    @Serializable
    data class Failure(
        val statusCode: Int,
        val message: String
    ) : NoteResponse()
}