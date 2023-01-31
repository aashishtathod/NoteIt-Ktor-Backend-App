package aashishtathod.dev.utils.responses

import io.ktor.http.*
import kotlinx.serialization.Serializable

sealed class NoteResponse {
    @Serializable
    data class Success(
        val statusCode: Int,
        val noteId: Int
    ) : NoteResponse()

    @Serializable
    data class Failure(
        val statusCode: Int,
        val message: String
    ) : NoteResponse()
}