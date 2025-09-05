package joechungmsft.apispringboot

import joechungmsft.jsonkt.shared.parse
import joechungmsft.jsonkt.shared.prettyPrint
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Response structure for API error messages.
 *
 * @property code HTTP status code
 * @property message Human-readable error description
 */
data class ErrorResponse(
    val code: Int,
    val message: String,
)

@SpringBootApplication
/**
 * Main Spring Boot application class for the JSON parsing API.
 */
open class Application

/**
 * Main entry point for the Spring Boot application.
 *
 * @param args Command line arguments passed to the application.
 */
fun main(args: Array<String>) {
    runApplication<Application>(*args)
}

/**
 * REST controller for JSON parsing operations.
 */
@RestController
@RequestMapping("/api/v1")
class JsonController {
    /**
     * Parses a JSON string and returns the formatted result.
     *
     * @param jsonString The JSON string to parse.
     * @return A ResponseEntity containing the formatted JSON or an error response.
     */
    @PostMapping("/parse")
    fun parseJson(
        @RequestBody jsonString: String,
    ): ResponseEntity<Any> {
        return try {
            // Validate request body
            if (jsonString.isBlank()) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .header("Content-Type", "application/json")
                    .body(ErrorResponse(400, "Request body is required"))
            }

            val result = parse(jsonString)
            ResponseEntity
                .ok()
                .header("Content-Type", "application/json")
                .body(result.token.prettyPrint())
        } catch (e: Exception) {
            ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .header("Content-Type", "application/json")
                .body(ErrorResponse(400, e.message ?: "Parse error"))
        }
    }
}
