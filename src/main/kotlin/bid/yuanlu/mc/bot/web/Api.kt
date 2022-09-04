package bid.yuanlu.mc.bot.web

import bid.yuanlu.mc.bot.Core
import bid.yuanlu.mc.bot.def.Plans
import bid.yuanlu.mc.bot.def.Play
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach

const val apiPath = "/api"
const val gameApiPath = "$apiPath/game"
const val manageApiPath = "$apiPath/manage"

private data class ApiRespBad(val err: String) {
    val ok = false
}

private data class ApiRespOk(val info: Any? = null) {
    val ok = true
}

private data class CreateData(
    val player: String,
    val host: String,
    val port: Int,
    val plan: String,
)


fun Application.configureApi() {
    install(ContentNegotiation) {
        gson()
    }
    install(WebSockets) {
        pingPeriod = java.time.Duration.ofSeconds(15)
        timeout = java.time.Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        get("$gameApiPath/start-plan") {
            val play = getPlay(call) ?: return@get
            play.startPlans()
            call.respond(ApiRespOk())
        }
        get("$gameApiPath/stop-plan") {
            val play = getPlay(call) ?: return@get
            play.stopPlans()
            call.respond(ApiRespOk())
        }
        get("$gameApiPath/plan") {
            val play = getPlay(call) ?: return@get
            call.respond(ApiRespOk(play.getPlans().planString))
        }
        post("$gameApiPath/plan") {
            val data = call.receive<String>()
            val play = getPlay(call) ?: return@post
            play.plans.update(data)
            call.respond(ApiRespOk())
        }
        get("$gameApiPath/disconnect") {
            val play = getPlay(call) ?: return@get
            play.disconnect(call.request.queryParameters["reason"] ?: "")
            call.respond(ApiRespOk())
        }
        get("$gameApiPath/connect") {
            val play = getPlay(call) ?: return@get
            play.connect()
            call.respond(ApiRespOk())
        }
        get("$gameApiPath/chat") {
            val play = getPlay(call) ?: return@get
            val line = call.request.queryParameters["chat"]
            if (line == null || line.isBlank()) {
                call.respond(ApiRespBad("Bad Request"))
                return@get
            }
            play.chat(line)
            call.respond(ApiRespOk())
        }
        post("$manageApiPath/create") {
            val data = call.receive<CreateData>()
            try {
                val plan = Plans.build(data.plan)
                val id = Core.addPlay(data.player, data.host, data.port, plan)
                call.respond(ApiRespOk(id.toString()))
            } catch (e: Throwable) {
                call.respond(ApiRespBad(e.toString()))
            }
        }
        get("$manageApiPath/remove") {
            val id = getId(call) ?: return@get
            call.respond(ApiRespOk(Core.removePlay(id) != null))
        }
        get("$manageApiPath/list") {
            call.respond(ApiRespOk(getPlayList()))
        }
        get("$manageApiPath/get") {
            val play = getPlay(call) ?: return@get
            val withList = call.request.queryParameters["list"]?.toBoolean()
            call.respond(ApiRespOk(getPlayInfo(play, withList)))
        }
        webSocket("$gameApiPath/info") { // websocketSession
            val id = call.request.queryParameters["id"]?.toLongOrNull()
            if (id == null) {
                close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "NO ID"))
                return@webSocket
            }
            if (!Core.hookMsg(id, this)) {
                close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Not Found Play $id "))
                return@webSocket
            }
            incoming.consumeEach { }
        }
    }
}

suspend fun getId(call: ApplicationCall): Long? {
    val id = call.request.queryParameters["id"]?.toLongOrNull()
    if (id == null) call.respond(ApiRespBad("BAD ID"))
    return id
}

suspend fun getPlay(call: ApplicationCall): Play? {
    val id = getId(call) ?: return null
    val play = Core.getPlay(id)
    if (play == null) call.respond(ApiRespBad("Not Found Play $id"))
    return play
}