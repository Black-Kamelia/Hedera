package com.kamelia.hedera.rest.user

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

@DisplayName("Users pagination tests")
class UsersPaginationTests {

    @DisplayName("List users with given page index")
    @Test
    fun testListUsersWithPageIndex() = testApplication {
        val (tokens, _) = user

        val response = client().post("/api/users/search?page=3") {
            contentType(ContentType.Application.Json)
            setBody(PageDefinitionDTO())
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.OK, response.status)

        val users = Json.decodeFromString<UserPageDTO>(response.bodyAsText())
        assertEquals(3, users.page.page)
    }

    @DisplayName("List users with given page size")
    @Test
    fun testListUsersWithPageSize() = testApplication {
        val (tokens, _) = user

        val response = client().post("/api/users/search?pageSize=2") {
            contentType(ContentType.Application.Json)
            setBody(PageDefinitionDTO())
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.OK, response.status)

        val users = Json.decodeFromString<UserPageDTO>(response.bodyAsText())
        assertEquals(2, users.page.pageSize)
        assertEquals(2, users.page.items.size)
    }

    @DisplayName("Filter users")
    @ParameterizedTest(name = "Filter users ({0} {1} ''{2}'')")
    @MethodSource
    fun filterUsers(
        field: String,
        operator: String,
        value: String,
        check: (UserRepresentationDTO) -> Boolean,
    ) = testApplication {
        val (tokens, _) = user

        val response = client().post("/api/users/search?pageSize=1000") {
            contentType(ContentType.Application.Json)
            setBody(PageDefinitionDTO(filters = listOf(listOf(FilterObject(field, operator, value)))))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.OK, response.status)

        val users = Json.decodeFromString<UserPageDTO>(response.bodyAsText())
        assertTrue(users.page.items.all { check(it) })
    }

    @DisplayName("Sort users")
    @ParameterizedTest(name = "Sort users by {0} ({1})")
    @MethodSource
    fun <R : Comparable<R>> sortUsers(
        field: String,
        direction: SortDirection,
        selector: (UserRepresentationDTO) -> R?,
    ) = testApplication {
        val (tokens, _) = user

        val response = client().post("/api/users/search?pageSize=1000") {
            contentType(ContentType.Application.Json)
            setBody(PageDefinitionDTO(sorter = listOf(SortObject(field, direction))))
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.OK, response.status)

        val users = Json.decodeFromString<UserPageDTO>(response.bodyAsText())
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

    @DisplayName("Filter users on unknown field")
    @Test
    fun filterUsersOnUnknownField() = testApplication {
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

        val response = client().post("/api/users/search") {
            contentType(ContentType.Application.Json)
            setBody(body)
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @DisplayName("Filter users with malformed filter")
    @Test
    fun filterUsersWithMalformedFilter() = testApplication {
        val (tokens, _) = user
        val body = """{"filters":[[{"malformed"}]]}"""

        val response = client().post("/api/users/search") {
            contentType(ContentType.Application.Json)
            setBody(body)
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @DisplayName("Filter users with unknown operator")
    @Test
    fun filterUsersWithUnknownOperator() = testApplication {
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

        val response = client().post("/api/users/search") {
            contentType(ContentType.Application.Json)
            setBody(body)
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @DisplayName("Sort users on unknown field")
    @Test
    fun sortUsersOnUnknownField() = testApplication {
        val (tokens, _) = user

        val response = client().post("/api/users/search") {
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

    @DisplayName("Sort users with malformed filter")
    @Test
    fun sortUsersWithMalformedFilter() = testApplication {
        val (tokens, _) = user
        val body = """{"sorter":[{"malformed"}]}"""

        val response = client().post("/api/users/search") {
            contentType(ContentType.Application.Json)
            setBody(body)
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @DisplayName("Sort users with unknown direction")
    @Test
    fun sortUsersWithUnknownDirection() = testApplication {
        val (tokens, _) = user
        val body = Json.encodeToString(
            PageDefinitionDTO(
                sorter = listOf(
                    SortObject(field = "name", direction = SortDirection.DESC)
                )
            )
        ).replace("DESC", "WRONG")

        val response = client().post("/api/users/search") {
            contentType(ContentType.Application.Json)
            setBody(body)
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)

        println(response.bodyAsText())
    }

    @DisplayName("Paginate users with negative page")
    @Test
    fun paginateUsersWithNegativePage() = testApplication {
        val (tokens, _) = user

        val response = client().post("/api/users/search?page=-1") {
            contentType(ContentType.Application.Json)
            setBody(PageDefinitionDTO())
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @DisplayName("Paginate users with negative page size")
    @Test
    fun paginateUsersWithNegativePageSize() = testApplication {
        val (tokens, _) = user

        val response = client().post("/api/users/search?pageSize=-1") {
            contentType(ContentType.Application.Json)
            setBody(PageDefinitionDTO())
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @DisplayName("Paginate users with invalid page")
    @Test
    fun paginateUsersWithInvalidPage() = testApplication {
        val (tokens, _) = user

        val response = client().post("/api/users/search?page=wrong") {
            contentType(ContentType.Application.Json)
            setBody(PageDefinitionDTO())
            tokens?.let { bearerAuth(it.accessToken) }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @DisplayName("Paginate users with invalid page size")
    @Test
    fun paginateUsersWithInvalidPageSize() = testApplication {
        val (tokens, _) = user

        val response = client().post("/api/users/search?pageSize=wrong") {
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
                    login("users.owner", "password").second ?: throw Exception("Login failed"),
                    "ffffffff-0003-0000-0001-000000000000".uuid()
                )
            }
        }

        @JvmStatic
        fun filterUsers(): Stream<Arguments> = Stream.of(
            Arguments.of(
                "username",
                Named.of("==", "eq"),
                "users.owner",
                { user: UserRepresentationDTO -> user.username == "users.owner" }
            ),
            Arguments.of(
                "username",
                Named.of("!=", "ne"),
                "users.owner",
                { user: UserRepresentationDTO -> user.username != "users.owner" }
            ),
            Arguments.of(
                "username",
                Named.of("fuzzy-like", "fuzzy"),
                "owner",
                { user: UserRepresentationDTO -> user.username.contains("owner") }
            ),
            Arguments.of(
                "email",
                Named.of("==", "eq"),
                "users.owner",
                { user: UserRepresentationDTO -> user.email == "users.owner@test.com" }
            ),
            Arguments.of(
                "email",
                Named.of("!=", "ne"),
                "users.owner@test.com",
                { user: UserRepresentationDTO -> user.email != "users.owner@test.com" }
            ),
            Arguments.of(
                "email",
                Named.of("fuzzy-like", "fuzzy"),
                "owner",
                { user: UserRepresentationDTO -> user.email.contains("owner") }
            ),
            Arguments.of(
                "role",
                Named.of("==", "eq"),
                "ADMIN",
                { user: UserRepresentationDTO -> user.role == UserRole.ADMIN }
            ),
            Arguments.of(
                "role",
                Named.of("!=", "ne"),
                "ADMIN",
                { user: UserRepresentationDTO -> user.role != UserRole.ADMIN }
            ),
            Arguments.of(
                "role",
                Named.of(">", "gt"),
                "ADMIN",
                { user: UserRepresentationDTO ->
                    user.role.power > UserRole.ADMIN.power
                    throw TestAbortedException()
                }
            ),
            Arguments.of(
                "role",
                Named.of(">=", "ge"),
                "ADMIN",
                { user: UserRepresentationDTO ->
                    user.role.power >= UserRole.ADMIN.power
                    throw TestAbortedException()
                }
            ),
            Arguments.of(
                "role",
                Named.of("<", "lt"),
                "ADMIN",
                { user: UserRepresentationDTO ->
                    user.role.power < UserRole.ADMIN.power
                    throw TestAbortedException()
                }
            ),
            Arguments.of(
                "role",
                Named.of("<=", "le"),
                "ADMIN",
                { user: UserRepresentationDTO ->
                    user.role.power <= UserRole.ADMIN.power
                    throw TestAbortedException()
                }
            ),
            Arguments.of(
                "enabled",
                Named.of("==", "eq"),
                "true",
                { user: UserRepresentationDTO -> user.enabled }
            ),
            Arguments.of(
                "enabled",
                Named.of("!=", "ne"),
                "true",
                { user: UserRepresentationDTO -> !user.enabled }
            ),
        )

        @JvmStatic
        fun sortUsers(): Stream<Arguments> = Stream.of(
            Arguments.of(
                "username",
                SortDirection.ASC,
                { user: UserRepresentationDTO -> user.username }
            ),
            Arguments.of(
                "username",
                SortDirection.DESC,
                { user: UserRepresentationDTO -> user.username }
            ),
            Arguments.of(
                "email",
                SortDirection.ASC,
                { user: UserRepresentationDTO -> user.email }
            ),
            Arguments.of(
                "email",
                SortDirection.DESC,
                { user: UserRepresentationDTO -> user.email }
            ),
            Arguments.of(
                "role",
                SortDirection.ASC,
                { user: UserRepresentationDTO ->
                    user.role
                    throw TestAbortedException()
                }
            ),
            Arguments.of(
                "role",
                SortDirection.DESC,
                { user: UserRepresentationDTO ->
                    user.role
                    throw TestAbortedException()
                }
            ),
            Arguments.of(
                "enabled",
                SortDirection.ASC,
                { user: UserRepresentationDTO -> user.enabled }
            ),
            Arguments.of(
                "enabled",
                SortDirection.DESC,
                { user: UserRepresentationDTO -> user.enabled }
            ),
            Arguments.of(
                "createdAt",
                SortDirection.ASC,
                { user: UserRepresentationDTO -> user.createdAt }
            ),
            Arguments.of(
                "createdAt",
                SortDirection.DESC,
                { user: UserRepresentationDTO -> user.createdAt }
            ),
            Arguments.of(
                "currentDiskQuota",
                SortDirection.ASC,
                { user: UserRepresentationDTO -> user.currentDiskQuota }
            ),
            Arguments.of(
                "currentDiskQuota",
                SortDirection.DESC,
                { user: UserRepresentationDTO -> user.currentDiskQuota }
            ),
        )
    }

}