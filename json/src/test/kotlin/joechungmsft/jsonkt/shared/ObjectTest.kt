package joechungmsft.jsonkt.shared

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ObjectTest {
    @Test
    fun testEmptyObject() {
        val result = parse("{}")
        assertNotNull(result.token)
        assertTrue(result.token is ObjectToken)
        assertTrue((result.token as ObjectToken).members.isEmpty())
    }

    @Test
    fun testObjectWhitespaceAroundKeyValueColon() {
        val result = parse("{  \"a\"   :   1  ,   \"b\" : \"x\"  }")
        assertNotNull(result.token)
        val obj = result.token as ObjectToken
        assertEquals(2, obj.members.size)
        assertEquals("a", obj.members[0].key?.value)
        assertTrue(obj.members[0].value is NumberToken)
        assertEquals(1.0, (obj.members[0].value as NumberToken).value)
        assertEquals("b", obj.members[1].key?.value)
        assertTrue(obj.members[1].value is StringToken)
        assertEquals("x", (obj.members[1].value as StringToken).value)
    }

    @Test
    fun testObjectWhitespaceOutsideBraces() {
        val result = parse("   { \"k\" : \"v\" }   ")
        assertNotNull(result.token)
        val obj = result.token as ObjectToken
        assertEquals(1, obj.members.size)
        assertEquals("k", obj.members[0].key?.value)
        assertEquals("v", (obj.members[0].value as StringToken).value)
    }

    @Test
    fun testObjectWhitespaceEverywhere() {
        val result = parse(" {   \"x\"   :   42   ,   \"y\"   :   true   } ")
        assertNotNull(result.token)
        val obj = result.token as ObjectToken
        assertEquals(2, obj.members.size)
        assertEquals("x", obj.members[0].key?.value)
        assertEquals(42.0, (obj.members[0].value as NumberToken).value)
        assertEquals("y", obj.members[1].key?.value)
        assertTrue(obj.members[1].value is TrueToken)
    }

    @Test
    fun testSimpleObject() {
        val result = parse("{\"a\": 1}")
        assertNotNull(result.token)
        val obj = result.token as ObjectToken
        assertEquals(1, obj.members.size)
        val pair = obj.members[0]
        assertEquals("a", pair.key?.value)
        assertTrue(pair.value is NumberToken)
        assertEquals(1.0, (pair.value as NumberToken).value)
    }

    @Test
    fun testNestedObject() {
        val result = parse("{\"a\": {\"b\": true}}")
        assertNotNull(result.token)
        val obj = result.token as ObjectToken
        assertEquals("a", obj.members[0].key?.value)
        val innerObj = obj.members[0].value as ObjectToken
        assertEquals("b", innerObj.members[0].key?.value)
        assertTrue(innerObj.members[0].value is TrueToken)
    }

    @Test
    fun testObjectWithNestedArray() {
        val result = parse("{\"arr\": [1, {\"x\": [2, 3]}, 4]}")
        assertNotNull(result.token)
        val obj = result.token as ObjectToken
        assertEquals(1, obj.members.size)
        assertEquals("arr", obj.members[0].key?.value)
        val arr = obj.members[0].value as ArrayToken
        assertEquals(3, arr.values.size)
        assertEquals(1.0, (arr.values[0] as NumberToken).value)
        val nestedObj = arr.values[1] as ObjectToken
        assertEquals("x", nestedObj.members[0].key?.value)
        val nestedArr = nestedObj.members[0].value as ArrayToken
        assertEquals(2.0, (nestedArr.values[0] as NumberToken).value)
        assertEquals(3.0, (nestedArr.values[1] as NumberToken).value)
        assertEquals(4.0, (arr.values[2] as NumberToken).value)
    }

    @Test
    fun testDeeplyNestedObject() {
        val result = parse("{\"a\": {\"b\": {\"c\": {\"d\": [1, {\"e\": 2}]}}}}")
        assertNotNull(result.token)
        val objA = result.token as ObjectToken
        val objB = objA.members[0].value as ObjectToken
        val objC = objB.members[0].value as ObjectToken
        val objD = objC.members[0].value as ObjectToken
        val arrD = objD.members[0].value as ArrayToken
        assertEquals(2, arrD.values.size)
        assertEquals(1.0, (arrD.values[0] as NumberToken).value)
        val objE = arrD.values[1] as ObjectToken
        assertEquals("e", objE.members[0].key?.value)
        assertEquals(2.0, (objE.members[0].value as NumberToken).value)
    }

    @Test
    fun testInvalidJson() {
        assertThrows(SyntaxError::class.java) {
            parse("{unquoted: value}")
        }
    }

    @Test
    fun testIncompleteJson() {
        assertThrows(SyntaxError::class.java) {
            parse("{\"a\":")
        }
    }
}
