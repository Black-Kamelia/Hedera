package com.kamelia.hedera.rest.configuration

import io.ktor.http.*


class GlobalConfigurationExpectedResults(
    val getConfiguration: HttpStatusCode,
    val getPublicConfiguration: HttpStatusCode,
    val updateConfiguration: HttpStatusCode,
)

val ownerExpectedResults = GlobalConfigurationExpectedResults(
    getConfiguration = HttpStatusCode.OK,
    getPublicConfiguration = HttpStatusCode.OK,
    updateConfiguration = HttpStatusCode.OK,
)
val adminExpectedResults = GlobalConfigurationExpectedResults(
    getConfiguration = HttpStatusCode.OK,
    getPublicConfiguration = HttpStatusCode.OK,
    updateConfiguration = HttpStatusCode.OK,
)
val regularUserExpectedResults = GlobalConfigurationExpectedResults(
    getConfiguration = HttpStatusCode.Forbidden,
    getPublicConfiguration = HttpStatusCode.OK,
    updateConfiguration = HttpStatusCode.Forbidden,
)
val guestExpectedResults = GlobalConfigurationExpectedResults(
    getConfiguration = HttpStatusCode.Unauthorized,
    getPublicConfiguration = HttpStatusCode.OK,
    updateConfiguration = HttpStatusCode.Unauthorized,
)

suspend fun isolateGlobalConfiguration(
    block: suspend () -> Unit,
) {
    val originalConfiguration = GlobalConfigurationService.currentConfiguration
    block()
    val field = GlobalConfigurationService::class.java.getDeclaredField("_currentConfiguration")
    field.isAccessible = true
    field.set(GlobalConfigurationService, originalConfiguration)
}
