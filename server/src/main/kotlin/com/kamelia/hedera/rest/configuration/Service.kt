package com.kamelia.hedera.rest.configuration

import com.kamelia.hedera.core.Errors
import com.kamelia.hedera.core.HederaException
import com.kamelia.hedera.core.response.Response
import com.kamelia.hedera.util.Environment
import java.io.File
import kotlin.io.path.createParentDirectories
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

enum class DiskQuotaPolicy {
    UNLIMITED,
    LIMITED,
}

@Serializable
data class GlobalConfiguration(
    val enableRegistrations: Boolean = false,
    val defaultDiskQuotaPolicy: DiskQuotaPolicy = DiskQuotaPolicy.LIMITED,
    val defaultDiskQuota: Long? = 524288000, // 500 MiB
) {
    fun toDTO() = GlobalConfigurationRepresentationDTO(
        enableRegistrations,
        defaultDiskQuotaPolicy,
        defaultDiskQuota
    )
}

object GlobalConfigurationService {

    private val mutex = Mutex()

    private lateinit var _currentConfiguration: GlobalConfiguration
    val currentConfiguration: GlobalConfiguration get() = runBlocking {
        mutex.withLock { _currentConfiguration }
    }

    suspend fun init() = mutex.withLock {
        if (::_currentConfiguration.isInitialized) return
        val file = File(Environment.globalConfigurationFile)
        if (!file.exists()) generateDefaultConfiguration(file)
        _currentConfiguration = try {
            Json.decodeFromString<GlobalConfiguration>(file.readText())
        } catch (e: SerializationException) {
            throw HederaException(Errors.Configuration.PARSE_ERROR)
        }
    }

    suspend fun getConfiguration(): Response<GlobalConfigurationRepresentationDTO> = mutex.withLock {
        Response.ok(_currentConfiguration.toDTO())
    }

    /* May change in the future if we need to hide some settings */
    suspend fun getConfigurationPublic(): Response<GlobalConfigurationRepresentationDTO>  = mutex.withLock {
        Response.ok(_currentConfiguration.toDTO())
    }

    suspend fun updateConfiguration(
        dto: GlobalConfigurationUpdateDTO
    ): Response<GlobalConfigurationRepresentationDTO> = mutex.withLock {
        val enableRegistrations = dto.enableRegistrations ?: _currentConfiguration.enableRegistrations
        val (defaultDiskQuotaPolicy, defaultDiskQuota) = when {
            dto.defaultDiskQuotaPolicy == DiskQuotaPolicy.UNLIMITED -> dto.defaultDiskQuotaPolicy to null
            dto.defaultDiskQuotaPolicy == DiskQuotaPolicy.LIMITED && dto.defaultDiskQuota != null -> dto.defaultDiskQuotaPolicy to dto.defaultDiskQuota
            else -> _currentConfiguration.defaultDiskQuotaPolicy to _currentConfiguration.defaultDiskQuota
        }

        _currentConfiguration = GlobalConfiguration(
            enableRegistrations,
            defaultDiskQuotaPolicy,
            defaultDiskQuota
        )

        val response = _currentConfiguration.toDTO()
        writeConfiguration()
        ConfigurationEvents.configurationUpdatedEvent(response)
        return Response.ok(response)
    }

    private fun generateDefaultConfiguration(file: File) {
        file.toPath().createParentDirectories()
        try  {
            if (!file.createNewFile()) throw HederaException(Errors.Configuration.GENERATION_ERROR)
        } catch (e: Exception) {
            throw HederaException(Errors.Configuration.GENERATION_ERROR)
        }
        file.writeText(Json.encodeToString(GlobalConfiguration()))
    }

    private fun writeConfiguration() {
        val file = File(Environment.globalConfigurationFile)
        if (!file.exists() && !file.createNewFile()) throw HederaException(Errors.Configuration.WRITE_ERROR)
        file.writeText(Json.encodeToString(_currentConfiguration))
    }
}
