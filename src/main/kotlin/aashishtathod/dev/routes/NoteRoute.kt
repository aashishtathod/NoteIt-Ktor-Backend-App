package aashishtathod.dev.routes

import aashishtathod.dev.auth.UserPrincipal
import aashishtathod.dev.controllers.NoteController
import aashishtathod.dev.utils.exceptions.FailureMessages
import aashishtathod.dev.utils.exceptions.UnauthorizedActivityException
import aashishtathod.dev.utils.requests.NoteRequest
import aashishtathod.dev.utils.requests.RegisterUserRequest
import aashishtathod.dev.utils.responses.AuthResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.NoteRoute(noteController: NoteController) {

   authenticate {
        get("/notes") {
            val principal = call.principal<UserPrincipal>()
                ?: throw UnauthorizedActivityException(FailureMessages.MESSAGE_ACCESS_DENIED)


        }

        route("/note") {

            post("/new") {

                val principal = call.principal<UserPrincipal>()
                    ?: throw UnauthorizedActivityException(FailureMessages.MESSAGE_ACCESS_DENIED)

                val noteRequest = runCatching { call.receive<NoteRequest>() }.getOrElse {
                    throw BadRequestException(FailureMessages.MESSAGE_MISSING_CREDENTIALS)
                }
                val noteResponse = noteController.addNote(principal.user, noteRequest)

                call.respond(HttpStatusCode.Created, noteResponse)
            }


        }
    }
}