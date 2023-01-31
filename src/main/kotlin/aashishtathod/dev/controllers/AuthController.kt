package aashishtathod.dev.controllers

import aashishtathod.dev.auth.JWTController
import aashishtathod.dev.auth.PasswordEncryptor
import aashishtathod.dev.data.dao.UserDao
import aashishtathod.dev.utils.isAlphaNumeric
import aashishtathod.dev.utils.requests.LoginUserRequest
import aashishtathod.dev.utils.requests.RegisterUserRequest
import aashishtathod.dev.utils.responses.AuthResponse
import io.ktor.http.*
import io.ktor.server.plugins.*

class AuthController(
    private val userDao: UserDao,
    private val jwt: JWTController,
    private val encryptor: PasswordEncryptor
) {

    suspend fun register(request: RegisterUserRequest): AuthResponse {
        return try {
            validateSignUpCredentialsOrThrowException(request)

            if (userDao.isUsernameAvailable(request.username).not()) {
                throw BadRequestException("Username is not available")
            }

            val user = userDao.registerUser(
                request.username,
                encryptor.encrypt(request.password),
                request.name
            )!!

            AuthResponse.Success(HttpStatusCode.Created.value, jwt.sign(user.userId))

        } catch (e: Exception) {
            when (e) {
                is BadRequestException -> AuthResponse.Failure(
                    HttpStatusCode.BadRequest.value,
                    e.localizedMessage
                )

                else -> AuthResponse.Failure(
                    HttpStatusCode.InternalServerError.value,
                    e.localizedMessage
                )
            }
        }

    }


    suspend fun login(request: LoginUserRequest): AuthResponse {
        return try {
            validateLoginCredentialsOrThrowException(request)

            val user = userDao.findByUsernameAndPassword(
                request.username,
                encryptor.encrypt(request.password)
            ) ?: throw BadRequestException("Invalid credentials")

            AuthResponse.Success(HttpStatusCode.OK.value, jwt.sign(user.userId))

        } catch (e: Exception) {
            when (e) {
                is BadRequestException -> AuthResponse.Failure(
                    HttpStatusCode.BadRequest.value,
                    e.localizedMessage
                )

                else -> AuthResponse.Failure(
                    HttpStatusCode.InternalServerError.value,
                    e.localizedMessage
                )
            }
        }
    }

    private fun validateSignUpCredentialsOrThrowException(request: RegisterUserRequest) {
        with(request) {
            val message = when {
                (username.isBlank() or password.isBlank() or name.isBlank()) -> "Username,password and name should not be blank"
                (username.length !in (4..30)) -> "Username should be of min 4 and max 30 character in length"
                (password.length !in (8..50)) -> "Password should be of min 8 and max 50 character in length"
                (name.length !in (4..24)) -> "Name should be of min 4 and max 24 character in length"
                (!username.isAlphaNumeric()) -> "No special characters allowed in username"
                else -> return
            }
            throw BadRequestException(message)
        }
    }

    private fun validateLoginCredentialsOrThrowException(request: LoginUserRequest) {
        with(request) {
            val message = when {
                (username.isBlank() or password.isBlank()) -> "Username and password  should not be blank"
                (username.length !in (4..30)) -> "Username should be of min 4 and max 30 character in length"
                (password.length !in (8..50)) -> "Password should be of min 8 and max 50 character in length"
                (!username.isAlphaNumeric()) -> "No special characters allowed in username"
                else -> return
            }
            throw BadRequestException(message)
        }
    }

}