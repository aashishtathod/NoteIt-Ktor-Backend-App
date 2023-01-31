package aashishtathod.dev.routes

import aashishtathod.dev.controllers.AuthController
import aashishtathod.dev.utils.exceptions.FailureMessages
import aashishtathod.dev.utils.requests.LoginUserRequest
import aashishtathod.dev.utils.requests.RegisterUserRequest
import aashishtathod.dev.utils.responses.AuthResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.AuthRoute(authController: AuthController) {

    route("/auth") {

        post("/register") {
            val registerRequest = runCatching { call.receive<RegisterUserRequest>() }.getOrElse {
                throw BadRequestException(FailureMessages.MESSAGE_MISSING_CREDENTIALS)
            }
            val registerResponse: AuthResponse = authController.register(registerRequest)
            call.respond(HttpStatusCode.Created, registerResponse)
        }

        post("/login") {
            val loginRequest = runCatching { call.receive<LoginUserRequest>() }.getOrElse {
                throw BadRequestException(FailureMessages.MESSAGE_MISSING_CREDENTIALS)
            }

            val loginResponse: AuthResponse = authController.login(loginRequest)
            call.respond(HttpStatusCode.Created, loginResponse)
        }

    }
}