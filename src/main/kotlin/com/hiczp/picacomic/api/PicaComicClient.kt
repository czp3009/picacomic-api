package com.hiczp.picacomic.api

import com.github.salomonbrys.kotson.*
import com.hiczp.caeruleum.create
import com.hiczp.picacomic.api.feature.doBeforeSend
import com.hiczp.picacomic.api.feature.logging
import com.hiczp.picacomic.api.service.Response
import com.hiczp.picacomic.api.service.Thumbnail
import com.hiczp.picacomic.api.service.auth.AuthService
import com.hiczp.picacomic.api.service.category.CategoryService
import com.hiczp.picacomic.api.service.comic.ComicService
import com.hiczp.picacomic.api.service.comment.CommentService
import com.hiczp.picacomic.api.service.episode.EpisodeService
import com.hiczp.picacomic.api.service.game.GameService
import com.hiczp.picacomic.api.service.init.InitService
import com.hiczp.picacomic.api.service.keyword.KeywordService
import com.hiczp.picacomic.api.service.user.UserService
import com.hiczp.picacomic.api.service.util.UtilService
import com.hiczp.picacomic.api.utils.*
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.userAgent
import kotlinx.coroutines.io.ByteReadChannel
import kotlinx.io.core.Closeable
import java.time.Instant
import kotlin.random.Random

private const val picaAPI = "https://picaapi.picacomic.com"

/**
 * 如果密钥不正确(未来的更新), 所有 API 将始终返回 success 且无 data 字段
 */
@Suppress("MemberVisibilityCanBePrivate")
class PicaComicClient<out T : HttpClientEngineConfig>(
    engine: HttpClientEngineFactory<T>,
    logLevel: LogLevel? = null,
    config: HttpClientConfig<T>.() -> Unit = {}
) : Closeable {
    var token: String? = null

    private val httpClient = HttpClient(engine) {
        logging(logLevel ?: LogLevel.ALL) {}
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
                        "${url.buildString().substringAfter("$picaAPI/")}$time$nonce${method.value}$apiKey".toLowerCase()
                    ).convertToString()
                )
                header("app-version", appVersion)
                header("app-uuid", appUUID)
                header("image-quality", imageQuality)
                header("app-platform", appPlatform)
                header("app-build-version", appBuildVersion)
                userAgent(userAgent)
            }

            val currentToken = token
            if (currentToken == null || attributes.containsByKey(noAuthAttribute)) return@defaultRequest
            header("authorization", currentToken)
        }
        install(JsonFeature) {
            serializer = GsonSerializer {
                registerTypeAdapter<Response<*>> {
                    deserialize { (json, type, context) ->
                        val rootJsonObject = json.obj
                        val dataElement = rootJsonObject.get("data")
                        val data = if (dataElement == null || !dataElement.isJsonObject) {
                            null
                        } else {
                            val dataJsonObject = dataElement.obj
                            val entrySet = dataJsonObject.entrySet()
                            when {
                                entrySet.isEmpty() -> null
                                //use Any to make type system happy
                                entrySet.size == 1 -> context.deserialize<Any>(
                                    entrySet.first().value,
                                    type.actualTypeArguments.first()
                                )
                                else -> context.deserialize(dataJsonObject, type.actualTypeArguments.first())
                            }
                        }
                        with(json.obj) {
                            Response(
                                get("code").int,
                                get("error").nullString,
                                get("message").string,
                                get("detail").nullString,
                                data
                            )
                        }
                    }
                }
            }
        }
        doBeforeSend {
            headers.removeAndAdd("accept", PicaComicInherent.accept)
            headers.remove(HttpHeaders.AcceptCharset)
        }
        config()
    }

    val init by lazy { httpClient.create<InitService>() }
    val auth by lazy { httpClient.create<AuthService>("$picaAPI/auth/") }
    val category by lazy { httpClient.create<CategoryService>("$picaAPI/categories") }
    val comic by lazy { httpClient.create<ComicService>("$picaAPI/comics/") }
    val comment by lazy { httpClient.create<CommentService>("$picaAPI/comments/") }
    val episode by lazy { httpClient.create<EpisodeService>("$picaAPI/eps/") }
    val game by lazy { httpClient.create<GameService>("$picaAPI/games/") }
    val keyword by lazy { httpClient.create<KeywordService>("$picaAPI/keywords") }
    val user by lazy { httpClient.create<UserService>("$picaAPI/users/") }
    val util by lazy { httpClient.create<UtilService>("$picaAPI/utils/") }

    suspend fun signIn(email: String, password: String) =
        auth.signIn(email, password).also {
            if (it.ok()) token = it.data
        }

    suspend fun checkAuthorize() = if (token == null) {
        false
    } else {
        !user.getProfile().unauthorized()
    }

    private val downloader = HttpClient(engine) {
        logging(logLevel ?: LogLevel.HEADERS) {}
        config()
    }

    suspend fun downloadFile(urlString: String) = downloader.get<ByteReadChannel>(urlString)

    suspend fun downloadFile(thumbnail: Thumbnail) = downloadFile(thumbnail.urlString)

    override fun close() {
        httpClient.close()
        downloader.close()
    }
}
