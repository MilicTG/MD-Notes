package com.delminius.notes

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.notesRoutes(repository: NoteRepository) {
    route("/api/notes") {
        get {
            call.respond(repository.findAll())
        }

        get("/{id}") {
            val id = call.parameters["id"]
                ?: return@get call.respond(HttpStatusCode.BadRequest)
            val note = repository.findById(id)
                ?: return@get call.respond(HttpStatusCode.NotFound)
            call.respond(note)
        }

        post {
            val request = call.receive<CreateNoteRequest>()
            val created = repository.create(request)
            call.respond(status = HttpStatusCode.Created, created)
        }
    }
}