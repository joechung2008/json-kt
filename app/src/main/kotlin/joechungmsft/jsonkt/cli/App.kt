package joechungmsft.jsonkt.cli

import joechungmsft.jsonkt.shared.parse

fun main() {
    try {
        val input = System.`in`.bufferedReader().readText()
        val result = parse(input)
        println(result.toString())
    } catch (e: Exception) {
        println("Parse error: ${e.message}")
    }
}
