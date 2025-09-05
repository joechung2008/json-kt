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

/**
 * Response structure for API error messages.
 *
 * @property code HTTP status code
 * @property message Human-readable error description
 */
@Serializable
data class ErrorResponse(val code: Int, val message: String)

/**
 * Main entry point for the Ktor-based JSON parsing API server.
 *
 * Starts an embedded Netty server on port 8000 with a single endpoint:
 * POST /api/v1/parse - Accepts JSON strings and returns formatted JSON responses
 */
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
