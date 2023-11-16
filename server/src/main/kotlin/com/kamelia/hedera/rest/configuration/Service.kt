package com.kamelia.hedera.rest.configuration

import com.kamelia.hedera.core.Errors
import com.kamelia.hedera.core.HederaException
import com.kamelia.hedera.core.Response
import com.kamelia.hedera.util.Environment
import com.kamelia.hedera.util.suspendUntilUnlocked
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
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

object GlobalConfigurationService {

    // Mutex to prevent concurrent read+write. Read-only is allowed if the lock is free.
    private val readWriteMutex = Mutex()

    private lateinit var _currentConfiguration: GlobalConfiguration
    val currentConfiguration: GlobalConfiguration get() = runBlocking {
        readWriteMutex.suspendUntilUnlocked()
        _currentConfiguration
    }

    suspend fun init() = readWriteMutex.withLock {
        if (::_currentConfiguration.isInitialized) return
        val file = File(Environment.globalConfigurationFile)
        if (!file.exists()) generateDefaultConfiguration(file)
        _currentConfiguration = try {
            Json.decodeFromString<GlobalConfiguration>(file.readText())
        } catch (e: SerializationException) {
            throw HederaException(Errors.Configuration.PARSE_ERROR)
        }
    }

    suspend fun getConfiguration(): Response<GlobalConfigurationRepresentationDTO> {
        readWriteMutex.suspendUntilUnlocked()
        return Response.ok(_currentConfiguration.toDTO())
    }

    /* May change in the future if we need to hide some settings */
    suspend fun getConfigurationPublic(): Response<GlobalConfigurationRepresentationDTO> {
        readWriteMutex.suspendUntilUnlocked()
        return Response.ok(_currentConfiguration.toDTO())
    }

    suspend fun updateConfiguration(
        dto: GlobalConfigurationUpdateDTO
    ): Response<GlobalConfigurationRepresentationDTO> = readWriteMutex.withLock {
        dto.enableRegistrations?.let { _currentConfiguration.enableRegistrations = it }
        if (dto.defaultDiskQuotaPolicy == DiskQuotaPolicy.UNLIMITED) {
            _currentConfiguration.defaultDiskQuotaPolicy = dto.defaultDiskQuotaPolicy
            _currentConfiguration.defaultDiskQuota = null
        } else if (dto.defaultDiskQuotaPolicy == DiskQuotaPolicy.LIMITED && dto.defaultDiskQuota != null) {
            _currentConfiguration.defaultDiskQuotaPolicy = dto.defaultDiskQuotaPolicy
            _currentConfiguration.defaultDiskQuota = dto.defaultDiskQuota
        }
        writeConfiguration()
        ConfigurationEvents.configurationUpdatedEvent(_currentConfiguration.toDTO())

        return Response.ok(_currentConfiguration.toDTO())
    }

    private fun generateDefaultConfiguration(file: File) {
        if (!file.createNewFile()) throw HederaException(Errors.Configuration.GENERATION_ERROR)
        file.writeText(Json.encodeToString(GlobalConfiguration()))
    }

    private fun writeConfiguration() {
        val file = File(Environment.globalConfigurationFile)
        if (!file.exists() && !file.createNewFile()) throw HederaException(Errors.Configuration.WRITE_ERROR)
        file.writeText(Json.encodeToString(_currentConfiguration))
    }
}
