package com.github.jsonkt.cli

import com.github.jsonkt.shared.parse

/**
 * Command-line interface for the JSON parser.
 *
 * This application reads JSON input from standard input (stdin), parses it using
 * the shared JSON parsing library, and outputs the result to standard output (stdout).
 * If parsing fails, it prints an error message to stdout.
 *
 * Usage:
 *   echo '{"key": "value"}' | ./gradlew :app:run
 *   cat input.json | ./gradlew :app:run
 */
fun main() {
    try {
        val input = System.`in`.bufferedReader().readText()
        val result = parse(input)
        println(result.toString())
    } catch (e: Exception) {
        println("Parse error: ${e.message}")
    }
}
