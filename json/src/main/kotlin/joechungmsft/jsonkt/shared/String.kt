package joechungmsft.jsonkt.shared

import joechungmsft.jsonkt.shared.StringToken
import joechungmsft.jsonkt.shared.SyntaxError
import joechungmsft.jsonkt.shared.Type

enum class StringMode {
    Char,
    End,
    EscapedChar,
    Scanning,
    Unicode,
}

data class StringParseResult(
    val skip: Int,
    val token: StringToken,
)

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
