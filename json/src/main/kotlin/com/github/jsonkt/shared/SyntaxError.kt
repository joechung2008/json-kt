package com.github.jsonkt.shared

/**
 * Exception thrown when JSON parsing encounters invalid syntax.
 *
 * This exception is used throughout the JSON parsing library to indicate
 * malformed JSON input or unexpected characters during parsing.
 *
 * @param message A description of the syntax error that occurred
 */
class SyntaxError(
    message: String,
) : Exception(message)
