package com.hiczp.picacomic.api.feature

import io.ktor.client.HttpClientConfig
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logging
import org.slf4j.LoggerFactory

internal fun HttpClientConfig<*>.logging(logLevel: LogLevel = LogLevel.ALL, func: () -> Unit) = install(Logging) {
    level = if (LoggerFactory.getLogger(func.javaClass).isDebugEnabled) {
        logLevel
    } else {
        LogLevel.NONE
    }
}
