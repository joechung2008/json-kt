package com.github.jsonkt.shared

/**
 * Represents the different types of JSON tokens that can be parsed.
 */
enum class Type {
    Array,
    False,
    Null,
    Number,
    Pair,
    Object,
    String,
    True,
}

/**
 * Base interface for all JSON token types in the parse tree.
 * Each token has a specific [Type] that identifies what kind of JSON value it represents.
 */
sealed interface Token {
    val type: Type
}

/**
 * Represents a JSON array token containing a list of values.
 *
 * @property values The list of JSON values contained in this array
 */
data class ArrayToken(
    override val type: Type = Type.Array,
    val values: List<ValueToken>,
) : Token

/**
 * Represents a JSON boolean false token.
 *
 * @property value Always false for this token type
 */
data class FalseToken(
    override val type: Type = Type.False,
    val value: Boolean,
) : Token

/**
 * Represents a JSON null token.
 *
 * @property value Always null for this token type
 */
data class NullToken(
    override val type: Type = Type.Null,
    val value: Nothing? = null,
) : Token

/**
 * Represents a JSON number token.
 *
 * @property value The numeric value as a Double, or null if parsing failed
 */
data class NumberToken(
    override val type: Type = Type.Number,
    val value: Double?,
) : Token

/**
 * Represents a JSON object token containing key-value pairs.
 *
 * @property members The list of key-value pairs contained in this object
 */
data class ObjectToken(
    override val type: Type = Type.Object,
    val members: List<PairToken>,
) : Token

/**
 * Represents a key-value pair within a JSON object.
 *
 * @property key The string key of the pair
 * @property value The JSON value associated with the key
 */
data class PairToken(
    val type: Type = Type.Pair,
    val key: StringToken?,
    val value: ValueToken?,
)

/**
 * Represents a JSON string token.
 *
 * @property value The string value, or null if the string was malformed
 */
data class StringToken(
    override val type: Type = Type.String,
    val value: String?,
) : Token

/**
 * Represents a JSON boolean true token.
 *
 * @property value Always true for this token type
 */
data class TrueToken(
    override val type: Type = Type.True,
    val value: Boolean,
) : Token

/**
 * Type alias for any JSON token that can be a value (everything except Pair tokens).
 */
typealias ValueToken = Token

/**
 * Internal parsing modes for the main JSON parsing state machine.
 */
private enum class Mode {
    End,
    Scanning,
    Value,
}

/**
 * Result of a JSON parsing operation.
 *
 * @property skip The number of characters consumed from the input string
 * @property token The parsed JSON token (never null for successful parsing)
 */
data class ParseResult(
    val skip: Int,
    val token: ValueToken,
)

/**
 * Parses a JSON string into a token tree structure.
 *
 * This function takes a JSON string and converts it into a hierarchical token structure
 * that represents the JSON data. It handles all standard JSON value types including
 * objects, arrays, strings, numbers, booleans, and null.
 *
 * @param expression The JSON string to parse
 * @return A [ParseResult] containing the parsed token tree and the number of characters consumed
 * @throws SyntaxError if the JSON string is malformed, contains invalid syntax, or is empty
 */
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
            else -> {
                // BUG This should not happen as we should have already exited the loop
            }
        }
    }

    // Throw SyntaxError if parsing failed (token is null)
    if (token == null) {
        throw SyntaxError("Failed to parse JSON: invalid or empty input")
    }

    return ParseResult(skip = pos, token = token)
}

/**
 * Formats a JSON token tree into a human-readable string with proper indentation.
 *
 * This extension function converts a parsed JSON token back into a formatted JSON string
 * with consistent indentation and line breaks for better readability. It's the inverse
 * operation of [parse].
 *
 * @param indent The number of spaces to use for indentation (default: 0)
 * @return A formatted JSON string with proper indentation and structure
 */
fun ValueToken.prettyPrint(indent: Int = 0): String {
    val indentStr = "  ".repeat(indent)
    return when (this) {
        is ObjectToken -> {
            if (members.isEmpty()) return "{}"
            val content =
                members.joinToString(",\n") { pair ->
                    val key = pair.key?.value ?: ""
                    val value = pair.value?.prettyPrint(indent + 1) ?: "null"
                    "$indentStr  \"$key\": $value"
                }
            "{\n$content\n$indentStr}"
        }
        is ArrayToken -> {
            if (values.isEmpty()) return "[]"
            val content =
                values.joinToString(",\n") { v -> "$indentStr  ${v.prettyPrint(indent + 1)}" }
            "[\n$content\n$indentStr]"
        }
        is StringToken -> "\"${value ?: ""}\""
        is NumberToken -> value?.toString() ?: "null"
        is TrueToken -> "true"
        is FalseToken -> "false"
        is NullToken -> "null"
    }
}
