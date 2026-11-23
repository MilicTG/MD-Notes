package com.delminius.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import kotlinx.serialization.Serializable

@Serializable
data class ErrorDetail(
    val code: String,
    val message: String,
)

@Serializable
data class ErrorResponse(
    val error: ErrorDetail
)

class ApiException(
    val status: HttpStatusCode,
    val code: String,
    override val message: String,
) : RuntimeException(message)

fun Application.configureStatusPages() {
    install(plugin = StatusPages) {

        exception<ApiException> { call, cause ->
            call.respond(
                status = cause.status,
                message = ErrorResponse(ErrorDetail(cause.code, cause.message)),
            )
        }

        exception<BadRequestException> { call, _ ->
            call.respond(
                status = HttpStatusCode.BadRequest,
                message = ErrorResponse(ErrorDetail("VALIDATION_ERROR", "Invalid request body")),
            )
        }
        exception<Throwable> { call, cause ->
            call.application.environment.log.error("Unhandled exception", cause)
            call.respond(
                status = HttpStatusCode.InternalServerError,
                message = ErrorResponse(ErrorDetail("INTERNAL_ERROR", "Internal server error")),
            )
        }
    }
}