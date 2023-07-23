package com.kamelia.hedera.util

import com.kamelia.hedera.rest.file.toSizeDTO
import java.util.stream.Stream
import kotlin.test.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class DtoTest {

    @DisplayName("SizeDTO test")
    @ParameterizedTest(name = "SizeDTO test ({0} bytes approx {1} << {2})")
    @MethodSource
    fun sizeDtoTest(
        size: Long,
        value: Double,
        shift: Int,
    ) {
        val sizeDTO = size.toSizeDTO()
        assertEquals(value, sizeDTO.value)
        assertEquals(shift, sizeDTO.shift)
    }

    companion object {

        @JvmStatic
        fun sizeDtoTest(): Stream<Arguments> = Stream.of(
            Arguments.of(20L, 20.0, 0),
            Arguments.of(1000L, 1000.0, 0),
            Arguments.of(1024L, 1.0, 10),
            Arguments.of(2048L, 2.0, 10),
            Arguments.of(1024L * 1024L, 1.0, 20),
            Arguments.of(2048L * 1024L, 2.0, 20),
        )
    }

}