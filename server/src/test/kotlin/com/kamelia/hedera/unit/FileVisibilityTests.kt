package com.kamelia.hedera.unit

import com.kamelia.hedera.rest.file.FileVisibility
import java.util.stream.Stream
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class FileVisibilityTests {

    @DisplayName("Find file visibility by name")
    @Test
    fun findFileVisibilityByName() {
        val visibility = FileVisibility.valueOfOrNull("PUBLIC")
        assertEquals(FileVisibility.PUBLIC, visibility)
    }

    @DisplayName("Find unknown file visibility by name returns null")
    @Test
    fun findUnknownFileVisibilityByNameReturnsNull() {
        val visibility = FileVisibility.valueOfOrNull("UNKNOWN_VISIBILITY")
        assertEquals(null, visibility)
    }

    @DisplayName("Get file visibility translation key")
    @ParameterizedTest
    @MethodSource
    fun getFileVisibilityTranslationKey(
        visibility: FileVisibility,
        expectedTranslationKey: String,
    ) = assertEquals(expectedTranslationKey, visibility.toMessageKey())

    companion object {

        @JvmStatic
        fun getFileVisibilityTranslationKey(): Stream<Arguments> = Stream.of(
            Arguments.of(FileVisibility.PUBLIC, "pages.files.visibility.public"),
            Arguments.of(FileVisibility.PRIVATE, "pages.files.visibility.private"),
            Arguments.of(FileVisibility.UNLISTED, "pages.files.visibility.unlisted"),
        )

    }

}