package com.github.jsonkt.shared

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ArrayTest {
    @Test
    fun `parse empty array`() {
        val result = parse("[]")
        assertNotNull(result.token)
        val array = result.token as ArrayToken
        assertEquals(0, array.values.size)
    }

    @Test
    fun `parse empty array with whitespace`() {
        val result = parse("[   ]")
        assertNotNull(result.token)
        val array = result.token as ArrayToken
        assertEquals(0, array.values.size)
    }

    @Test
    fun `parse array with one element`() {
        val result = parse("[42]")
        assertNotNull(result.token)
        val array = result.token as ArrayToken
        assertEquals(1, array.values.size)
        val num = array.values[0] as NumberToken
        assertEquals(42.0, num.value)
    }

    @Test
    fun `parse array with mixed types`() {
        val result = parse("[1, \"two\", null, true, false, {\"a\":1}, [2]]")
        assertNotNull(result.token)
        val array = result.token as ArrayToken
        assertEquals(7, array.values.size)
        assertEquals(1.0, (array.values[0] as NumberToken).value)
        assertEquals("two", (array.values[1] as StringToken).value)
        assertTrue(array.values[2] is NullToken)
        assertEquals(true, (array.values[3] as TrueToken).value)
        assertEquals(false, (array.values[4] as FalseToken).value)
        val obj = array.values[5] as ObjectToken
        val pair = obj.members[0]
        assertEquals("a", pair.key?.value)
        assertEquals(1.0, (pair.value as NumberToken).value)
        val nestedArr = array.values[6] as ArrayToken
        assertEquals(2.0, (nestedArr.values[0] as NumberToken).value)
    }

    @Test
    fun `parse nested arrays`() {
        val result = parse("[[1,2], [3,4], []]")
        assertNotNull(result.token)
        val array = result.token as ArrayToken
        assertEquals(3, array.values.size)
        val arr1 = array.values[0] as ArrayToken
        assertEquals(1.0, (arr1.values[0] as NumberToken).value)
        assertEquals(2.0, (arr1.values[1] as NumberToken).value)
        val arr2 = array.values[1] as ArrayToken
        assertEquals(3.0, (arr2.values[0] as NumberToken).value)
        assertEquals(4.0, (arr2.values[1] as NumberToken).value)
        val arr3 = array.values[2] as ArrayToken
        assertEquals(0, arr3.values.size)
    }

    @Test
    fun `parse deeply nested array`() {
        val result = parse("[[[[[[1]]]]]]")
        assertNotNull(result.token)
        var array = result.token as ArrayToken
        repeat(5) {
            assertTrue(array.values[0] is ArrayToken)
            array = array.values[0] as ArrayToken
        }
        val num = array.values[0] as NumberToken
        assertEquals(1.0, num.value)
    }

    @Test
    fun `parse array with nested objects`() {
        val result = parse("[{\"a\": {\"b\": [1, 2]}}, {\"c\": {\"d\": {\"e\": 3}}}]")
        assertNotNull(result.token)
        val array = result.token as ArrayToken
        assertEquals(2, array.values.size)
        val obj1 = array.values[0] as ObjectToken
        val pairA = obj1.members[0]
        assertEquals("a", pairA.key?.value)
        val nestedObjB = pairA.value as ObjectToken
        val pairB = nestedObjB.members[0]
        assertEquals("b", pairB.key?.value)
        val nestedArr = pairB.value as ArrayToken
        assertEquals(2, nestedArr.values.size)
        assertEquals(1.0, (nestedArr.values[0] as NumberToken).value)
        assertEquals(2.0, (nestedArr.values[1] as NumberToken).value)

        val obj2 = array.values[1] as ObjectToken
        val pairC = obj2.members[0]
        assertEquals("c", pairC.key?.value)
        val nestedObjD = pairC.value as ObjectToken
        val pairD = nestedObjD.members[0]
        assertEquals("d", pairD.key?.value)
        val nestedObjE = pairD.value as ObjectToken
        val pairE = nestedObjE.members[0]
        assertEquals("e", pairE.key?.value)
        assertEquals(3.0, (pairE.value as NumberToken).value)
    }

    @Test
    fun `parse array with objects`() {
        val result = parse("[{\"x\":1}, {\"y\":2}]")
        assertNotNull(result.token)
        val array = result.token as ArrayToken
        assertEquals(2, array.values.size)
        val obj1 = array.values[0] as ObjectToken
        val pair1 = obj1.members[0]
        assertEquals("x", pair1.key?.value)
        assertEquals(1.0, (pair1.value as NumberToken).value)
        val obj2 = array.values[1] as ObjectToken
        val pair2 = obj2.members[0]
        assertEquals("y", pair2.key?.value)
        assertEquals(2.0, (pair2.value as NumberToken).value)
    }

    @Test
    fun `parse array with whitespace and newlines`() {
        val result =
            parse(
                """
                [
                    1,
                    2,
                    3
                ]
                """.trimIndent(),
            )
        assertNotNull(result.token)
        val array = result.token as ArrayToken
        assertEquals(3, array.values.size)
        assertEquals(1.0, (array.values[0] as NumberToken).value)
        assertEquals(2.0, (array.values[1] as NumberToken).value)
        assertEquals(3.0, (array.values[2] as NumberToken).value)
    }

    @Test
    fun `parse array with Unicode and escapes`() {
        val result = parse("""["", "\n", "\\t"]""")
        assertNotNull(result.token)
        val array = result.token as ArrayToken
        assertEquals(3, array.values.size)
        assertEquals("", (array.values[0] as StringToken).value)
        assertEquals("\n", (array.values[1] as StringToken).value)
        assertEquals("\\t", (array.values[2] as StringToken).value)
    }

    @Test
    fun `parse large array`() {
        val arr = (1..1000).toList()
        val json = arr.joinToString(prefix = "[", postfix = "]", separator = ",")
        val result = parse(json)
        assertNotNull(result.token)
        val array = result.token as ArrayToken
        assertEquals(1000, array.values.size)
        assertEquals(1.0, (array.values[0] as NumberToken).value)
        assertEquals(1000.0, (array.values[999] as NumberToken).value)
    }

    @Test
    fun `parse array with trailing comma fails`() {
        assertThrows(SyntaxError::class.java) {
            parse("[1,2,3,]")
        }
    }

    @Test
    fun `parse array with leading comma fails`() {
        assertThrows(SyntaxError::class.java) {
            parse("[,1,2,3]")
        }
    }

    @Test
    fun `parse array with missing value fails`() {
        assertThrows(SyntaxError::class.java) {
            parse("[1,,2]")
        }
    }

    @Test
    fun `parse array with invalid syntax fails`() {
        assertThrows(SyntaxError::class.java) {
            parse("[1 2 3]")
        }
    }

    @Test
    fun `access out of bounds throws`() {
        val result = parse("[1]")
        assertNotNull(result.token)
        val array = result.token as ArrayToken
        assertThrows(IndexOutOfBoundsException::class.java) {
            array.values[5]
        }
    }

    @Test
    fun `array equality`() {
        val result1 = parse("[1, 2, 3]")
        val result2 = parse("[1, 2, 3]")
        assertNotNull(result1.token)
        assertNotNull(result2.token)
        val array1 = result1.token as ArrayToken
        val array2 = result2.token as ArrayToken
        assertEquals(array1.values, array2.values)
    }
}
