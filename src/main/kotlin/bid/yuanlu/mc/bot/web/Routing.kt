package bid.yuanlu.mc.bot.web

import bid.yuanlu.mc.bot.Core
import freemarker.cache.ClassTemplateLoader
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }

    routing {
        static("/css") {
            resources("templates/css")
        }
        get("/") {
            call.respond(FreeMarkerContent("index.ftl", mapOf("data" to IndexData(Core.PLAYS)), ""))
        }
        get("/play"){
          val id= call.request.queryParameters["id"];

        }
    }
}
