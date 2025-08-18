package joechungmsft.jsonkt.shared

enum class ArrayMode {
    Comma,
    Elements,
    End,
    Scanning,
}

data class ArrayParseResult(
    val skip: Int,
    val token: ArrayToken,
)

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
                        throw joechungmsft.jsonkt.shared.SyntaxError(
                            "expected '[', actual '$ch'",
                        )
                }
            }
            ArrayMode.Elements -> {
                when {
                    ch.isWhitespace() -> pos++
                    ch == ']' -> {
                        if (values.isNotEmpty()) {
                            throw joechungmsft.jsonkt.shared.SyntaxError("unexpected ','")
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
                        throw joechungmsft.jsonkt.shared.SyntaxError(
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
        throw joechungmsft.jsonkt.shared.SyntaxError("incomplete expression, mode $mode")
    }

    return ArrayParseResult(skip = pos, token = ArrayToken(values = values))
}
