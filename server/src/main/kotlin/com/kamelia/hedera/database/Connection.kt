package com.kamelia.hedera.database

import com.kamelia.hedera.util.Environment
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import java.sql.Connection

object Connection {

    private lateinit var dataSource: HikariDataSource
    private lateinit var database: Database

    val connection: Connection get() = dataSource.connection

    fun init() {
        dataSource = hikari()
        database = Database.connect(dataSource)
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig("/hikari.properties").apply {
            jdbcUrl = "jdbc:postgresql://${Environment.databaseHost}:${Environment.databasePort}/${Environment.databaseName}"
            username = Environment.databaseUsername
            password = Environment.databasePassword
        }
        return HikariDataSource(config)
    }

    suspend fun <T> transaction(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        block: suspend Transaction.() -> T,
    ): T = suspendedTransactionAsync(dispatcher, database) {
        block()
    }.await()
}
