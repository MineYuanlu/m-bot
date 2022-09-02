package bid.yuanlu.mc.bot.web

import freemarker.cache.ClassTemplateLoader
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }

    routing {
        static("/") {
            staticBasePackage = "web"
            resources(".")
            defaultResource("index.html")
        }
        static("/console") {
            staticBasePackage = "web"
            defaultResource("console.html")
        }
    }
}
