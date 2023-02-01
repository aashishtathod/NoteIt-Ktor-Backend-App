package aashishtathod.dev

import aashishtathod.dev.data.db.DatabaseFactory
import io.ktor.server.application.*
import aashishtathod.dev.plugins.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {

    DatabaseFactory.init()

    configureCORS()
    configureMonitoring()
    configureAuthentication()
    configureStatusPages()
    configureSerialization()
    configureRouting()
}
