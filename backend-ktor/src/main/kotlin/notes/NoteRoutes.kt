package com.delminius.notes

import com.delminius.plugins.ApiException
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
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
            validateNote(request.title, request.content)

            val created = repository.create(request.copy(title = request.title.trim()))
            call.respond(status = HttpStatusCode.Created, message = created)
        }

        put("/{id}") {
            val id = call.parameters["id"]
                ?: throw ApiException(HttpStatusCode.BadRequest, "VALIDATION_ERROR", "Missing note id")

            val request = call.receive<UpdateNoteRequest>()
            validateNote(request.title, request.content)

            val updated = repository.update(id, request.copy(title = request.title.trim()))
                ?: throw ApiException(HttpStatusCode.NotFound, "NOT_FOUND", "Note not found")
            call.respond(updated)
        }

        delete("/{id}") {
            val id = call.parameters["id"]
                ?: return@delete call.respond(HttpStatusCode.BadRequest)
            val deleted = repository.delete(id)
            if (!deleted) {
                return@delete call.respond(HttpStatusCode.NotFound)
            }
            call.respond(HttpStatusCode.NoContent)
        }
    }
}