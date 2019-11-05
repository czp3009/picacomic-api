package com.hiczp.picacomic.api

import com.hiczp.caeruleum.create
import com.hiczp.picacomic.api.service.main.MainService
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
import io.ktor.client.request.accept
import io.ktor.client.request.header
import io.ktor.http.ContentType
import kotlinx.io.core.Closeable
import org.slf4j.LoggerFactory
import java.time.Instant
import kotlin.random.Random

private const val picaAPIBaseUrl = "https://picaapi.picacomic.com/"

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
            accept(ContentType("application", "vnd.picacomic.com.v1+json"))
            header("api-key", "C69BAF41DA5ABD1FFEDC6D2FEA56B")
            header("app-channel", "")
            header("time", Instant.now().epochSecond)
            header("nonce", Random.nextString(32, ('0'..'9') + ('a'..'f')))
        }

        install(JsonFeature) {
            serializer = GsonSerializer()
        }

        config()
    }

    val main by lazy { httpClient.create<MainService>(picaAPIBaseUrl) }

    override fun close() {
        httpClient.close()
    }
}
