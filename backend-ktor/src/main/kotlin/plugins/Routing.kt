package com.delminius.plugins

import com.delminius.health.healthRoutes
import com.delminius.notes.NoteRepository
import com.delminius.notes.notesRoutes
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {

    val repository = NoteRepository()

    routing {
        healthRoutes()
        notesRoutes(repository = repository)
    }
}