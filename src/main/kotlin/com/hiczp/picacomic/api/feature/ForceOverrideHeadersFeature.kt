package com.hiczp.picacomic.api.feature

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.features.HttpClientFeature
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.client.request.headers
import io.ktor.http.Headers
import io.ktor.http.HeadersBuilder
import io.ktor.util.AttributeKey
import io.ktor.util.valuesOf

class ForceOverrideHeadersFeature internal constructor(
    val headers: Headers
) {
    class Config {
        val headers = HeadersBuilder()
    }

    companion object Feature : HttpClientFeature<Config, ForceOverrideHeadersFeature> {
        override val key = AttributeKey<ForceOverrideHeadersFeature>("OverrideHeaders")

        override fun prepare(block: Config.() -> Unit): ForceOverrideHeadersFeature {
            val config = Config().apply(block)
            return ForceOverrideHeadersFeature(config.headers.build())
        }

        override fun install(feature: ForceOverrideHeadersFeature, scope: HttpClient) {
            scope.requestPipeline.intercept(HttpRequestPipeline.Transform) {
                feature.headers.forEach { key, values ->
                    context.headers {
                        remove(key)
                        appendAll(valuesOf(key, values))
                    }
                }
            }
        }
    }
}

fun HttpClientConfig<*>.forceOverrideHeaders(block: HeadersBuilder.() -> Unit) {
    install(ForceOverrideHeadersFeature) {
        headers.apply(block)
    }
}
