package com.delminius.notes

import org.jetbrains.exposed.v1.core.Table

object NotesTable : Table("notes") {
    val id = varchar("id", 36)
    val title = varchar("title", 255)
    val content = text("content").default("")
    val createdAt = varchar("created_at", 64)
    val updatedAt = varchar("updated_at", 64)

    override val primaryKey = PrimaryKey(id)
}