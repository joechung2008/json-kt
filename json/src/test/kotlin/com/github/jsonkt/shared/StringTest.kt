package com.github.jsonkt.shared

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class StringTest {
    @Test
    fun testSimpleString() {
        val result = parse("\"hello\"")
        assertTrue(result.token is StringToken)
        assertEquals("hello", (result.token as StringToken).value)
    }

    @Test
    fun testEmptyString() {
        val result = parse("\"\"")
        assertTrue(result.token is StringToken)
        assertEquals("", (result.token as StringToken).value)
    }

    @Test
    fun testEscapedCharacters() {
        val result = parse("\"he\\\"llo\\\\world\\n\"")
        assertTrue(result.token is StringToken)
        assertEquals("he\"llo\\world\n", (result.token as StringToken).value)
    }

    @Test
    fun testUnicode() {
        val result = parse("\"hi\\u0041\"")
        assertTrue(result.token is StringToken)
        assertEquals("hiA", (result.token as StringToken).value)
    }

    @Test
    fun testWhitespaceAroundString() {
        val result = parse("   \"abc\"   ")
        assertTrue(result.token is StringToken)
        assertEquals("abc", (result.token as StringToken).value)
    }

    @Test
    fun testMalformedStringMissingQuote() {
        assertThrows(SyntaxError::class.java) {
            parse("\"abc")
        }
    }

    @Test
    fun testMalformedStringBadEscape() {
        assertThrows(SyntaxError::class.java) {
            parse("\"abc\\x\"")
        }
    }

    @Test
    fun testMalformedStringBadUnicode() {
        assertThrows(SyntaxError::class.java) {
            parse("\"abc\\u12\"")
        }
    }
}
