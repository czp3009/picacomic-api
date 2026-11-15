package com.hiczp.picacomic.api.internal

import com.hiczp.picacomic.api.PicaComicClientConfig
import com.hiczp.picacomic.api.PicaConstants
import com.hiczp.picacomic.api.PicaSession
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.api.SendingRequest
import io.ktor.http.HttpHeaders

internal class PicaRequestPluginConfig {
    lateinit var session: PicaSession
    lateinit var clientConfig: PicaComicClientConfig
}

internal val PicaRequestPlugin = createClientPlugin(
    name = "PicaRequestPlugin",
    createConfiguration = ::PicaRequestPluginConfig,
) {
    val session = pluginConfig.session
    val configuration = pluginConfig.clientConfig
    onRequest { request, _ ->
        normalizeRetrofitEncoding(request.url)
        val url = request.url.build()
        val time = configuration.timeProvider().toInt().toString()
        val signature = PicaSigner.sign(
            encodedPath = url.encodedPath,
            encodedQuery = url.encodedQuery,
            time = time,
            nonce = session.identity.nonce,
            method = request.method.value,
        )
        request.headers.apply {
            remove(HttpHeaders.Accept)
            append(HttpHeaders.Accept, "application/vnd.picacomic.com.v1+json")
            remove("time")
            append("time", time)
            remove("nonce")
            append("nonce", session.identity.nonce)
            remove("signature")
            append("signature", signature)
            remove("api-key")
            append("api-key", PicaConstants.API_KEY)
            remove("app-channel")
            append("app-channel", "1")
            remove("app-version")
            append("app-version", "2.3.1.20241111")
            remove("app-uuid")
            append("app-uuid", session.identity.applicationUuid)
            remove("app-platform")
            append("app-platform", "android")
            remove("app-build-version")
            append("app-build-version", "20250144")
            remove(HttpHeaders.UserAgent)
            append(HttpHeaders.UserAgent, "okhttp/3.8.1")
            remove("image-quality")
            append("image-quality", configuration.imageQuality.headerValue)
            remove(HttpHeaders.Authorization)
            session.credential()?.let { append(HttpHeaders.Authorization, it) }
            if (request.method.value == "POST" || request.method.value == "PUT") {
                remove(HttpHeaders.ContentType)
                append(HttpHeaders.ContentType, "application/json; charset=UTF-8")
            }
            if (url.encodedPath == "/comics/random") {
                remove(HttpHeaders.CacheControl)
                append(HttpHeaders.CacheControl, "no-cache")
            }
        }
    }
    on(SendingRequest) { request, _ ->
        request.headers.apply {
            remove(HttpHeaders.Accept)
            append(HttpHeaders.Accept, "application/vnd.picacomic.com.v1+json")
            if (request.method.value == "POST" || request.method.value == "PUT") {
                remove(HttpHeaders.ContentType)
                append(HttpHeaders.ContentType, "application/json; charset=UTF-8")
            }
        }
    }
}
