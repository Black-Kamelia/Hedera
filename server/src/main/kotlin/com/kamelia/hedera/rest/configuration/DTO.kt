package com.kamelia.hedera.rest.configuration

import kotlinx.serialization.Serializable

@Serializable
data class GlobalConfigurationRepresentationDTO(
    val enableRegistrations: Boolean,
    val defaultDiskQuotaPolicy: DiskQuotaPolicy,
    val defaultDiskQuota: Long?,
    val maximumThumbnailCount: Int,
)

@Serializable
data class PublicGlobalConfigurationRepresentationDTO(
    val enableRegistrations: Boolean,
    val defaultDiskQuotaPolicy: DiskQuotaPolicy,
    val defaultDiskQuota: Long?,
)

@Serializable
data class GlobalConfigurationUpdateDTO(
    val enableRegistrations: Boolean? = null,
    val defaultDiskQuotaPolicy: DiskQuotaPolicy? = null,
    val defaultDiskQuota: Long? = null,
    val maximumThumbnailCount: Int? = null,
)
