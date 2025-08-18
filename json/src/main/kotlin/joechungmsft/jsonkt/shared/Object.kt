package joechungmsft.jsonkt.shared

enum class ObjectMode {
    Delimiter,
    End,
    Pair,
    Scanning,
}

data class ObjectParseResult(
    val skip: Int,
    val token: ObjectToken,
)

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
