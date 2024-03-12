package com.kamelia.hedera.rest.user

import com.kamelia.hedera.database.Connection
import com.kamelia.hedera.rest.auth.SessionManager
import com.kamelia.hedera.rest.auth.UserState
import com.kamelia.hedera.rest.auth.toUserState
import com.kamelia.hedera.util.uuid
import com.kamelia.hedera.util.withReentrantLock
import java.sql.Connection.TRANSACTION_REPEATABLE_READ
import java.time.Instant
import java.util.*
import kotlinx.coroutines.sync.Mutex

class DiskQuotaContainer(
    var current: Long,
    var maximum: Long,
    var lastUpdate: Instant = Instant.now(),
) {
    operator fun component1() = current
    operator fun component2() = maximum
}

object DiskQuotaService {

    private const val MAXIMUM_CACHE_SIZE = 32

    private val quotas = mutableMapOf<UUID, DiskQuotaContainer>()
    private val mutex = Mutex()

    suspend fun User.getDiskQuota(): DiskQuotaContainer = withLock {
        getDiskQuotaOrInsert()
    }

    suspend fun User.setMaximumDiskQuota(maximum: Long, persist: Boolean = true) = withLock {
        val quota = getDiskQuotaOrInsert()
        check(maximum >= -1) { "Maximum disk quota must be positive or unlimited" }
        quota.maximum = maximum
        quota.lastUpdate = Instant.now()
        if (persist) {
            saveQuota(quota)
        }
    }

    suspend fun User.increaseDiskQuota(size: Long) = withLock {
        val quota = getDiskQuotaOrInsert()
        val (current, maximum) = quota

        check(size >= 0) { "Added size must be positive" }
        check(maximum < 0 || current + size <= maximum) { "Insufficient disk quota" }

        quota.current += size
        quota.lastUpdate = Instant.now()
        saveQuota(quota)
    }

    suspend fun User.decreaseDiskQuota(size: Long) = withLock {
        val quota = getDiskQuotaOrInsert()
        val (current, maximum) = quota

        check(size >= 0) { "Subtracted size must be positive" }
        check(current - size >= 0) { "Quota cannot be negative" }

        quota.current -= size
        quota.lastUpdate = Instant.now()
        saveQuota(quota)
    }

    private suspend fun User.getDiskQuotaOrInsert(): DiskQuotaContainer {
        if (quotas.size >= MAXIMUM_CACHE_SIZE) {
            quotas.entries
                .sortedByDescending { it.value.lastUpdate }
                .take(quotas.size - MAXIMUM_CACHE_SIZE + 1)
                .forEach { quotas.remove(it.key) }
        }
        return quotas.getOrPut(id.value) {
            Connection.transaction(transactionIsolation = TRANSACTION_REPEATABLE_READ) {
                repetitionAttempts = 10
                minRepetitionDelay = 500

                DiskQuotaContainer(currentDiskQuota, maximumDiskQuota)
            }
        }
    }

    private suspend fun User.saveQuota(quota: DiskQuotaContainer) {
        val (current, maximum) = quota
        Connection.transaction {
            currentDiskQuota = current
            maximumDiskQuota = maximum
        }
        SessionManager.updateSession(this.uuid, this)
    }

    private suspend inline fun <T> withLock(noinline block: suspend () -> T): T = mutex.withReentrantLock(block)

}
