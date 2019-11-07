package com.hiczp.picacomic.api

import com.hiczp.caeruleum.create
import com.hiczp.picacomic.api.feature.doBeforeSend
import com.hiczp.picacomic.api.service.auth.AuthService
import com.hiczp.picacomic.api.service.auth.model.SignInRequest
import com.hiczp.picacomic.api.service.main.MainService
import com.hiczp.picacomic.api.utils.convertToString
import com.hiczp.picacomic.api.utils.hmacSHA256
import com.hiczp.picacomic.api.utils.nextString
import com.hiczp.picacomic.api.utils.removeAndAdd
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
import io.ktor.http.HttpHeaders
import io.ktor.http.userAgent
import kotlinx.io.core.Closeable
import org.slf4j.LoggerFactory
import java.time.Instant
import kotlin.random.Random

private const val picaAPI = "https://picaapi.picacomic.com"

/**
 * 如果密钥不正确(未来的更新), 所有 API 将始终返回 success 且无 data 字段
 */
class PicaComicClient<out T : HttpClientEngineConfig>(
    engine: HttpClientEngineFactory<T>,
    config: HttpClientConfig<T>.() -> Unit = {}
) : Closeable {
    var token: String? = null

    private val httpClient = HttpClient(engine) {
        install(Logging) {
            level = if (LoggerFactory.getLogger(PicaComicClient::class.java).isDebugEnabled) {
                LogLevel.ALL
            } else {
                LogLevel.NONE
            }
        }

        defaultRequest {
            if (attributes.containsByKey(noSignAttribute)) return@defaultRequest
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

            if (token == null || attributes.containsByKey(noAuthAttribute)) return@defaultRequest
            header("authorization", token)
        }

        install(JsonFeature) {
            serializer = GsonSerializer()
        }

        doBeforeSend {
            headers.removeAndAdd("accept", PicaComicInherent.accept)
            headers.remove(HttpHeaders.AcceptCharset)
        }

        config()
    }

    val main by lazy { httpClient.create<MainService>() }
    val auth by lazy { httpClient.create<AuthService>("$picaAPI/auth/") }

    suspend fun signIn(email: String, password: String) =
        auth.signIn(SignInRequest(email, password)).also {
            if (it.code == 200) token = it.data.token
        }

    override fun close() {
        httpClient.close()
    }
}
