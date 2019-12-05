package com.hiczp.picacomic.api

import com.github.salomonbrys.kotson.*
import com.hiczp.caeruleum.create
import com.hiczp.picacomic.api.feature.doBeforeSend
import com.hiczp.picacomic.api.feature.logging
import com.hiczp.picacomic.api.service.Response
import com.hiczp.picacomic.api.service.announcement.AnnouncementService
import com.hiczp.picacomic.api.service.auth.AuthService
import com.hiczp.picacomic.api.service.banner.BannerService
import com.hiczp.picacomic.api.service.category.CategoryService
import com.hiczp.picacomic.api.service.chat.ChatService
import com.hiczp.picacomic.api.service.comic.ComicService
import com.hiczp.picacomic.api.service.comment.CommentService
import com.hiczp.picacomic.api.service.episode.EpisodeService
import com.hiczp.picacomic.api.service.game.GameService
import com.hiczp.picacomic.api.service.init.InitService
import com.hiczp.picacomic.api.service.keyword.KeywordService
import com.hiczp.picacomic.api.service.picaapp.PicaAppService
import com.hiczp.picacomic.api.service.user.UserService
import com.hiczp.picacomic.api.service.util.UtilService
import com.hiczp.picacomic.api.utils.*
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.features.ClientRequestException
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.userAgent
import kotlinx.io.core.Closeable
import java.time.Instant
import kotlin.random.Random

internal const val picaAPIBaseUrl = "https://picaapi.picacomic.com"

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
            val path = url.buildString().substringAfter("$picaAPIBaseUrl/")
            val raw = "$path$time$nonce${method.value}${PicaComicInherent.apiKey}".toLowerCase()
            val signature = hmacSHA256(PicaComicInherent.hmacSha256Key, raw).convertToString()
            with(PicaComicInherent) {
                header("api-key", apiKey)
                header("app-channel", appChannel)
                header("time", time)
                header("nonce", nonce)
                header("signature", signature)
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
    val announcement by lazy { httpClient.create<AnnouncementService>("$picaAPIBaseUrl/announcements") }
    val auth by lazy { httpClient.create<AuthService>("$picaAPIBaseUrl/auth/") }
    val banner by lazy { httpClient.create<BannerService>("$picaAPIBaseUrl/banners") }
    val category by lazy { httpClient.create<CategoryService>("$picaAPIBaseUrl/categories") }
    val chat by lazy { httpClient.create<ChatService>("$picaAPIBaseUrl/chat") }
    val comic by lazy { httpClient.create<ComicService>("$picaAPIBaseUrl/comics/") }
    val comment by lazy { httpClient.create<CommentService>("$picaAPIBaseUrl/comments/") }
    val episode by lazy { httpClient.create<EpisodeService>("$picaAPIBaseUrl/eps/") }
    val game by lazy { httpClient.create<GameService>("$picaAPIBaseUrl/games/") }
    val keyword by lazy { httpClient.create<KeywordService>("$picaAPIBaseUrl/keywords") }
    val picaApp by lazy { httpClient.create<PicaAppService>("$picaAPIBaseUrl/pica-apps") }
    val user by lazy { httpClient.create<UserService>("$picaAPIBaseUrl/users/") }
    val util by lazy { httpClient.create<UtilService>("$picaAPIBaseUrl/utils/") }

    suspend fun signIn(email: String, password: String) =
        auth.signIn(email, password).also {
            if (it.ok()) token = it.data
        }

    suspend fun checkAuthorize() = if (token == null) {
        false
    } else {
        try {
            !user.getProfile().unauthorized()
        } catch (e: ClientRequestException) {
            if (e.response.status == HttpStatusCode.Unauthorized) {
                false
            } else {
                throw e
            }
        }
    }

    override fun close() {
        httpClient.close()
    }
}
