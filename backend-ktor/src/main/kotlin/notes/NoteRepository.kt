package com.delminius.notes

import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
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

    fun update(id: String, request: UpdateNoteRequest): NoteResponse? = transaction {
        val now = Instant.now().toString()

        val updateRows = NotesTable.update({ NotesTable.id eq id }) {
            it[title] = request.title
            it[content] = request.content
            it[updatedAt] = now
        }

        if (updateRows == 0) {
            null
        } else {
            NotesTable
                .selectAll()
                .where { NotesTable.id eq id }
                .singleOrNull()
                ?.toNoteResponse()
        }
    }

    fun delete(id: String): Boolean = transaction {
        val deletedRows = NotesTable.deleteWhere { NotesTable.id eq id }
        deletedRows > 0
    }

    private fun ResultRow.toNoteResponse() = NoteResponse(
        id = this[NotesTable.id],
        title = this[NotesTable.title],
        content = this[NotesTable.content],
        createdAt = this[NotesTable.createdAt],
        updatedAt = this[NotesTable.updatedAt],
    )
}