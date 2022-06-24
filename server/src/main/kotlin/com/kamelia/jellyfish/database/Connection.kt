package com.kamelia.jellyfish.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

object Connection {

    private lateinit var dataSource: HikariDataSource
    val connection: Connection get() = dataSource.connection

    fun init() {
        dataSource = hikari()
        Database.connect(dataSource)
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig("/hikari.properties")
        config.validate()
        return HikariDataSource(config)
    }

    suspend fun <T> query(block: () -> T): T {
        return withContext(Dispatchers.IO) {
            transaction {
                block()
            }
        }
    }
}
