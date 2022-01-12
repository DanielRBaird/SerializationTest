package com.dbaird

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WireEnumTests {

    @Test
    fun `test deserialize class with enum`() {
        val json =
            """{"sampleEnum": "one"}"""

        val testData = deserializeObject<TestData>(json)
        assertTrue { testData.sampleEnum is WireEnum.Value }
        assertEquals(TestEnum.ONE, testData.sampleEnum.enumValue(TestEnum.UNKNOWN))
    }

    @Test
    fun `test serialize class with enum`() {
        val expectedJson =
            """{"sampleEnum":"one"}"""

        val actualJson = serializeObject(TestData(sampleEnum = WireEnum.Value(TestEnum.ONE)))
        assertEquals(expectedJson, actualJson)
    }
}

@Serializable
internal data class TestData(val sampleEnum: WireEnum<TestEnum>)

@Serializable
internal enum class TestEnum {
    @SerialName("one")
    ONE,
    @SerialName("two")
    TWO,
    UNKNOWN
}
