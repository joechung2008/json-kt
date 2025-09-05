package joechungmsft.apivertx

import io.vertx.core.Vertx
import io.vertx.core.http.HttpServer
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.coAwait
import kotlinx.coroutines.launch
import joechungmsft.jsonkt.shared.parse
import joechungmsft.jsonkt.shared.ValueToken
import joechungmsft.jsonkt.shared.prettyPrint

/**
 * Response structure for API error messages.
 *
 * @property code HTTP status code
 * @property message Human-readable error description
 */
data class ErrorResponse(val code: Int, val message: String)

/**
 * Vert.x-based JSON parsing API server using coroutines.
 *
 * This verticle sets up an HTTP server on port 8000 with a single endpoint:
 * POST /api/v1/parse - Accepts JSON strings and returns formatted JSON responses
 */
class Application : CoroutineVerticle() {

    /**
     * Starts the Vert.x verticle by setting up the HTTP server and routes.
     */
    override suspend fun start() {
        val router = Router.router(vertx)

        // Enable body handling for POST requests
        router.route().handler(BodyHandler.create())

        // API routes
        router.post("/api/v1/parse").handler(this::handleParse)

        // Start the HTTP server
        val server = vertx.createHttpServer()
        server.requestHandler(router).listen(8000).coAwait()

        println("Vert.x server started on port 8000")
    }

    /**
     * Handles POST requests to /api/v1/parse endpoint.
     *
     * Parses the request body as JSON and returns a formatted JSON response.
     * Returns an error response if the JSON is malformed or the request is invalid.
     *
     * @param ctx The routing context containing the HTTP request and response
     */
    private fun handleParse(ctx: RoutingContext) {
        launch {
            try {
                val body = ctx.body().asString()
                if (body.isNullOrBlank()) {
                    ctx.response()
                        .setStatusCode(400)
                        .putHeader("Content-Type", "application/json")
                        .end(JsonObject.mapFrom(ErrorResponse(400, "Request body is required")).encode())
                    return@launch
                }

                val result = parse(body)
                ctx.response()
                    .setStatusCode(200)
                    .putHeader("Content-Type", "application/json")
                    .end(result.token.prettyPrint())
            } catch (e: Exception) {
                ctx.response()
                    .setStatusCode(400)
                    .putHeader("Content-Type", "application/json")
                    .end(JsonObject().put("code", 400).put("message", e.message ?: "Parse error").encode())
            }
        }
    }
}

/**
 * Main entry point for the Vert.x-based JSON parsing API server.
 *
 * Creates a Vert.x instance, deploys the Application verticle, and starts the server.
 */
suspend fun main() {
    val vertx = Vertx.vertx()
    val deploymentId = vertx.deployVerticle(Application()).coAwait()
    println("Deployed verticle: $deploymentId")
}
