package aashishtathod.dev.utils.responses

import io.ktor.http.*

sealed class NoteResponse {
    data class Success<T>(
        val statusCode: HttpStatusCode,
        val data: T? = null
    ) : NoteResponse()

    data class Failure(
        val statusCode: HttpStatusCode,
        val message: String? = null
    ) : NoteResponse()
}