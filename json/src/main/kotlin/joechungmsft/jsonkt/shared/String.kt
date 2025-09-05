package joechungmsft.jsonkt.shared

import joechungmsft.jsonkt.shared.StringToken
import joechungmsft.jsonkt.shared.SyntaxError
import joechungmsft.jsonkt.shared.Type

/**
 * Parsing modes for JSON string syntax, supporting escape sequences and Unicode characters.
 */
enum class StringMode {
    Char,
    End,
    EscapedChar,
    Scanning,
    Unicode,
}

/**
 * Result of parsing a JSON string.
 *
 * @property skip The number of characters consumed from the input string
 * @property token The parsed string token
 */
data class StringParseResult(
    val skip: Int,
    val token: StringToken,
)

/**
 * Parses a JSON string from the beginning of the input string.
 *
 * This function supports the full JSON string specification including:
 * - Basic strings: "hello world"
 * - Escape sequences: \" \\ \/ \b \f \n \r \t
 * - Unicode characters: \u0041 (for 'A')
 *
 * @param expression The input string containing JSON to parse
 * @return A [StringParseResult] containing the parsed string and characters consumed
 * @throws SyntaxError if the string syntax is malformed or contains invalid escape sequences
 */
fun parseString(expression: String): StringParseResult {
    var mode = StringMode.Scanning
    var pos = 0
    var value: String? = null

    while (pos < expression.length && mode != StringMode.End) {
        val ch = expression[pos]

        when (mode) {
            StringMode.Scanning -> {
                if (ch.isWhitespace()) {
                    pos++
                } else if (ch == '"') {
                    value = ""
                    pos++
                    mode = StringMode.Char
                } else {
                    throw SyntaxError("expected '\"', actual '$ch'")
                }
            }
            StringMode.Char -> {
                when {
                    ch == '\\' -> {
                        pos++
                        mode = StringMode.EscapedChar
                    }
                    ch == '"' -> {
                        pos++
                        mode = StringMode.End
                    }
                    ch != '\n' && ch != '\r' -> {
                        value += ch
                        pos++
                    }
                    else -> throw SyntaxError("unexpected character '$ch'")
                }
            }
            StringMode.EscapedChar -> {
                when (ch) {
                    '"', '\\', '/' -> {
                        value += ch
                        pos++
                        mode = StringMode.Char
                    }
                    'b' -> {
                        value += '\b'
                        pos++
                        mode = StringMode.Char
                    }
                    'f' -> {
                        value += '\u000C'
                        pos++
                        mode = StringMode.Char
                    }
                    'n' -> {
                        value += '\n'
                        pos++
                        mode = StringMode.Char
                    }
                    'r' -> {
                        value += '\r'
                        pos++
                        mode = StringMode.Char
                    }
                    't' -> {
                        value += '\t'
                        pos++
                        mode = StringMode.Char
                    }
                    'u' -> {
                        pos++
                        mode = StringMode.Unicode
                    }
                    else -> throw SyntaxError("unexpected escape character '$ch'")
                }
            }
            StringMode.Unicode -> {
                val slice = expression.substring(pos, (pos + 4).coerceAtMost(expression.length))
                if (slice.length < 4) {
                    throw SyntaxError("incomplete Unicode code '$slice'")
                }
                val hex = slice.toIntOrNull(16)
                if (hex == null) {
                    throw SyntaxError("unexpected Unicode code '$slice'")
                }
                value += hex.toChar()
                pos += 4
                mode = StringMode.Char
            }
            StringMode.End -> {
                // This case is handled in StringMode.Char when we encounter a closing quote
            }
        }
    }

    if (mode != StringMode.End) {
        throw SyntaxError("incomplete string, mode $mode")
    }

    return StringParseResult(
        skip = pos,
        token = StringToken(value = value),
    )
}
