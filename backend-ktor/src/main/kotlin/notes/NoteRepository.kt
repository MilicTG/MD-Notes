package com.delminius.notes

import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.time.Instant
import java.util.UUID

class NoteRepository {

    fun create(request: CreateNoteRequest): NoteResponse = transaction {
        val id = UUID.randomUUID().toString()
        val now = Instant.now().toString()

        NotesTable.insert {
            it[NotesTable.id] = id
            it[title] = request.title
            it[content] = request.content
            it[createdAt] = now
            it[updatedAt] = now
        }

        NoteResponse(
            id = id,
            title = request.title,
            content = request.content,
            createdAt = now,
            updatedAt = now,
        )
    }

    fun findAll(): List<NoteResponse> = transaction {
        NotesTable
            .selectAll()
            .orderBy(NotesTable.updatedAt, SortOrder.DESC)
            .map { it.toNoteResponse() }
    }

    fun findById(id: String): NoteResponse? = transaction {
        NotesTable
            .selectAll()
            .where { NotesTable.id eq id }
            .singleOrNull()
            ?.toNoteResponse()
    }

    private fun ResultRow.toNoteResponse() = NoteResponse(
        id = this[NotesTable.id],
        title = this[NotesTable.title],
        content = this[NotesTable.content],
        createdAt = this[NotesTable.createdAt],
        updatedAt = this[NotesTable.updatedAt],
    )
}