package joechungmsft.jsonkt.shared

enum class PairMode {
    Colon,
    End,
    Scanning,
    String,
    Value,
}

data class PairParseResult(
    val skip: Int,
    val token: PairToken,
)

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
            PairMode.End -> {
                // This case is handled in PairMode.Value when we encounter the end of the value
            }
        }
    }

    if (mode != PairMode.End) {
        throw SyntaxError("incomplete expression, mode $mode")
    }

    return PairParseResult(skip = pos, token = PairToken(key = key, value = value))
}
