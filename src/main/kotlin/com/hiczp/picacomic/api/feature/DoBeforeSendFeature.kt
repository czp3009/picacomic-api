package com.hiczp.picacomic.api.feature

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.features.HttpClientFeature
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.util.AttributeKey

internal class DoBeforeSendFeature(private val builder: HttpRequestBuilder.() -> Unit) {
    companion object Feature : HttpClientFeature<HttpRequestBuilder, DoBeforeSendFeature> {
        override val key = AttributeKey<DoBeforeSendFeature>("DoBeforeSend")

        override fun prepare(block: HttpRequestBuilder.() -> Unit) = DoBeforeSendFeature(block)

        override fun install(feature: DoBeforeSendFeature, scope: HttpClient) {
            scope.requestPipeline.intercept(HttpRequestPipeline.Render) {
                context.apply(feature.builder)
            }
        }
    }
}

fun HttpClientConfig<*>.doBeforeSend(block: HttpRequestBuilder.() -> Unit) {
    install(DoBeforeSendFeature) {
        block()
    }
}
