package com.delminius.notes

import kotlinx.serialization.Serializable

@Serializable
data class CreateNoteRequest(
    val title: String,
    val content: String = "",
)

@Serializable
data class UpdateNoteRequest(
    val title: String,
    val content: String= "",
)

@Serializable
data class NoteResponse(
    val id: String,
    val title: String,
    val content: String,
    val createdAt: String,
    val updatedAt: String,
)