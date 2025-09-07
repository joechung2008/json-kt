package com.github.jsonkt.shared

/**
 * Parsing modes for JSON array syntax.
 */
enum class ArrayMode {
    Comma,
    Elements,
    End,
    Scanning,
}

/**
 * Result of parsing a JSON array.
 *
 * @property skip The number of characters consumed from the input string
 * @property token The parsed array token
 */
data class ArrayParseResult(
    val skip: Int,
    val token: ArrayToken,
)

/**
 * Parses a JSON array from the beginning of the input string.
 *
 * This function expects the input to start with '[' and parses until the matching ']'.
 * It handles arrays containing any valid JSON values separated by commas.
 *
 * @param expression The input string containing JSON to parse
 * @return An [ArrayParseResult] containing the parsed array and characters consumed
 * @throws SyntaxError if the array syntax is malformed
 */
fun parseArray(expression: String): ArrayParseResult {
    var mode = ArrayMode.Scanning
    var pos = 0
    val values = mutableListOf<ValueToken>()

    while (pos < expression.length && mode != ArrayMode.End) {
        val ch = expression[pos]

        when (mode) {
            ArrayMode.Scanning -> {
                when {
                    ch.isWhitespace() -> pos++
                    ch == '[' -> {
                        pos++
                        mode = ArrayMode.Elements
                    }
                    else ->
                        throw com.github.jsonkt.shared.SyntaxError(
                            "expected '[', actual '$ch'",
                        )
                }
            }
            ArrayMode.Elements -> {
                when {
                    ch.isWhitespace() -> pos++
                    ch == ']' -> {
                        if (values.isNotEmpty()) {
                            throw com.github.jsonkt.shared
                                .SyntaxError("unexpected ','")
                        }
                        pos++
                        mode = ArrayMode.End
                    }
                    else -> {
                        val slice = expression.substring(pos)
                        val value = parseValue(slice, Regex("[ \\n\\r\\t\\],]"))
                        values.add(value.token!!)
                        pos += value.skip
                        mode = ArrayMode.Comma
                    }
                }
            }
            ArrayMode.Comma -> {
                when {
                    ch.isWhitespace() -> pos++
                    ch == ']' -> {
                        pos++
                        mode = ArrayMode.End
                    }
                    ch == ',' -> {
                        pos++
                        mode = ArrayMode.Elements
                    }
                    else ->
                        throw com.github.jsonkt.shared.SyntaxError(
                            "expected ',', actual '$ch'",
                        )
                }
            }
            ArrayMode.End -> {
                // BUG This should not happen as we should have already exited the loop
            }
        }
    }

    if (mode != ArrayMode.End) {
        throw com.github.jsonkt.shared
            .SyntaxError("incomplete expression, mode $mode")
    }

    return ArrayParseResult(skip = pos, token = ArrayToken(values = values))
}
