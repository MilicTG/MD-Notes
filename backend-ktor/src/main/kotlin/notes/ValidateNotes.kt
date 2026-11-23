package com.delminius.notes

import com.delminius.plugins.ApiException
import io.ktor.http.HttpStatusCode

fun validateNote(title: String, content: String) {
    val trimmed = title.trim()

    if (trimmed.isEmpty() || trimmed.length > 200) {
        throw ApiException(
            status = HttpStatusCode.BadRequest,
            code = "VALIDATION_ERROR",
            message = "Title must be between 1 and 200 characters",
        )
    }
    if (content.length > 100_000) {
        throw ApiException(
            status = HttpStatusCode.BadRequest,
            code = "VALIDATION_ERROR",
            message = "Content exceeds maximum length of 100000 characters",
        )
    }
}