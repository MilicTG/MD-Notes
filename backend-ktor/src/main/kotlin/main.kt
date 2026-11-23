package com.delminius

import com.delminius.plugins.configureDatabase
import com.delminius.plugins.configureRouting
import com.delminius.plugins.configureSerialization
import io.ktor.server.application.Application


fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}


fun Application.module() {
    configureSerialization()
    configureRouting()
    configureDatabase()
}