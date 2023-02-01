package com.kamelia.jellyfish.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync

object Connection {

    private lateinit var dataSource: HikariDataSource
    private lateinit var database: Database

    val connection: Connection get() = dataSource.connection

    fun init() {
        dataSource = hikari()
        database = Database.connect(dataSource)
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig("/hikari.properties")
        config.validate()
        return HikariDataSource(config)
    }

    suspend fun <T> transaction(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        block: suspend Transaction.() -> T,
    ): T = suspendedTransactionAsync(dispatcher, database) {
        block()
    }.await()
}
