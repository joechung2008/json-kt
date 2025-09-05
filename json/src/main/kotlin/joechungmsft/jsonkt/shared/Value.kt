package joechungmsft.jsonkt.shared

/**
 * Parsing modes for JSON value types.
 */
enum class ValueMode {
    Array,
    End,
    False,
    Null,
    Number,
    Object,
    String,
    Scanning,
    True,
}

/**
 * Result of parsing a JSON value.
 *
 * @property skip The number of characters consumed from the input string
 * @property token The parsed value token, or null if parsing failed
 */
data class ValueParseResult(
    val skip: Int,
    val token: ValueToken?,
)

/**
 * Parses any JSON value from the beginning of the input string.
 *
 * This is the main entry point for parsing individual JSON values. It automatically
 * detects the type of value (array, object, string, number, boolean, or null) and
 * delegates to the appropriate parsing function.
 *
 * @param expression The input string containing JSON to parse
 * @param delimiters Optional regex pattern defining characters that terminate parsing
 * @return A [ValueParseResult] containing the parsed value and characters consumed
 * @throws SyntaxError if the value syntax is malformed or unrecognized
 */
fun parseValue(
    expression: String,
    delimiters: Regex? = null,
): ValueParseResult {
    var mode = ValueMode.Scanning
    var pos = 0
    var token: ValueToken? = null

    while (pos < expression.length && mode != ValueMode.End) {
        val ch = expression[pos]

        when (mode) {
            ValueMode.Scanning -> {
                when {
                    ch.isWhitespace() -> pos++
                    ch == '[' -> mode = ValueMode.Array
                    ch == 'f' -> mode = ValueMode.False
                    ch == 'n' -> mode = ValueMode.Null
                    ch == '-' || ch.isDigit() -> mode = ValueMode.Number
                    ch == '{' -> mode = ValueMode.Object
                    ch == '"' -> mode = ValueMode.String
                    ch == 't' -> mode = ValueMode.True
                    delimiters?.containsMatchIn(ch.toString()) == true -> mode = ValueMode.End
                    else ->
                        throw joechungmsft.jsonkt.shared.SyntaxError(
                            "expected array, false, null, number, object, string, or true, actual '$ch'",
                        )
                }
            }
            ValueMode.Array -> {
                val slice = expression.substring(pos)
                val array = parseArray(slice)
                token = array.token
                pos += array.skip
                mode = ValueMode.End
            }
            ValueMode.False -> {
                val slice = expression.substring(pos, (pos + 5).coerceAtMost(expression.length))
                if (slice == "false") {
                    token = FalseToken(value = false)
                    pos += 5
                    mode = ValueMode.End
                } else {
                    throw joechungmsft.jsonkt.shared.SyntaxError("expected false, actual $slice")
                }
            }
            ValueMode.Null -> {
                val slice = expression.substring(pos, (pos + 4).coerceAtMost(expression.length))
                if (slice == "null") {
                    token = NullToken()
                    pos += 4
                    mode = ValueMode.End
                } else {
                    throw joechungmsft.jsonkt.shared.SyntaxError("expected null, actual $slice")
                }
            }
            ValueMode.Number -> {
                val slice = expression.substring(pos)
                val number =
                    if (delimiters != null) {
                        parseNumber(slice, delimiters)
                    } else {
                        parseNumber(slice)
                    }
                token = number.token
                pos += number.skip
                mode = ValueMode.End
            }
            ValueMode.Object -> {
                val slice = expression.substring(pos)
                val obj = parseObject(slice)
                token = obj.token
                pos += obj.skip
                mode = ValueMode.End
            }
            ValueMode.String -> {
                val slice = expression.substring(pos)
                val str = parseString(slice)
                token = str.token
                pos += str.skip
                mode = ValueMode.End
            }
            ValueMode.True -> {
                val slice = expression.substring(pos, (pos + 4).coerceAtMost(expression.length))
                if (slice == "true") {
                    token = TrueToken(value = true)
                    pos += 4
                    mode = ValueMode.End
                } else {
                    throw joechungmsft.jsonkt.shared.SyntaxError("expected true, actual $slice")
                }
            }
            ValueMode.End -> {
                if (delimiters?.containsMatchIn(ch.toString()) == true) {
                    pos++
                } else {
                    throw joechungmsft.jsonkt.shared.SyntaxError(
                        "unexpected character '$ch' after value",
                    )
                }
            }
        }
    }

    if (token == null) {
        throw joechungmsft.jsonkt.shared.SyntaxError("value cannot be empty")
    }

    return ValueParseResult(skip = pos, token = token)
}
