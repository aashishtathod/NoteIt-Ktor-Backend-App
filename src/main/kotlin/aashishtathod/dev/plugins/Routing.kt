package aashishtathod.dev.plugins

import aashishtathod.dev.auth.JWTController
import aashishtathod.dev.auth.PasswordEncryptor
import aashishtathod.dev.controllers.AuthController
import aashishtathod.dev.data.daoImpl.UserDaoImpl
import aashishtathod.dev.routes.AuthRoute
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*

fun Application.configureRouting() {
    routing {
        val userDao = UserDaoImpl()
        val encryptor = PasswordEncryptor()
        val jwtController = JWTController()
        val authController = AuthController(userDao,jwtController,encryptor)
        AuthRoute(authController)

        get("/") {
            call.respondText("Hello World!")
        }
    }
}
