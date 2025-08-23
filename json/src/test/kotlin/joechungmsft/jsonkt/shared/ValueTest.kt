package joechungmsft.jsonkt.shared

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ValueTest {
    @Test
    fun testFalseValue() {
        val result = parse("false")
        assertTrue(result.token is FalseToken)
        assertEquals(false, (result.token as FalseToken).value)
    }

    @Test
    fun testTrueValue() {
        val result = parse("true")
        assertTrue(result.token is TrueToken)
        assertEquals(true, (result.token as TrueToken).value)
    }

    @Test
    fun testNullValue() {
        val result = parse("null")
        assertTrue(result.token is NullToken)
        assertNull((result.token as NullToken).value)
    }

    @Test
    fun testNumberValue() {
        val result = parse("123.45")
        assertTrue(result.token is NumberToken)
        assertEquals(123.45, (result.token as NumberToken).value)
    }

    @Test
    fun testStringValue() {
        val result = parse("\"json\"")
        assertTrue(result.token is StringToken)
        assertEquals("json", (result.token as StringToken).value)
    }

    @Test
    fun testArrayValue() {
        val result = parse("[1, \"a\", true, null]")
        assertTrue(result.token is ArrayToken)
        val values = (result.token as ArrayToken).values
        assertEquals(4, values.size)
        assertTrue(values[0] is NumberToken)
        assertTrue(values[1] is StringToken)
        assertTrue(values[2] is TrueToken)
        assertTrue(values[3] is NullToken)
    }

    @Test
    fun testNestedArrayValue() {
        val result = parse("[[1, [2, [3]]], {\"a\": [4, 5]}]")
        assertTrue(result.token is ArrayToken)
        val arr = result.token as ArrayToken
        assertEquals(2, arr.values.size)
        val arr1 = arr.values[0] as ArrayToken
        assertEquals(2, arr1.values.size)
        assertEquals(1.0, (arr1.values[0] as NumberToken).value)
        val arr2 = arr1.values[1] as ArrayToken
        assertEquals(2.0, (arr2.values[0] as NumberToken).value)
        val arr3 = arr2.values[1] as ArrayToken
        assertEquals(3.0, (arr3.values[0] as NumberToken).value)
        val obj = arr.values[1] as ObjectToken
        assertEquals("a", obj.members[0].key?.value)
        val arrA = obj.members[0].value as ArrayToken
        assertEquals(4.0, (arrA.values[0] as NumberToken).value)
        assertEquals(5.0, (arrA.values[1] as NumberToken).value)
    }

    @Test
    fun testNestedObjectValue() {
        val result = parse("{\"x\": {\"y\": [1, {\"z\": 2}]}}")
        assertTrue(result.token is ObjectToken)
        val objX = result.token as ObjectToken
        val objY = objX.members[0].value as ObjectToken
        assertEquals("y", objY.members[0].key?.value)
        val arrY = objY.members[0].value as ArrayToken
        assertEquals(2, arrY.values.size)
        assertEquals(1.0, (arrY.values[0] as NumberToken).value)
        val objZ = arrY.values[1] as ObjectToken
        assertEquals("z", objZ.members[0].key?.value)
        assertEquals(2.0, (objZ.members[0].value as NumberToken).value)
    }

    @Test
    fun testObjectValue() {
        val result = parse("{\"x\": 1, \"y\": \"z\"}")
        assertTrue(result.token is ObjectToken)
        val members = (result.token as ObjectToken).members
        assertEquals(2, members.size)
        assertEquals("x", members[0].key?.value)
        assertTrue(members[0].value is NumberToken)
        assertEquals("y", members[1].key?.value)
        assertTrue(members[1].value is StringToken)
    }

    @Test
    fun testWhitespace() {
        val result = parse("   true   ")
        assertTrue(result.token is TrueToken)
        assertEquals(true, (result.token as TrueToken).value)
    }

    @Test
    fun testMalformedValue() {
        assertThrows(SyntaxError::class.java) {
            parse("{\"x\": }")
        }
    }
}
