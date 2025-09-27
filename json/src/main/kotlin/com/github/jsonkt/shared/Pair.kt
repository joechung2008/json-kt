package com.github.jsonkt.shared

/**
 * Parsing modes for JSON key-value pair syntax.
 */
enum class PairMode {
    Colon,
    End,
    Scanning,
    String,
    Value,
}

/**
 * Result of parsing a JSON key-value pair.
 *
 * @property skip The number of characters consumed from the input string
 * @property token The parsed pair token
 */
data class PairParseResult(
    val skip: Int,
    val token: PairToken,
)

/**
 * Parses a JSON key-value pair from the beginning of the input string.
 *
 * This function expects the format "key": value and parses both the string key
 * and the associated JSON value. Keys must be valid JSON strings, and values
 * can be any valid JSON type.
 *
 * @param expression The input string containing JSON to parse
 * @param delimiters Optional regex pattern defining characters that terminate parsing
 * @return A [PairParseResult] containing the parsed pair and characters consumed
 * @throws SyntaxError if the pair syntax is malformed
 */
fun parsePair(
    expression: String,
    delimiters: Regex? = null,
): PairParseResult {
    var mode = PairMode.Scanning
    var pos = 0
    var key: StringToken? = null
    var value: ValueToken? = null

    while (pos < expression.length && mode != PairMode.End) {
        val ch = expression[pos]

        when (mode) {
            PairMode.Scanning -> {
                if (ch.isWhitespace()) {
                    pos++
                } else {
                    mode = PairMode.String
                }
            }
            PairMode.String -> {
                val slice = expression.substring(pos)
                val string = parseString(slice)
                key = string.token
                pos += string.skip
                mode = PairMode.Colon
            }
            PairMode.Colon -> {
                if (ch.isWhitespace()) {
                    pos++
                } else if (ch == ':') {
                    pos++
                    mode = PairMode.Value
                } else {
                    throw SyntaxError("expected ':', actual '$ch'")
                }
            }
            PairMode.Value -> {
                val slice = expression.substring(pos)
                val valueResult = parseValue(slice, delimiters)
                value = valueResult.token
                pos += valueResult.skip
                mode = PairMode.End
            }
            else -> {
                // This case is handled in PairMode.Value when we encounter the end of the value
            }
        }
    }

    if (mode != PairMode.End) {
        throw SyntaxError("incomplete expression, mode $mode")
    }

    return PairParseResult(skip = pos, token = PairToken(key = key, value = value))
}
