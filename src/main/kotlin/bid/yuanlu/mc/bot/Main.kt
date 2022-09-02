package bid.yuanlu.mc.bot

import bid.yuanlu.mc.bot.web.configureApi
import bid.yuanlu.mc.bot.web.configureRouting
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cors.*

fun main() {
    Core.init()
    embeddedServer(Netty, port = 8080, host = "127.0.0.1") {
        install(CORS) {
            allowHost("*")
        }
        configureRouting()
        configureApi()
    }.start(wait = true)
}