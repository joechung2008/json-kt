package joechungmsft.apiktor

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import joechungmsft.jsonkt.shared.parse
import joechungmsft.jsonkt.shared.ValueToken
import joechungmsft.jsonkt.shared.prettyPrint

@Serializable
data class ErrorResponse(val code: Int, val message: String)

fun main() {
    embeddedServer(Netty, port = 8000) {
        install(ContentNegotiation) {
            json()
        }
        routing {
            route("/api/v1") {
                post("/parse") {
                    val text = call.receiveText()
                    try {
                        val result = parse(text)
                        if (result.token == null) {
                            call.respond(
                                HttpStatusCode.BadRequest,
                                ErrorResponse(400, "Parse error: invalid input")
                            )
                        } else {
                            call.respondText(
                                result.token!!.prettyPrint(),
                                ContentType.Text.Plain
                            )
                        }
                    } catch (e: Exception) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse(400, e.message ?: "Parse error")
                        )
                    }
                }
            }
        }
    }.start(wait = true)
}
