package com.kamelia.hedera.rest.configuration

import com.kamelia.hedera.core.Errors
import com.kamelia.hedera.core.HederaException
import com.kamelia.hedera.core.Response
import com.kamelia.hedera.rest.user.ConfigurationEvents
import java.io.File
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
class GlobalConfiguration(
    var enableRegistrations: Boolean = false,
    var defaultDiskQuotaPolicy: DiskQuotaPolicy = DiskQuotaPolicy.LIMITED,
    var defaultDiskQuota: Long? = 524288000, // 500 MiB
) {
    fun toDTO() = GlobalConfigurationRepresentationDTO(
        enableRegistrations,
        defaultDiskQuotaPolicy,
        defaultDiskQuota
    )
}

const val FILENAME = "global_configuration.json"

object GlobalConfigurationService {

    private val currentConfiguration: GlobalConfiguration by lazy {
        with(File(FILENAME)) {
            if (!exists()) generateDefaultConfiguration(this)
            try {
                Json.decodeFromString<GlobalConfiguration>(this.readText())
            } catch (e: SerializationException) {
                throw HederaException(Errors.Configuration.PARSE_ERROR)
            }
        }
    }

    private fun generateDefaultConfiguration(file: File) {
        if (!file.createNewFile()) throw HederaException(Errors.Configuration.GENERATION_ERROR)
        file.writeText(Json.encodeToString(GlobalConfiguration()))
    }

    private fun writeConfiguration() {
        with(File(FILENAME)) {
            if (!exists() && !createNewFile()) throw HederaException(Errors.Configuration.WRITE_ERROR)
            writeText("")
            appendText(Json.encodeToString(currentConfiguration))
        }
    }

    fun getConfiguration() = Response.ok(currentConfiguration.toDTO())

    /* May change in the future if we need to hide some settings */
    fun getConfigurationPublic() = Response.ok(currentConfiguration.toDTO())

    suspend fun updateConfiguration(
        dto: GlobalConfigurationUpdateDTO
    ): Response<GlobalConfigurationRepresentationDTO>  {
        dto.enableRegistrations?.let { currentConfiguration.enableRegistrations = it }
        dto.defaultDiskQuotaPolicy?.let{
            if (currentConfiguration.defaultDiskQuota != null)
                currentConfiguration.defaultDiskQuotaPolicy = it
            if (it == DiskQuotaPolicy.UNLIMITED)
                currentConfiguration.defaultDiskQuota = null
        }
        dto.defaultDiskQuota?.let{
            if (currentConfiguration.defaultDiskQuotaPolicy == DiskQuotaPolicy.LIMITED)
                currentConfiguration.defaultDiskQuota = it
        }
        writeConfiguration()
        ConfigurationEvents.configurationUpdatedEvent(currentConfiguration.toDTO())

        return Response.ok(currentConfiguration.toDTO())
    }

}