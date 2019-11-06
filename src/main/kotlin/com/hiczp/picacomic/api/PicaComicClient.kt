package com.hiczp.picacomic.api

import com.hiczp.caeruleum.create
import com.hiczp.picacomic.api.feature.forceOverrideHeaders
import com.hiczp.picacomic.api.service.main.MainService
import com.hiczp.picacomic.api.utils.convertToString
import com.hiczp.picacomic.api.utils.hmacSHA256
import com.hiczp.picacomic.api.utils.nextString
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.userAgent
import kotlinx.io.core.Closeable
import org.slf4j.LoggerFactory
import java.time.Instant
import kotlin.random.Random

private const val picaAPI = "https://picaapi.picacomic.com"

class PicaComicClient<out T : HttpClientEngineConfig>(
    engine: HttpClientEngineFactory<T>,
    config: HttpClientConfig<T>.() -> Unit = {}
) : Closeable {
    private val httpClient = HttpClient(engine) {
        install(Logging) {
            level = if (LoggerFactory.getLogger(PicaComicClient::class.java).isDebugEnabled) {
                LogLevel.ALL
            } else {
                LogLevel.NONE
            }
        }

        defaultRequest {
            val time = Instant.now().epochSecond
            val nonce = Random.nextString(('0'..'9') + ('a'..'f'), 32)

            with(PicaComicInherent) {
                header("api-key", apiKey)
                header("app-channel", appChannel)
                header("time", time)
                header("nonce", nonce)
                header(
                    "signature",
                    hmacSHA256(
                        hmacSha256Key,
                        "${url.encodedPath.drop(1)}$time$nonce${method.value}$apiKey".toLowerCase()
                    ).convertToString()
                )
                header("app-version", appVersion)
                header("app-uuid", appUUID)
                header("image-quality", imageQuality)
                header("app-platform", appPlatform)
                header("app-build-version", appBuildVersion)
                userAgent(userAgent)
            }
        }

        install(JsonFeature) {
            serializer = GsonSerializer()
        }

        forceOverrideHeaders {
            append("accept", PicaComicInherent.accept)
        }

        config()
    }

    val main by lazy { httpClient.create<MainService>("$picaAPI/") }

    override fun close() {
        httpClient.close()
    }
}
