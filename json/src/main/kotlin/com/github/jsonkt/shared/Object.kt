package com.github.jsonkt.shared

/**
 * Parsing modes for JSON object syntax.
 */
enum class ObjectMode {
    Delimiter,
    End,
    Pair,
    Scanning,
}

/**
 * Result of parsing a JSON object.
 *
 * @property skip The number of characters consumed from the input string
 * @property token The parsed object token
 */
data class ObjectParseResult(
    val skip: Int,
    val token: ObjectToken,
)

/**
 * Parses a JSON object from the beginning of the input string.
 *
 * This function expects the input to start with '{' and parses until the matching '}'.
 * It handles objects containing key-value pairs separated by commas, where keys are strings
 * and values are any valid JSON values.
 *
 * @param expression The input string containing JSON to parse
 * @return An [ObjectParseResult] containing the parsed object and characters consumed
 * @throws SyntaxError if the object syntax is malformed
 */
fun parseObject(expression: String): ObjectParseResult {
    var mode = ObjectMode.Scanning
    var pos = 0
    val members = mutableListOf<PairToken>()

    while (pos < expression.length && mode != ObjectMode.End) {
        val ch = expression[pos]

        when (mode) {
            ObjectMode.Scanning -> {
                if (ch.isWhitespace()) {
                    pos++
                } else if (ch == '{') {
                    pos++
                    mode = ObjectMode.Pair
                } else {
                    throw SyntaxError("expected '{', actual '$ch'")
                }
            }
            ObjectMode.Pair -> {
                if (ch.isWhitespace()) {
                    pos++
                } else if (ch == '}') {
                    if (members.isNotEmpty()) {
                        throw SyntaxError("unexpected ','")
                    }
                    pos++
                    mode = ObjectMode.End
                } else {
                    val slice = expression.substring(pos)
                    val pair = parsePair(slice, Regex("[ \\n\\r\\t\\},]"))
                    members.add(pair.token)
                    pos += pair.skip
                    mode = ObjectMode.Delimiter
                }
            }
            ObjectMode.Delimiter -> {
                if (ch.isWhitespace()) {
                    pos++
                } else if (ch == ',') {
                    pos++
                    mode = ObjectMode.Pair
                } else if (ch == '}') {
                    pos++
                    mode = ObjectMode.End
                } else {
                    throw SyntaxError("expected ',' or '}', actual '$ch'")
                }
            }
            ObjectMode.End -> {
                // BUG This should not happen as we should have already exited the loop
            }
        }
    }

    if (mode != ObjectMode.End) {
        throw SyntaxError("incomplete expression, mode $mode")
    }

    return ObjectParseResult(skip = pos, token = ObjectToken(members = members))
}
