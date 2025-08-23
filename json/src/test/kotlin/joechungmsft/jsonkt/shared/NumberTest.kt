package joechungmsft.jsonkt.shared

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class NumberTest {
    @Test
    fun testParseZero() {
        val result = parse("0")
        val token = result.token
        assertTrue(token is NumberToken)
        assertEquals(0.0, (token as NumberToken).value)
    }

    @Test
    fun testParseOne() {
        val result = parse("1")
        val token = result.token
        assertTrue(token is NumberToken)
        assertEquals(1.0, (token as NumberToken).value)
    }

    @Test
    fun testParseNegativeOne() {
        val result = parse("-1")
        val token = result.token
        assertTrue(token is NumberToken)
        assertEquals(-1.0, (token as NumberToken).value)
    }

    @Test
    fun testParseLargeInteger() {
        val result = parse("123456789")
        val token = result.token
        assertTrue(token is NumberToken)
        assertEquals(123456789.0, (token as NumberToken).value)
    }

    @Test
    fun testParseNegativeLargeInteger() {
        val result = parse("-987654321")
        val token = result.token
        assertTrue(token is NumberToken)
        assertEquals(-987654321.0, (token as NumberToken).value)
    }

    @Test
    fun testParseFloat() {
        val result = parse("3.1415")
        val token = result.token
        assertTrue(token is NumberToken)
        assertEquals(3.1415, (token as NumberToken).value)
    }

    @Test
    fun testParseNegativeFloat() {
        val result = parse("-2.71828")
        val token = result.token
        assertTrue(token is NumberToken)
        assertEquals(-2.71828, (token as NumberToken).value)
    }

    @Test
    fun testParseScientificNotation() {
        val result = parse("1e10")
        val token = result.token
        assertTrue(token is NumberToken)
        assertEquals(1e10, (token as NumberToken).value)
    }

    @Test
    fun testParseNegativeScientificNotation() {
        val result = parse("-1e-10")
        val token = result.token
        assertTrue(token is NumberToken)
        assertEquals(-1e-10, (token as NumberToken).value)
    }

    @Test
    fun testParseLargeScientificNotation() {
        val result = parse("6.022e23")
        val token = result.token
        assertTrue(token is NumberToken)
        assertEquals(6.022e23, (token as NumberToken).value)
    }

    @Test
    fun testParseZeroFloat() {
        val result = parse("0.0")
        val token = result.token
        assertTrue(token is NumberToken)
        assertEquals(0.0, (token as NumberToken).value)
    }

    @Test
    fun testParseNegativeZeroFloat() {
        val result = parse("-0.0")
        val token = result.token
        assertTrue(token is NumberToken)
        assertEquals(-0.0, (token as NumberToken).value)
    }

    @Test
    fun testParseSmallFloat() {
        val result = parse("1.0e-5")
        val token = result.token
        assertTrue(token is NumberToken)
        assertEquals(1.0e-5, (token as NumberToken).value)
    }

    @Test
    fun testParseExponentWithPlus() {
        val result = parse("2E+2")
        val token = result.token
        assertTrue(token is NumberToken)
        assertEquals(2e2, (token as NumberToken).value)
    }

    @Test
    fun testParseMaxDouble() {
        val result = parse("1.7976931348623157e308")
        val token = result.token
        assertTrue(token is NumberToken)
        assertEquals(1.7976931348623157e308, (token as NumberToken).value)
    }

    @Test
    fun testParseMinPositiveDouble() {
        val result = parse("5e-324")
        val token = result.token
        assertTrue(token is NumberToken)
        assertEquals(5e-324, (token as NumberToken).value)
    }
}
