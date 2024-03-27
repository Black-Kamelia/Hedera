package com.kamelia.hedera.rest.file

import com.kamelia.hedera.TestUser
import com.kamelia.hedera.client
import com.kamelia.hedera.login
import com.kamelia.hedera.rest.core.pageable.FilterObject
import com.kamelia.hedera.rest.core.pageable.PageDefinitionDTO
import com.kamelia.hedera.rest.core.pageable.SortDirection
import com.kamelia.hedera.rest.core.pageable.SortObject
import com.kamelia.hedera.uuid
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import java.time.Instant
import java.util.stream.Stream
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Named
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.opentest4j.TestAbortedException

@DisplayName("Files pagination tests")
class FilesPaginationTests {

    @DisplayName("List files with given page index")
    @Test
    fun testListFilesWithPageIndex() = testApplication {
        val (tokens, _) = user

        val response = client().post("/api/files/search?page=3") {
            contentType(ContentType.Application.Json)
            setBody(PageDefinitionDTO())
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.OK, response.status)

        val users = Json.decodeFromString<FilePageDTO>(response.bodyAsText())
        assertEquals(3, users.page.page)
    }

    @DisplayName("List files with given page size")
    @Test
    fun testListFilesWithPageSize() = testApplication {
        val (tokens, _) = user

        val response = client().post("/api/files/search?pageSize=2") {
            contentType(ContentType.Application.Json)
            setBody(PageDefinitionDTO())
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.OK, response.status)

        val users = Json.decodeFromString<FilePageDTO>(response.bodyAsText())
        assertEquals(2, users.page.pageSize)
        assertEquals(2, users.page.items.size)
    }

    @DisplayName("Filter files")
    @ParameterizedTest(name = "Filter files ({0} {1} ''{2}'')")
    @MethodSource
    fun filterFiles(
        field: String,
        operator: String,
        value: String,
        check: (FileRepresentationDTO) -> Boolean,
    ) = testApplication {
        val (tokens, _) = user

        val response = client().post("/api/files/search?pageSize=1000") {
            contentType(ContentType.Application.Json)
            setBody(PageDefinitionDTO(filters = listOf(listOf(FilterObject(field, operator, value)))))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.OK, response.status)

        val users = Json.decodeFromString<FilePageDTO>(response.bodyAsText())
        assertTrue(users.page.items.all { check(it) })
    }

    @DisplayName("Sort files")
    @ParameterizedTest(name = "Sort files by {0} ({1})")
    @MethodSource
    fun <R : Comparable<R>> sortFiles(
        field: String,
        direction: SortDirection,
        selector: (FileRepresentationDTO) -> R?,
    ) = testApplication {
        val (tokens, _) = user

        val response = client().post("/api/files/search?pageSize=1000") {
            contentType(ContentType.Application.Json)
            setBody(PageDefinitionDTO(sorter = listOf(SortObject(field, direction))))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.OK, response.status)

        val users = Json.decodeFromString<FilePageDTO>(response.bodyAsText())
        if (direction == SortDirection.ASC) {
            assertEquals(
                users.page.items.shuffled().stream().map(selector).sorted().toList(),
                users.page.items.map(selector)
            )
        } else {
            assertEquals(
                users.page.items.shuffled().stream().map(selector).sorted().toList().reversed(),
                users.page.items.map(selector)
            )
        }
    }

    @DisplayName("Filter files on unknown field")
    @Test
    fun filterFilesOnUnknownField() = testApplication {
        val (tokens, _) = user
        val body = Json.encodeToString(
            PageDefinitionDTO(
                filters = listOf(
                    listOf(
                        FilterObject(field = "unknown", operator = "eq", value = "test")
                    )
                )
            )
        )

        val response = client().post("/api/files/search") {
            contentType(ContentType.Application.Json)
            setBody(body)
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @DisplayName("Filter files with malformed filter")
    @Test
    fun filterFilesWithMalformedFilter() = testApplication {
        val (tokens, _) = user
        val body = """{"filters":[[{"malformed"}]]}"""

        val response = client().post("/api/files/search") {
            contentType(ContentType.Application.Json)
            setBody(body)
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @DisplayName("Filter files with unknown operator")
    @Test
    fun filterFilesWithUnknownOperator() = testApplication {
        val (tokens, _) = user
        val body = Json.encodeToString(
            PageDefinitionDTO(
                filters = listOf(
                    listOf(
                        FilterObject(field = "name", operator = "unknown", value = "test")
                    )
                )
            )
        )

        val response = client().post("/api/files/search") {
            contentType(ContentType.Application.Json)
            setBody(body)
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @DisplayName("Sort files on unknown field")
    @Test
    fun sortFilesOnUnknownField() = testApplication {
        val (tokens, _) = user

        val response = client().post("/api/files/search") {
            contentType(ContentType.Application.Json)
            setBody(
                PageDefinitionDTO(
                    sorter = listOf(SortObject(field = "unknown"))
                )
            )
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @DisplayName("Sort files with malformed filter")
    @Test
    fun sortFilesWithMalformedFilter() = testApplication {
        val (tokens, _) = user
        val body = """{"sorter":[{"malformed"}]}"""

        val response = client().post("/api/files/search") {
            contentType(ContentType.Application.Json)
            setBody(body)
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @DisplayName("Sort files with unknown direction")
    @Test
    fun sortFilesWithUnknownDirection() = testApplication {
        val (tokens, _) = user
        val body = Json.encodeToString(
            PageDefinitionDTO(
                sorter = listOf(
                    SortObject(field = "name", direction = SortDirection.DESC)
                )
            )
        ).replace("DESC", "WRONG")

        val response = client().post("/api/files/search") {
            contentType(ContentType.Application.Json)
            setBody(body)
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)

        println(response.bodyAsText())
    }

    @DisplayName("Paginate files with negative page")
    @Test
    fun paginateFilesWithNegativePage() = testApplication {
        val (tokens, _) = user

        val response = client().post("/api/files/search?page=-1") {
            contentType(ContentType.Application.Json)
            setBody(PageDefinitionDTO())
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @DisplayName("Paginate files with negative page size")
    @Test
    fun paginateFilesWithNegativePageSize() = testApplication {
        val (tokens, _) = user

        val response = client().post("/api/files/search?pageSize=-1") {
            contentType(ContentType.Application.Json)
            setBody(PageDefinitionDTO())
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @DisplayName("Paginate files with invalid page")
    @Test
    fun paginateFilesWithInvalidPage() = testApplication {
        val (tokens, _) = user

        val response = client().post("/api/files/search?page=wrong") {
            contentType(ContentType.Application.Json)
            setBody(PageDefinitionDTO())
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @DisplayName("Paginate files with invalid page size")
    @Test
    fun paginateFilesWithInvalidPageSize() = testApplication {
        val (tokens, _) = user

        val response = client().post("/api/files/search?pageSize=wrong") {
            contentType(ContentType.Application.Json)
            setBody(PageDefinitionDTO())
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    companion object {

        private lateinit var user: TestUser

        init {
            testApplication {
                user = Pair(
                    login("files.owner", "password").second ?: throw Exception("Login failed"),
                    "ffffffff-0001-0000-0001-000000000000".uuid()
                )
            }
        }

        @JvmStatic
        fun filterFiles(): Stream<Arguments> = Stream.of(
            Arguments.of(
                "name",
                Named.of("==", "eq"),
                "owner_view_public_own.txt",
                { file: FileRepresentationDTO -> file.name == "owner_view_public_own.txt" }
            ),
            Arguments.of(
                "name",
                Named.of("!=", "ne"),
                "owner_view_public_own.txt",
                { file: FileRepresentationDTO -> file.name != "owner_view_public_own.txt" }
            ),
            Arguments.of(
                "name",
                Named.of("fuzzy-like", "fuzzy"),
                "owner",
                { file: FileRepresentationDTO -> file.name.contains("owner") }
            ),
            Arguments.of(
                "mimeType",
                Named.of("==", "eq"),
                "text/plain",
                { file: FileRepresentationDTO -> file.mimeType == "text/plain" }
            ),
            Arguments.of(
                "mimeType",
                Named.of("!=", "ne"),
                "text/plain",
                { file: FileRepresentationDTO -> file.mimeType != "text/plain" }
            ),
            Arguments.of(
                "mimeType",
                Named.of("fuzzy-like", "fuzzy"),
                "image",
                { file: FileRepresentationDTO -> file.mimeType.contains("image") }
            ),
            Arguments.of(
                "size",
                Named.of("==", "eq"),
                "50",
                { file: FileRepresentationDTO -> file.size == 50L }
            ),
            Arguments.of(
                "size",
                Named.of("!=", "ne"),
                "50",
                { file: FileRepresentationDTO -> file.size != 50L }
            ),
            Arguments.of(
                "size",
                Named.of(">", "gt"),
                "50",
                { file: FileRepresentationDTO -> file.size > 50L }
            ),
            Arguments.of(
                "size",
                Named.of(">=", "ge"),
                "50",
                { file: FileRepresentationDTO -> file.size >= 50L }
            ),
            Arguments.of(
                "size",
                Named.of("<", "lt"),
                "50",
                { file: FileRepresentationDTO -> file.size < 50L }
            ),
            Arguments.of(
                "size",
                Named.of("<=", "le"),
                "50",
                { file: FileRepresentationDTO -> file.size <= 50L }
            ),
            Arguments.of(
                "visibility",
                Named.of("==", "eq"),
                "UNLISTED",
                { file: FileRepresentationDTO -> file.visibility == FileVisibility.UNLISTED }
            ),
            Arguments.of(
                "visibility",
                Named.of("!=", "ne"),
                "UNLISTED",
                { file: FileRepresentationDTO -> file.visibility != FileVisibility.UNLISTED }
            ),
            Arguments.of(
                "createdAt",
                Named.of("==", "eq"),
                "2019-01-01T00:00:00.00Z",
                { file: FileRepresentationDTO ->
                    Instant.parse(file.createdAt).toEpochMilli() == Instant.parse("2019-01-01T00:00:00.00Z")
                        .toEpochMilli()
                }
            ),
            Arguments.of(
                "createdAt",
                Named.of("!=", "ne"),
                "2019-01-01T00:00:00.00Z",
                { file: FileRepresentationDTO ->
                    Instant.parse(file.createdAt).toEpochMilli() != Instant.parse("2019-01-01T00:00:00.00Z")
                        .toEpochMilli()
                }
            ),
            Arguments.of(
                "createdAt",
                Named.of(">", "gt"),
                "2019-01-01T00:00:00.00Z",
                { file: FileRepresentationDTO ->
                    Instant.parse(file.createdAt).toEpochMilli() > Instant.parse("2019-01-01T00:00:00.00Z")
                        .toEpochMilli()
                }
            ),
            Arguments.of(
                "createdAt",
                Named.of(">=", "ge"),
                "2019-01-01T00:00:00.00Z",
                { file: FileRepresentationDTO ->
                    Instant.parse(file.createdAt).toEpochMilli() >= Instant.parse("2019-01-01T00:00:00.00Z")
                        .toEpochMilli()
                }
            ),
            Arguments.of(
                "createdAt",
                Named.of("<", "lt"),
                "2019-01-01T00:00:00.00Z",
                { file: FileRepresentationDTO ->
                    Instant.parse(file.createdAt).toEpochMilli() < Instant.parse("2019-01-01T00:00:00.00Z")
                        .toEpochMilli()
                }
            ),
            Arguments.of(
                "createdAt",
                Named.of("<=", "le"),
                "2019-01-01T00:00:00.00Z",
                { file: FileRepresentationDTO ->
                    Instant.parse(file.createdAt).toEpochMilli() <= Instant.parse("2019-01-01T00:00:00.00Z")
                        .toEpochMilli()
                }
            ),
            Arguments.of(
                "token",
                Named.of("==", "eq"),
                "00000003-0001-0000-0001-000000000000",
                { file: FileRepresentationDTO -> file.uploadTokenId == "00000003-0001-0000-0001-000000000000" }
            ),
            Arguments.of(
                "token",
                Named.of("!=", "ne"),
                "00000003-0001-0000-0001-000000000000",
                { file: FileRepresentationDTO -> file.uploadTokenId != "00000003-0001-0000-0001-000000000000" }
            ),
        )

        @JvmStatic
        fun sortFiles(): Stream<Arguments> = Stream.of(
            Arguments.of(
                "name",
                SortDirection.ASC,
                { file: FileRepresentationDTO -> file.name }
            ),
            Arguments.of(
                "name",
                SortDirection.DESC,
                { file: FileRepresentationDTO -> file.name }
            ),
            Arguments.of(
                "mimeType",
                SortDirection.ASC,
                { file: FileRepresentationDTO -> file.mimeType }
            ),
            Arguments.of(
                "mimeType",
                SortDirection.DESC,
                { file: FileRepresentationDTO -> file.mimeType }
            ),
            Arguments.of(
                "size",
                SortDirection.ASC,
                { file: FileRepresentationDTO -> file.size }
            ),
            Arguments.of(
                "size",
                SortDirection.DESC,
                { file: FileRepresentationDTO -> file.size }
            ),
            Arguments.of(
                "visibility",
                SortDirection.ASC,
                { file: FileRepresentationDTO ->
                    file.visibility
                    throw TestAbortedException()
                }
            ),
            Arguments.of(
                "visibility",
                SortDirection.DESC,
                { file: FileRepresentationDTO ->
                    file.visibility
                    throw TestAbortedException()
                }
            ),
            Arguments.of(
                "createdAt",
                SortDirection.ASC,
                { file: FileRepresentationDTO -> file.createdAt }
            ),
            Arguments.of(
                "createdAt",
                SortDirection.DESC,
                { file: FileRepresentationDTO -> file.createdAt }
            ),
        )
    }

}