package aashishtathod.dev.data.db

import aashishtathod.dev.data.db.tables.NotesTable
import aashishtathod.dev.data.db.tables.UsersTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    fun init() {
        val tables = arrayOf(UsersTable, NotesTable)

        Database.connect(createDataSource())

        transaction {
            SchemaUtils.createMissingTablesAndColumns(*tables)
        }
    }

    private fun createDataSource(): HikariDataSource {
        val config = HikariConfig()

        val dbHost = System.getenv("PGHOST")
        val dbPort = System.getenv("PGPORT")
        val dbName = System.getenv("PGDATABASE")

        config.driverClassName = System.getenv("JDBC_DRIVER")
        config.password = System.getenv("PGPASSWORD")
        config.username = System.getenv("PGUSER")
        config.jdbcUrl = "jdbc:postgresql://$dbHost:$dbPort/$dbName"
        config.maximumPoolSize = 3
        config.isAutoCommit = true
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        config.validate()
        return HikariDataSource(config)
    }

    suspend fun <T> dbQuery(block: () -> T): T =
        withContext(Dispatchers.IO) {
            transaction {
                block()
            }
        }
}