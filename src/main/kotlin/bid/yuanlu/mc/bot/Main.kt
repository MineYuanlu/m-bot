package bid.yuanlu.mc.bot

import bid.yuanlu.mc.bot.web.configureRouting
import bid.yuanlu.mc.bot.web.configureSockets
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "127.0.0.1") {
        configureSockets()
        configureRouting()
    }.start(wait = true)
}