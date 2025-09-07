package com.github.jsonkt.apiktor

import com.github.jsonkt.shared.parse
import com.github.jsonkt.shared.prettyPrint
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receiveText
import io.ktor.server.response.respondText
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

/**
 * Response structure for API error messages.
 *
 * @property code HTTP status code
 * @property message Human-readable error description
 */
@Serializable
data class ErrorResponse(
    val code: Int,
    val message: String,
)

/**
 * Main entry point for the Ktor-based JSON parsing API server.
 *
 * Starts an embedded Netty server on port 8000 with a single endpoint:
 * POST /api/v1/parse - Accepts JSON strings and returns formatted JSON responses
 */
fun main() {
    embeddedServer(Netty, port = 8000, module = Application::module).start(wait = true)
}

/**
 * Configures the Ktor application with necessary plugins and routes.
 */
fun Application.module() {
    install(ContentNegotiation) {
        json()
    }
    routing {
        route("/api/v1") {
            post("/parse") {
                val text = call.receiveText()
                try {
                    val result = parse(text)
                    call.respondText(
                        result.token.prettyPrint(),
                        ContentType.Application.Json,
                    )
                } catch (e: Exception) {
                    call.respondText(
                        Json.encodeToString(ErrorResponse.serializer(), ErrorResponse(400, e.message ?: "Parse error")),
                        ContentType.Application.Json,
                        HttpStatusCode.BadRequest,
                    )
                }
            }
        }
    }
}
