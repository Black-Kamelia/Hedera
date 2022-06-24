package com.kamelia.jellyfish.util

import io.ktor.server.application.Application

object Environment {
    private val Application.envKind get() = environment.config.propertyOrNull("ktor.environment")?.getString()
    val Application.isDev get() = envKind != null && envKind == "dev"
    val Application.isProd get() = envKind != null && envKind != "dev"
}
