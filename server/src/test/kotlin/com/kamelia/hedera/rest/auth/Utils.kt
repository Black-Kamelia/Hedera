package com.kamelia.hedera.rest.auth

import com.kamelia.hedera.rest.user.UserCreationDTO
import io.ktor.http.*

open class AuthenticationExpectedResults(
    val refreshSession: HttpStatusCode,
    val logoutSession: HttpStatusCode,
    val logoutAllSessions: HttpStatusCode,
)

open class AuthenticationTestsInput(
    val login: Pair<String, String>,
)

class UserAuthenticationTestsInput(
    val resetPassword: String,
    login: Pair<String, String>,
) : AuthenticationTestsInput(login)

class GuestAuthenticationTestsInput(
    val signUp: UserCreationDTO,
) : AuthenticationTestsInput(Pair("", ""))

class UserAuthenticationExpectedResults(
    refreshSession: HttpStatusCode,
    logoutSession: HttpStatusCode,
    logoutAllSessions: HttpStatusCode,
    val login: HttpStatusCode,
    val resetPassword: HttpStatusCode,
) : AuthenticationExpectedResults(
    refreshSession,
    logoutSession,
    logoutAllSessions,
)

class GuestAuthenticationExpectedResults(
    refreshSession: HttpStatusCode,
    logoutSession: HttpStatusCode,
    logoutAllSessions: HttpStatusCode,
    val signUp: HttpStatusCode,
) : AuthenticationExpectedResults(
    refreshSession,
    logoutSession,
    logoutAllSessions,
)

val ownerExpectedResults = UserAuthenticationExpectedResults(
    refreshSession = HttpStatusCode.Created,
    logoutSession = HttpStatusCode.NoContent,
    logoutAllSessions = HttpStatusCode.NoContent,
    login = HttpStatusCode.Created,
    resetPassword = HttpStatusCode.OK,
)
val adminExpectedResults = UserAuthenticationExpectedResults(
    refreshSession = HttpStatusCode.Created,
    logoutSession = HttpStatusCode.NoContent,
    logoutAllSessions = HttpStatusCode.NoContent,
    login = HttpStatusCode.Created,
    resetPassword = HttpStatusCode.OK,
)
val regularUserExpectedResults = UserAuthenticationExpectedResults(
    refreshSession = HttpStatusCode.Created,
    logoutSession = HttpStatusCode.NoContent,
    logoutAllSessions = HttpStatusCode.NoContent,
    login = HttpStatusCode.Created,
    resetPassword = HttpStatusCode.OK,
)
val guestExpectedResults = GuestAuthenticationExpectedResults(
    refreshSession = HttpStatusCode.Unauthorized,
    logoutSession = HttpStatusCode.Unauthorized,
    logoutAllSessions = HttpStatusCode.Unauthorized,
    signUp = HttpStatusCode.Created,
)

val ownerInput = UserAuthenticationTestsInput(
    login = Pair("authentication.owner.login", "password"),
    resetPassword = "authentication-owner-reset@test.com",
)
val adminInput = UserAuthenticationTestsInput(
    login = Pair("authentication.admin.login", "password"),
    resetPassword = "authentication-admin-reset@test.com",
)
val regularUserInput = UserAuthenticationTestsInput(
    login = Pair("authentication.user.login", "password"),
    resetPassword = "authentication-user-reset@test.com",
)
val guestInput = GuestAuthenticationTestsInput(
    signUp = UserCreationDTO(
        username = "guest.register",
        email = "guest.register@test.com",
        password = "password",
    ),
)
