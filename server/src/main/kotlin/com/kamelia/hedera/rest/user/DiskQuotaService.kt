package com.kamelia.hedera.rest.user

import com.kamelia.hedera.database.Connection
import com.kamelia.hedera.rest.auth.SessionManager
import com.kamelia.hedera.util.uuid
import com.kamelia.hedera.util.withReentrantLock
import java.sql.Connection.TRANSACTION_REPEATABLE_READ
import java.util.*
import kotlinx.coroutines.sync.Mutex

object DiskQuotaService {

    private val quotas = mutableMapOf<UUID, Pair<Long, Long>>()
    private val mutex = Mutex()

    private suspend fun User.getDiskQuotaOrInsert(): Pair<Long, Long> {
        if (!quotas.containsKey(id.value)) {
            quotas[id.value] = Connection.transaction(transactionIsolation = TRANSACTION_REPEATABLE_READ) {
                repetitionAttempts = 10
                minRepetitionDelay = 500

                currentDiskQuota to maximumDiskQuota
            }
        }
        return quotas[id.value]!!
    }

    private suspend fun User.saveQuota() {
        val (current, maximum) = quotas[id.value] ?: return
        Connection.transaction {
            currentDiskQuota = current
            maximumDiskQuota = maximum
        }
        SessionManager.updateSession(this.uuid, this)
    }

    suspend fun User.getDiskQuota(): Pair<Long, Long> = withLock {
        getDiskQuotaOrInsert()
    }

    suspend fun User.setMaximumDiskQuota(maximum: Long, persist: Boolean = true) = withLock {
        check(maximum >= 0) { "Maximum disk quota must be positive" }
        quotas[id.value] = currentDiskQuota to maximum
        if (persist) {
            saveQuota()
        }
    }

    suspend fun User.increaseDiskQuota(size: Long) = withLock {
        val (current, maximum) = getDiskQuotaOrInsert()

        check(size >= 0) { "Added size must be positive" }
        check(maximum < 0 || current + size <= maximum) { "Insufficient disk quota" }

        quotas[id.value] = current + size to maximum
        saveQuota()
    }

    suspend fun User.decreaseDiskQuota(size: Long) = withLock {
        val (current, maximum) = getDiskQuotaOrInsert()

        check(size >= 0) { "Subtracted size must be positive" }
        check(current - size >= 0) { "Quota cannot be negative" }

        quotas[id.value] = current - size to maximum
        saveQuota()
    }

    private suspend fun <T> withLock(block: suspend () -> T): T {
        return mutex.withReentrantLock {
            block()
        }
    }

}