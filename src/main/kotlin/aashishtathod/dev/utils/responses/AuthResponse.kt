package aashishtathod.dev.utils.responses

import aashishtathod.dev.entity.User
import io.ktor.http.*
import kotlinx.serialization.Serializable

sealed class AuthResponse {
    @Serializable
    data class Success(
        val statusCode: Int,
        val token: String,
    ) : AuthResponse()

    @Serializable
    data class Failure(
        val statusCode: Int,
        val message: String
    ) : AuthResponse()
}
