package com.delminius.plugins

import com.delminius.notes.NotesTable
import io.ktor.server.application.Application
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.io.File

fun Application.configureDatabase() {
    File("data").mkdirs()

    Database.connect(
        url = "jdbc:sqlite:data/notes.db",
        driver = "org.sqlite.JDBC"
    )

    transaction {
        SchemaUtils.create(NotesTable)
    }
}