package com.kamelia.hedera.rest.configuration

import io.ktor.http.*


class GlobalConfigurationExpectedResults(
    val getConfiguration: HttpStatusCode,
    val getPublicConfiguration: HttpStatusCode,
    val updateConfiguration: HttpStatusCode,
    val getThumbnailCacheSize: HttpStatusCode,
    val clearThumbnailCacheSize: HttpStatusCode,
)

val ownerExpectedResults = GlobalConfigurationExpectedResults(
    getConfiguration = HttpStatusCode.OK,
    getPublicConfiguration = HttpStatusCode.OK,
    updateConfiguration = HttpStatusCode.OK,
    getThumbnailCacheSize = HttpStatusCode.OK,
    clearThumbnailCacheSize = HttpStatusCode.OK,
)
val adminExpectedResults = GlobalConfigurationExpectedResults(
    getConfiguration = HttpStatusCode.OK,
    getPublicConfiguration = HttpStatusCode.OK,
    updateConfiguration = HttpStatusCode.OK,
    getThumbnailCacheSize = HttpStatusCode.OK,
    clearThumbnailCacheSize = HttpStatusCode.OK,
)
val regularUserExpectedResults = GlobalConfigurationExpectedResults(
    getConfiguration = HttpStatusCode.Forbidden,
    getPublicConfiguration = HttpStatusCode.OK,
    updateConfiguration = HttpStatusCode.Forbidden,
    getThumbnailCacheSize = HttpStatusCode.Forbidden,
    clearThumbnailCacheSize = HttpStatusCode.Forbidden,
)
val guestExpectedResults = GlobalConfigurationExpectedResults(
    getConfiguration = HttpStatusCode.Unauthorized,
    getPublicConfiguration = HttpStatusCode.OK,
    updateConfiguration = HttpStatusCode.Unauthorized,
    getThumbnailCacheSize = HttpStatusCode.Unauthorized,
    clearThumbnailCacheSize = HttpStatusCode.Unauthorized,
)

suspend fun isolateGlobalConfiguration(
    block: suspend () -> Unit,
) {
    val field = GlobalConfigurationService::class.java.getDeclaredField("_currentConfiguration")
    field.isAccessible = true
    val originalConfiguration = field.get(GlobalConfigurationService) as GlobalConfiguration
    block()
    field.set(GlobalConfigurationService, originalConfiguration)
    field.isAccessible = false
}
