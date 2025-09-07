package com.github.jsonkt.shared

/**
 * Parsing modes for JSON number syntax, supporting the full JSON number specification
 * including integers, decimals, and scientific notation.
 */
enum class NumberMode {
    Characteristic,
    CharacteristicDigit,
    DecimalPoint,
    End,
    Exponent,
    ExponentDigits,
    ExponentFirstDigit,
    ExponentSign,
    Mantissa,
    Scanning,
}

/**
 * Result of parsing a JSON number.
 *
 * @property skip The number of characters consumed from the input string
 * @property token The parsed number token
 */
data class NumberParseResult(
    val skip: Int,
    val token: NumberToken,
)

/**
 * Parses a JSON number from the beginning of the input string.
 *
 * This function supports the full JSON number specification including:
 * - Integers: 42, -123
 * - Decimals: 3.14, -0.5
 * - Scientific notation: 1.23e4, 5.67E-8
 *
 * @param expression The input string containing JSON to parse
 * @param delimiters Regex pattern defining characters that terminate number parsing
 * @return A [NumberParseResult] containing the parsed number and characters consumed
 * @throws SyntaxError if the number syntax is malformed
 */
fun parseNumber(
    expression: String,
    delimiters: Regex = Regex("[ \\n\\r\\t]"),
): NumberParseResult {
    var mode = NumberMode.Scanning
    var pos = 0
    var valueAsString = ""
    var value: Double?

    while (pos < expression.length && mode != NumberMode.End) {
        val ch = expression[pos]

        when (mode) {
            NumberMode.Scanning -> {
                when {
                    ch.isWhitespace() -> pos++
                    ch == '-' -> {
                        valueAsString = "-"
                        pos++
                        mode = NumberMode.Characteristic
                    }
                    else -> mode = NumberMode.Characteristic
                }
            }
            NumberMode.Characteristic -> {
                when {
                    ch == '0' -> {
                        valueAsString += "0"
                        pos++
                        mode = NumberMode.DecimalPoint
                    }
                    ch in '1'..'9' -> {
                        valueAsString += ch
                        pos++
                        mode = NumberMode.CharacteristicDigit
                    }
                    else -> throw SyntaxError("Expected digit, actual '$ch'")
                }
            }
            NumberMode.CharacteristicDigit -> {
                when {
                    ch.isDigit() -> {
                        valueAsString += ch
                        pos++
                    }
                    delimiters.containsMatchIn(ch.toString()) -> mode = NumberMode.End
                    else -> mode = NumberMode.DecimalPoint
                }
            }
            NumberMode.DecimalPoint -> {
                when {
                    ch == '.' -> {
                        valueAsString += "."
                        pos++
                        mode = NumberMode.Mantissa
                    }
                    delimiters.containsMatchIn(ch.toString()) -> mode = NumberMode.End
                    else -> mode = NumberMode.Exponent
                }
            }
            NumberMode.Mantissa -> {
                when {
                    ch.isDigit() -> {
                        valueAsString += ch
                        pos++
                    }
                    ch == 'e' || ch == 'E' -> mode = NumberMode.Exponent
                    delimiters.containsMatchIn(ch.toString()) -> mode = NumberMode.End
                    else -> throw SyntaxError("unexpected character '$ch'")
                }
            }
            NumberMode.Exponent -> {
                when {
                    ch == 'e' || ch == 'E' -> {
                        valueAsString += "e"
                        pos++
                        mode = NumberMode.ExponentSign
                    }
                    else -> throw SyntaxError("expected 'e' or 'E', actual '$ch'")
                }
            }
            NumberMode.ExponentSign -> {
                when {
                    ch == '+' || ch == '-' -> {
                        valueAsString += ch
                        pos++
                        mode = NumberMode.ExponentFirstDigit
                    }
                    else -> mode = NumberMode.ExponentFirstDigit
                }
            }
            NumberMode.ExponentFirstDigit -> {
                when {
                    ch.isDigit() -> {
                        valueAsString += ch
                        pos++
                        mode = NumberMode.ExponentDigits
                    }
                    else -> throw SyntaxError("expected digit, actual '$ch'")
                }
            }
            NumberMode.ExponentDigits -> {
                when {
                    ch.isDigit() -> {
                        valueAsString += ch
                        pos++
                    }
                    delimiters.containsMatchIn(ch.toString()) -> mode = NumberMode.End
                    else -> throw SyntaxError("expected digit, actual '$ch'")
                }
            }
            NumberMode.End -> {
                // BUG This should not happen as we should have already exited the loop
            }
        }
    }

    when (mode) {
        NumberMode.Characteristic, NumberMode.ExponentFirstDigit ->
            throw SyntaxError("incomplete expression, mode $mode")
        else -> value = valueAsString.toDoubleOrNull()
    }

    return NumberParseResult(skip = pos, token = NumberToken(value = value))
}
