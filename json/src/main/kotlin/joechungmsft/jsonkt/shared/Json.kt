package joechungmsft.jsonkt.shared

enum class Type {
    Unknown,
    Array,
    False,
    Null,
    Number,
    Pair,
    Object,
    String,
    True,
    Value,
}

sealed interface Token {
    val type: Type
}

data class ArrayToken(
    override val type: Type = Type.Array,
    val values: List<ValueToken>,
) : Token

data class FalseToken(
    override val type: Type = Type.False,
    val value: Boolean,
) : Token

data class NullToken(
    override val type: Type = Type.Null,
    val value: Nothing? = null,
) : Token

data class NumberToken(
    override val type: Type = Type.Number,
    val value: Double?,
) : Token

data class ObjectToken(
    override val type: Type = Type.Object,
    val members: List<PairToken>,
) : Token

data class PairToken(
    override val type: Type = Type.Pair,
    val key: StringToken?,
    val value: ValueToken?,
) : Token

data class StringToken(
    override val type: Type = Type.String,
    val value: String?,
) : Token

data class TrueToken(
    override val type: Type = Type.True,
    val value: Boolean,
) : Token

typealias ValueToken = Token

private enum class Mode {
    End,
    Scanning,
    Value,
}

data class ParseResult(
    val skip: Int,
    val token: ValueToken?,
)

fun parse(expression: String): ParseResult {
    var mode = Mode.Scanning
    var pos = 0
    var token: ValueToken? = null

    while (pos < expression.length && mode != Mode.End) {
        val ch = expression[pos]

        when (mode) {
            Mode.Scanning -> {
                if (ch.isWhitespace()) {
                    pos++
                } else {
                    mode = Mode.Value
                }
            }
            Mode.Value -> {
                val slice = expression.substring(pos)
                val value = parseValue(slice)
                token = value.token
                pos += value.skip
                mode = Mode.End
            }
            Mode.End -> {
                // BUG This should not happen as we should have already exited the loop
            }
        }
    }

    return ParseResult(skip = pos, token = token)
}
