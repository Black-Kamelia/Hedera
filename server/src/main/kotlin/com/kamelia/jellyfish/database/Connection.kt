package com.kamelia.jellyfish.database

import com.kamelia.jellyfish.rest.user.Users
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.transactions.transaction

object Connection {

    fun init() {
        Database.connect(hikari())
        transaction {
            if (!Users.exists()) SchemaUtils.create(Users)
        }
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
