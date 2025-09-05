package joechungmsft.apispringboot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpStatus
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

@SpringBootApplication
open class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}

@RestController
@RequestMapping("/api/v1")
class JsonController {

    @PostMapping("/parse")
    fun parseJson(@RequestBody jsonString: String): ResponseEntity<Any> {
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

