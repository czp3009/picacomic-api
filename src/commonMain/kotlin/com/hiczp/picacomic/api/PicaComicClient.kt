package com.hiczp.picacomic.api

import com.hiczp.picacomic.api.internal.PicaRequestPlugin
import com.hiczp.picacomic.api.internal.RetrofitEncodingPlugin
import com.hiczp.picacomic.api.model.AdvancedSearchBody
import com.hiczp.picacomic.api.model.AdZone
import com.hiczp.picacomic.api.model.AdZonesResponse
import com.hiczp.picacomic.api.model.AnimeCatalogResponse
import com.hiczp.picacomic.api.model.AnimeCategory
import com.hiczp.picacomic.api.model.AnimeEntry
import com.hiczp.picacomic.api.model.AnimePage
import com.hiczp.picacomic.api.model.AnnouncementsResponse
import com.hiczp.picacomic.api.model.AvatarUpdateRequest
import com.hiczp.picacomic.api.model.BannersResponse
import com.hiczp.picacomic.api.model.CategoriesData
import com.hiczp.picacomic.api.model.ChapterPagesData
import com.hiczp.picacomic.api.model.CollectionsResponseData
import com.hiczp.picacomic.api.model.ComicDetailData
import com.hiczp.picacomic.api.model.CommentLikeResponse
import com.hiczp.picacomic.api.model.CommentResponseData
import com.hiczp.picacomic.api.model.ComicsListResponse
import com.hiczp.picacomic.api.model.ContentBody
import com.hiczp.picacomic.api.model.EpisodesData
import com.hiczp.picacomic.api.model.GameCommentResponseData
import com.hiczp.picacomic.api.model.GameDetailResponse
import com.hiczp.picacomic.api.model.GameLikeResponse
import com.hiczp.picacomic.api.model.GamesResponse
import com.hiczp.picacomic.api.model.InitConfigResponse
import com.hiczp.picacomic.api.model.KeywordsData
import com.hiczp.picacomic.api.model.LeaderboardData
import com.hiczp.picacomic.api.model.LoginData
import com.hiczp.picacomic.api.model.MyCommentsResponseData
import com.hiczp.picacomic.api.model.PicaResponse
import com.hiczp.picacomic.api.model.PlayEpisode
import com.hiczp.picacomic.api.model.PlaySource
import com.hiczp.picacomic.api.model.PunchInResponse
import com.hiczp.picacomic.api.model.RandomComicsResponse
import com.hiczp.picacomic.api.model.RecommendationTrackingRequest
import com.hiczp.picacomic.api.model.RegisterRequest
import com.hiczp.picacomic.api.model.SmartDnsData
import com.hiczp.picacomic.api.model.SloganUpdateRequest
import com.hiczp.picacomic.api.model.UserProfileData
import com.hiczp.picacomic.api.model.VersionCheckData
import com.hiczp.picacomic.api.model.VersionCheckResponse
import com.hiczp.picacomic.api.service.AnimeApi
import com.hiczp.picacomic.api.service.NoCacheRequest
import com.hiczp.picacomic.api.service.PicaApi
import com.hiczp.picacomic.api.service.RecommendationApi
import com.hiczp.picacomic.api.service.SmartDnsApi
import com.hiczp.picacomic.api.service.createAnimeApi
import com.hiczp.picacomic.api.service.createPicaApi
import com.hiczp.picacomic.api.service.createRecommendationApi
import com.hiczp.picacomic.api.service.createSmartDnsApi
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/** Supported advertisement placement endpoints. */
public enum class AdPlacement(internal val path: String) {
    FONT("android/font"),
    CATEGORY("android/cat"),
    COMIC_DETAIL("android/comic_detail"),
}

/**
 * Stateful, multiplatform PicACG API client.
 *
 * The supplied [HttpClient] owns the platform engine. This class creates configured children but
 * never closes the supplied parent client.
 */
public class PicaComicClient(
    private val httpClient: HttpClient,
    public val session: PicaSession = InMemoryPicaSession(),
    public val configuration: PicaComicClientConfig = PicaComicClientConfig(),
) {
    public constructor(
        httpClient: HttpClient,
        initialCredential: String,
        identity: PicaIdentity = PicaIdentity.random(),
        configuration: PicaComicClientConfig = PicaComicClientConfig(),
    ) : this(httpClient, InMemoryPicaSession(initialCredential, identity), configuration)

    private val picaHttpClient: HttpClient = httpClient.config {
        expectSuccess = true
        install(ContentNegotiation) { json(configuration.json) }
        install(HttpTimeout) {
            connectTimeoutMillis = 30_000
            socketTimeoutMillis = 30_000
            requestTimeoutMillis = 30_000
        }
        install(PicaRequestPlugin) {
            session = this@PicaComicClient.session
            clientConfig = configuration
        }
    }

    private val recommendationHttpClient: HttpClient = externalServiceClient(15_000)
    private val animeHttpClient: HttpClient = externalServiceClient(30_000)
    private val smartDnsHttpClient: HttpClient = externalServiceClient(5_000)
    private val directHttpClient: HttpClient = directClient(followRedirects = true)
    private val placementHttpClient: HttpClient = directClient(followRedirects = false)

    private val externalServiceHosts: List<String> = listOf("macapi1.com", "macapi2.com", "macapi3.top")
    private val externalHostState: ExternalHostState = ExternalHostState()
    private val recommendationApis: List<RecommendationApi> = externalServiceHosts.map { host ->
        ktorfit("https://$host/", recommendationHttpClient).createRecommendationApi()
    }
    private val animeApis: List<AnimeApi> = externalServiceHosts.map { host ->
        ktorfit("https://$host/maccms/json/baofeng/", animeHttpClient).createAnimeApi()
    }
    private val smartDnsApis: List<SmartDnsApi> = externalServiceHosts.map { host ->
        ktorfit("https://$host/", smartDnsHttpClient).createSmartDnsApi()
    }

    /** Raw signed PicA service. Calling it does not apply envelope validation or client retries. */
    public val picaApi: PicaApi = ktorfit(picaBaseUrl(), picaHttpClient).createPicaApi()

    /** Raw recommendation service pinned to macapi1. High-level methods add host failover. */
    public val recommendationApi: RecommendationApi
        get() = recommendationApis.first()

    /** Raw anime service pinned to macapi1. High-level methods add host failover. */
    public val animeApi: AnimeApi
        get() = animeApis.first()

    /** Raw Smart DNS service pinned to macapi1. High-level methods add host failover. */
    public val smartDnsApi: SmartDnsApi
        get() = smartDnsApis.first()

    public suspend fun credential(): String? = session.credential()

    public suspend fun updateCredential(credential: String?): Unit = session.updateCredential(credential)

    public suspend fun signOut(): Unit = session.updateCredential(null)

    public suspend fun signIn(email: String, password: String): LoginData {
        require(email.isNotEmpty()) { "email must not be empty" }
        require(password.isNotEmpty()) { "password must not be empty" }
        val firstRequest = com.hiczp.picacomic.api.model.SignInRequest(email, password)
        val result = try {
            picaData(retry = false) { picaApi.signIn(firstRequest) }
        } catch (exception: PicaException) {
            if (!email.contains('@') || !isLegacyLoginCandidate(exception)) throw exception
            picaData(retry = false) {
                picaApi.signIn(firstRequest.copy(email = email.replace("@", "")))
            }
        }
        session.updateCredential(result.token)
        return result
    }

    public suspend fun register(request: RegisterRequest): Unit {
        val normalized = normalizeRegisterRequest(request)
        picaUnit { picaApi.register(normalized) }
    }

    public suspend fun getProfile(): UserProfileData = picaData { picaApi.getProfile() }

    public suspend fun updateSlogan(slogan: String): UserProfileData {
        val normalized = slogan.trim()
        require(normalized.isNotEmpty()) { "slogan must not be empty" }
        require(normalized.length <= 350) { "slogan must not exceed 350 UTF-16 code units" }
        picaUnit { picaApi.updateSlogan(SloganUpdateRequest(normalized)) }
        return getProfile()
    }

    public suspend fun updateAvatar(avatar: String): UserProfileData {
        require(avatar.startsWith("data:image/jpeg;base64,")) { "avatar must be a JPEG data URL" }
        picaUnit { picaApi.updateAvatar(AvatarUpdateRequest(avatar)) }
        return getProfile()
    }

    public suspend fun getFavourites(
        page: Int = 1,
        sort: ComicSort = ComicSort.NEWEST,
    ): ComicsListResponse {
        requirePage(page)
        return picaData { picaApi.getFavourites(page, sort.value) }
    }

    public suspend fun getMyComments(page: Int = 1): MyCommentsResponseData {
        requirePage(page)
        return picaData { picaApi.getMyComments(page) }
    }

    public suspend fun punchIn(): PunchInResponse = picaData(retry = false) {
        picaApi.punchIn(emptyMap())
    }

    public suspend fun getCategories(): CategoriesData {
        val data: CategoriesData = picaData { picaApi.getCategories() }
        val dynamicSupportUrl = joinUrl(configuration.adBaseUrl, AdPlacement.FONT.path)
        return data.copy(
            categories = data.categories.map { category ->
                if (category.link == "{{DYNAMIC_SUPPORT_URL}}") {
                    category.copy(link = dynamicSupportUrl)
                } else {
                    category
                }
            },
        )
    }

    public suspend fun getKeywords(): KeywordsData = picaData { picaApi.getKeywords() }

    public suspend fun getBanners(): BannersResponse = picaData { picaApi.getBanners() }

    public suspend fun getAnnouncements(page: Int = 1): AnnouncementsResponse {
        requirePage(page)
        return picaData { picaApi.getAnnouncements(page, "android") }
    }

    public suspend fun getCollections(): CollectionsResponseData = picaData { picaApi.getCollections() }

    public suspend fun getComics(
        page: Int = 1,
        category: String? = null,
        tag: String? = null,
        author: String? = null,
        creatorId: String? = null,
        chineseTeam: String? = null,
        sort: String = ComicSort.NEWEST.value,
        customQueryParameters: Map<String, String> = emptyMap(),
    ): ComicsListResponse {
        requirePage(page)
        val queryParameters = linkedMapOf<String, String>()
        category?.let { queryParameters["c"] = it }
        tag?.let { queryParameters["t"] = it }
        author?.let { queryParameters["a"] = it }
        creatorId?.let { queryParameters["ca"] = it }
        chineseTeam?.let { queryParameters["ct"] = it }
        queryParameters["s"] = sort
        queryParameters.putAll(customQueryParameters)
        queryParameters["page"] = page.toString()
        return picaData { picaApi.getComics(queryParameters) }
    }

    public suspend fun advancedSearch(
        page: Int = 1,
        keyword: String? = null,
        sort: ComicSort = ComicSort.NEWEST,
        categories: List<String>? = null,
        category: String? = null,
    ): ComicsListResponse {
        requirePage(page)
        val selectedCategories = categories?.takeIf { it.isNotEmpty() } ?: category?.let(::listOf)
        return picaData {
            picaApi.advancedSearch(page, AdvancedSearchBody(keyword.orEmpty(), sort.value, selectedCategories))
        }
    }

    public suspend fun getRandomComics(): RandomComicsResponse = picaData {
        picaApi.getRandomComics(NoCacheRequest)
    }

    public suspend fun getLeaderboard(
        timeframe: LeaderboardTimeframe,
        category: String = "VC",
    ): LeaderboardData = picaData { picaApi.getLeaderboard(timeframe.value, category) }

    public suspend fun getComicDetail(comicId: String): ComicDetailData =
        picaData { picaApi.getComicDetail(comicId) }

    public suspend fun toggleFavourite(comicId: String): Unit =
        picaUnit { picaApi.toggleFavourite(comicId, emptyMap()) }

    public suspend fun toggleComicLike(comicId: String): Unit =
        picaUnit { picaApi.toggleComicLike(comicId, emptyMap()) }

    public suspend fun getEpisodes(comicId: String, page: Int = 1): EpisodesData {
        requirePage(page)
        return picaData { picaApi.getEpisodes(comicId, page) }
    }

    public suspend fun getChapterPages(comicId: String, chapterOrder: Int, page: Int = 1): ChapterPagesData {
        requirePage(page)
        return picaData(retry = false) { picaApi.getChapterPages(comicId, chapterOrder.toString(), page) }
    }

    public suspend fun getComicComments(comicId: String, page: Int = 1): CommentResponseData {
        requirePage(page)
        return picaData { picaApi.getComicComments(comicId, page) }
    }

    public suspend fun postComicComment(comicId: String, content: String): Unit =
        picaUnit { picaApi.postComicComment(comicId, contentBody(content)) }

    public suspend fun getReplies(commentId: String, page: Int = 1): CommentResponseData {
        requirePage(page)
        return picaData { picaApi.getReplies(commentId, page) }
    }

    public suspend fun replyComment(commentId: String, content: String): Unit =
        picaUnit { picaApi.replyComment(commentId, contentBody(content)) }

    public suspend fun toggleCommentLike(commentId: String): CommentLikeResponse =
        picaData(retry = false) { picaApi.toggleCommentLike(commentId, emptyMap()) }

    public suspend fun reportComment(commentId: String): Unit =
        picaUnit { picaApi.reportComment(commentId, emptyMap()) }

    public suspend fun getGames(page: Int = 1): GamesResponse {
        requirePage(page)
        return picaData { picaApi.getGames(page) }
    }

    public suspend fun getGameDetail(gameId: String): GameDetailResponse =
        picaData { picaApi.getGameDetail(gameId) }

    public suspend fun getGameComments(gameId: String, page: Int = 1): GameCommentResponseData {
        requirePage(page)
        return picaData { picaApi.getGameComments(gameId, page) }
    }

    public suspend fun postGameComment(gameId: String, content: String): Unit =
        picaUnit { picaApi.postGameComment(gameId, contentBody(content)) }

    public suspend fun toggleGameLike(gameId: String): GameLikeResponse =
        picaData(retry = false) { picaApi.toggleGameLike(gameId, emptyMap()) }

    public suspend fun trackRecommendation(fromComicId: String, toComicId: String): Unit {
        require(fromComicId.isNotEmpty() && toComicId.isNotEmpty()) { "comic ids must not be empty" }
        callExternalService(recommendationApis) { service ->
            service.track(RecommendationTrackingRequest(fromComicId, toComicId))
        }
    }

    public suspend fun recommend(comicId: String, limit: Int = 10): List<String> {
        val response = callExternalService(recommendationApis) { service -> service.recommend(comicId, limit) }
        return if (response.code == 1) response.recommendations else emptyList()
    }

    public suspend fun getAnimeCategories(): List<AnimeCategory> {
        val response = requireAnimeSuccess(callExternalService(animeApis) { it.getCategories("list") })
        return response.categories ?: throw ExternalApiException(response.code, "Anime response omitted categories")
    }

    public suspend fun getAnimeCategory(categoryId: Int, page: Int = 1): AnimePage {
        require(categoryId != 0) { "categoryId must not be zero" }
        requirePage(page)
        return requireAnimeSuccess(
            callExternalService(animeApis) { it.getCategoryPage("list", categoryId, page) },
        ).toAnimePage()
    }

    public suspend fun queryAnime(
        page: Int = 1,
        categoryId: Int? = null,
        keyword: String? = null,
        year: String? = null,
        random: Boolean = false,
    ): AnimePage {
        requirePage(page)
        val response = callExternalService(animeApis) { service ->
            service.query(
                action = "list",
                categoryId = categoryId,
                page = page,
                keyword = keyword,
                year = year,
                orderBy = if (year != null) "time" else null,
                sort = if (keyword != null || year != null || random) "desc" else null,
                random = if (random) "1" else null,
            )
        }
        return requireAnimeSuccess(response).toAnimePage()
    }

    public suspend fun getAnimeDetail(animeId: Int): AnimeEntry {
        val response = requireAnimeSuccess(callExternalService(animeApis) { it.getDetail("videolist", animeId) })
        return response.anime.firstOrNull()
            ?: throw ExternalApiException(response.code, "Anime detail response contained no item")
    }

    public suspend fun resolveSmartDns(domain: String): SmartDnsData {
        val response = callExternalService(smartDnsApis) { it.resolve(domain) }
        if (response.code != 200 || response.data == null) {
            throw ExternalApiException(response.code, response.message.ifEmpty { "Smart DNS request failed" })
        }
        return response.data
    }

    public suspend fun resolveSmartDnsLines(domain: String): Map<String, List<String>> =
        resolveSmartDns(domain).lines.mapNotNull { (key, line) ->
            val pollutedAddresses = line.pollutedAddresses.toSet()
            val filteredAddresses = line.addresses.filterNot(pollutedAddresses::contains)
            if (line.status == "ok" && filteredAddresses.isNotEmpty()) key to filteredAddresses else null
        }.toMap()

    public suspend fun getInitConfig(persisted: InitConfigResponse? = null): InitConfigResponse {
        repeat(2) { attempt ->
            if (attempt > 0) delayDirect()
            try {
                val response = directHttpClient.get("http://68.183.234.72/init")
                if (!response.status.isSuccess()) throw DirectApiException("Init returned HTTP ${response.status.value}")
                val parsed = response.body<InitConfigResponse>()
                if (parsed.status != "ok") throw DirectApiException("Init status was ${parsed.status}")
                return parsed.copy(addresses = parsed.addresses.orEmpty())
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Throwable) {
            }
        }
        if (persisted != null && persisted.adBaseUrl != PicaConstants.DEFAULT_AD_BASE_URL) {
            return persisted.copy(addresses = persisted.addresses.orEmpty())
        }
        return InitConfigResponse(
            status = "ok",
            addresses = emptyList(),
            adBaseUrl = PicaConstants.DEFAULT_AD_BASE_URL,
            adKeyword = "diwodiwo",
        )
    }

    public suspend fun checkVersion(versionCode: Long = configuration.versionCode): VersionCheckData {
        val response = directHttpClient.get("https://picapp.picappns.com/app/picacomic/version/check") {
            parameter("versionCode", versionCode)
            parameter("platform", "android")
        }
        if (!response.status.isSuccess()) {
            throw DirectApiException("Version check returned HTTP ${response.status.value}")
        }
        val envelope = response.body<VersionCheckResponse>()
        val data = envelope.data
        if (envelope.code != 200 || data == null) {
            throw DirectApiException(envelope.message.ifEmpty { "Version check failed" })
        }
        fun normalize(url: String): String =
            if (url.startsWith("http")) url else "https://picapp.picappns.com$url"
        return data.copy(
            downloadUrl = normalize(data.downloadUrl),
            fallbackUrls = data.fallbackUrls.map(::normalize),
        )
    }

    public suspend fun getAdZones(
        placement: AdPlacement,
        adBaseUrl: String = configuration.adBaseUrl,
    ): List<AdZone>? {
        repeat(3) { attempt ->
            if (attempt > 0) delayDirect()
            discoverAdZones(joinUrl(adBaseUrl, placement.path), adBaseUrl)?.let { return it }
        }
        return null
    }

    public suspend fun getBannerAdZones(
        bannerLink: String,
        adBaseUrl: String = configuration.adBaseUrl,
    ): List<AdZone>? {
        if (bannerLink.isBlank()) return null
        return discoverAdZones(bannerLink, adBaseUrl)
    }

    public suspend fun getAdZonesByToken(
        zoneToken: String,
        adBaseUrl: String = configuration.adBaseUrl,
    ): List<AdZone>? {
        val url = joinUrl(adBaseUrl, "get-ad-zones") + "?z=" + zoneToken
        return try {
            val response = directHttpClient.get(url)
            if (!response.status.isSuccess()) return null
            val parsed = response.body<AdZonesResponse>()
            parsed.data.zones.takeIf { parsed.code == 200 && it.isNotEmpty() }
        } catch (exception: CancellationException) {
            throw exception
        } catch (_: Throwable) {
            null
        }
    }

    public fun close(): Unit {
        picaHttpClient.close()
        recommendationHttpClient.close()
        animeHttpClient.close()
        smartDnsHttpClient.close()
        directHttpClient.close()
        placementHttpClient.close()
    }

    private suspend fun discoverAdZones(placementUrl: String, adBaseUrl: String): List<AdZone>? = try {
        val response = placementHttpClient.get(placementUrl)
        if (response.status != HttpStatusCode.MovedPermanently && response.status != HttpStatusCode.Found) {
            null
        } else {
            val location = response.headers[HttpHeaders.Location] ?: return null
            val token = parseZones(location, response.call.request.url.toString()) ?: return null
            getAdZonesByToken(token, adBaseUrl)
        }
    } catch (exception: CancellationException) {
        throw exception
    } catch (_: Throwable) {
        null
    }

    private fun parseZones(location: String, requestUrl: String): String? {
        val base = runCatching { Url(requestUrl) }.getOrNull() ?: return null
        val port = if (base.port == base.protocol.defaultPort) "" else ":${base.port}"
        val origin = "${base.protocol.name}://${base.host}$port"
        val absolute = when {
            location.startsWith("http://") || location.startsWith("https://") -> location
            location.startsWith("//") -> "${base.protocol.name}:$location"
            location.startsWith('/') -> origin + location
            location.startsWith('?') -> origin + base.encodedPath + location
            else -> {
                val directory = base.encodedPath.substringBeforeLast('/', missingDelimiterValue = "")
                origin + directory + "/" + location
            }
        }
        return runCatching { Url(absolute).parameters["zones"] }
            .getOrNull()
            ?.takeIf { it.isNotBlank() }
    }

    private suspend fun <T : Any> picaData(
        retry: Boolean = true,
        call: suspend () -> PicaResponse<T>,
    ): T = picaExecute(retry) {
        val envelope = call()
        validateEnvelope(envelope)
        envelope.data ?: throw PicaMissingDataException("PicA response data was null")
    }

    private suspend fun picaUnit(call: suspend () -> PicaResponse<JsonElement>): Unit =
        picaExecute(retry = false) {
            validateEnvelope(call())
        }

    private suspend fun <T> picaExecute(retry: Boolean, block: suspend () -> T): T {
        val delays = if (retry) configuration.retryDelaysMilliseconds else emptyList()
        var attempt = 0
        while (true) {
            try {
                return block()
            } catch (exception: ResponseException) {
                val mapped = mapPicaHttpException(exception)
                if (mapped !is PicaSignatureException || attempt >= delays.size) throw mapped
                delayFor(delays[attempt++])
            } catch (exception: PicaSignatureException) {
                if (attempt >= delays.size) throw exception
                delayFor(delays[attempt++])
            } catch (exception: HttpRequestTimeoutException) {
                if (attempt >= delays.size) throw exception
                delayFor(delays[attempt++])
            } catch (exception: IOException) {
                if (attempt >= delays.size) throw exception
                delayFor(delays[attempt++])
            }
        }
    }

    private fun <T> validateEnvelope(envelope: PicaResponse<T>): Unit {
        if (envelope.error == "1023") {
            throw PicaSignatureException(envelope.message, envelope.code)
        }
        if (envelope.message != "success" && envelope.code != 200) {
            throw PicaApiException(envelope.message, envelope.code, envelope.error)
        }
    }

    private suspend fun mapPicaHttpException(exception: ResponseException): PicaException {
        val status = exception.response.status.value
        val body = runCatching { exception.response.bodyAsText() }.getOrNull()
        val fields = parseErrorFields(body)
        val message = fields.first ?: "HTTP error $status"
        val error = fields.second
        return when {
            status == 401 -> {
                session.updateCredential(null)
                PicaUnauthorizedException(message, error, exception)
            }

            status == 404 -> PicaNotFoundException(message, error, exception)
            status >= 500 -> PicaServerException(message, status, error, exception)
            error == "1023" -> PicaSignatureException(message, status = status, cause = exception)
            else -> PicaApiException(message, error = error, status = status, cause = exception)
        }
    }

    private fun parseErrorFields(body: String?): Pair<String?, String?> {
        if (body.isNullOrBlank()) return null to null
        return try {
            val objectValue = configuration.json.parseToJsonElement(body).jsonObject
            val message = (objectValue["message"] as? JsonPrimitive)?.contentOrNull
            val error = (objectValue["error"] as? JsonPrimitive)?.contentOrNull
            message to error
        } catch (_: SerializationException) {
            null to null
        } catch (_: IllegalArgumentException) {
            null to null
        }
    }

    private suspend fun <A, T> callExternalService(services: List<A>, serviceCall: suspend (A) -> T): T {
        val start = externalHostState.snapshot()
        var lastIoFailure: Throwable? = null
        repeat(services.size) { offset ->
            val index = (start + offset) % services.size
            try {
                val result = serviceCall(services[index])
                externalHostState.select(index)
                return result
            } catch (exception: ServerResponseException) {
                // HTTP >= 500 tries the next host.
            } catch (exception: ClientRequestException) {
                externalHostState.select(index)
                throw exception
            } catch (exception: ResponseException) {
                if (exception.response.status.value >= 500) {
                    // Try the next host.
                } else {
                    externalHostState.select(index)
                    throw exception
                }
            } catch (exception: HttpRequestTimeoutException) {
                lastIoFailure = exception
            } catch (exception: IOException) {
                lastIoFailure = exception
            }
        }
        lastIoFailure?.let { throw it }
        throw ExternalHostExhaustedException()
    }

    private fun requireAnimeSuccess(response: AnimeCatalogResponse): AnimeCatalogResponse {
        if (response.code != 1) throw ExternalApiException(response.code, response.message)
        return response
    }

    private fun AnimeCatalogResponse.toAnimePage(): AnimePage = AnimePage(
        items = anime,
        page = page,
        pages = pageCount,
        limit = limit.toIntOrNull() ?: 20,
        total = total,
    )

    private fun picaBaseUrl(): String = "https://${configuration.requestMode.apiHost}/"

    private fun externalServiceClient(timeoutMilliseconds: Long): HttpClient = httpClient.config {
        expectSuccess = true
        install(ContentNegotiation) { json(configuration.json) }
        install(RetrofitEncodingPlugin)
        install(HttpTimeout) {
            connectTimeoutMillis = timeoutMilliseconds
            socketTimeoutMillis = timeoutMilliseconds
            requestTimeoutMillis = timeoutMilliseconds
        }
    }

    private fun directClient(followRedirects: Boolean): HttpClient = httpClient.config {
        expectSuccess = false
        this.followRedirects = followRedirects
        install(ContentNegotiation) { json(configuration.json) }
        install(HttpTimeout) {
            connectTimeoutMillis = 10_000
            socketTimeoutMillis = 10_000
            requestTimeoutMillis = 10_000
        }
    }

    private fun ktorfit(baseUrl: String, client: HttpClient): Ktorfit = Ktorfit.Builder()
        .baseUrl(baseUrl)
        .httpClient(client)
        .build()

    private suspend fun delayFor(milliseconds: Long): Unit {
        if (milliseconds > 0) configuration.delayProvider.delay(milliseconds)
    }

    private suspend fun delayDirect(): Unit = delayFor(configuration.directRetryDelayMilliseconds)

    private fun normalizeRegisterRequest(request: RegisterRequest): RegisterRequest {
        val normalized = request.copy(
            name = request.name.trim(),
            email = request.email.trim(),
            question1 = request.question1.trim(),
            answer1 = request.answer1.trim(),
            question2 = request.question2.trim(),
            answer2 = request.answer2.trim(),
            question3 = request.question3.trim(),
            answer3 = request.answer3.trim(),
        )
        require(normalized.name.isNotEmpty() && normalized.name.length <= 50) { "name must contain 1..50 characters" }
        require(normalized.email.isNotEmpty() && normalized.email.all { it.isLetterOrDigit() && it.code < 128 }) {
            "email must be an ASCII alphanumeric account name"
        }
        require(normalized.password.length >= 8) { "password must contain at least 8 characters" }
        require(
            listOf(
                normalized.question1,
                normalized.answer1,
                normalized.question2,
                normalized.answer2,
                normalized.question3,
                normalized.answer3,
            ).all(String::isNotEmpty),
        ) { "security questions and answers must not be empty" }
        require(Regex("\\d{4}-\\d{2}-\\d{2}").matches(normalized.birthday)) { "birthday must use yyyy-MM-dd" }
        require(normalized.gender in setOf("m", "f", "bot")) { "gender must be m, f, or bot" }
        return normalized
    }

    private fun contentBody(content: String): ContentBody {
        require(content.isNotBlank()) { "content must not be blank" }
        require(content.length <= 500) { "content must not exceed 500 UTF-16 code units" }
        return ContentBody(content)
    }

    private fun requirePage(page: Int): Unit = require(page >= 1) { "page must be at least 1" }

    private fun isLegacyLoginCandidate(exception: PicaException): Boolean {
        if (exception is PicaUnauthorizedException) return true
        if (exception !is PicaApiException) return false
        val lowerMessage = exception.message.orEmpty().lowercase()
        return exception.error == "1004" ||
                "invalid email or password" in lowerMessage ||
                "email or password" in lowerMessage
    }

    private fun joinUrl(baseUrl: String, child: String): String =
        baseUrl.trimEnd('/') + "/" + child.trimStart('/')

    private class ExternalHostState {
        private val mutex = Mutex()
        private var index: Int = 0

        suspend fun snapshot(): Int = mutex.withLock { index }

        suspend fun select(value: Int): Unit = mutex.withLock {
            index = value
        }
    }
}

public fun AnimeEntry.playSources(): List<PlaySource> {
    val sourceNames = playSource.orEmpty().split("$$$")
    val sourceValues = playUrl.orEmpty().split("$$$")
    return sourceValues.mapIndexed { index, encodedEpisodes ->
        val episodes = encodedEpisodes.split('#').mapNotNull { encodedEpisode ->
            if (encodedEpisode.isBlank()) return@mapNotNull null
            val separator = encodedEpisode.indexOf('$')
            val name = if (separator >= 0) encodedEpisode.substring(0, separator) else encodedEpisode
            var url = if (separator >= 0) encodedEpisode.substring(separator + 1) else ""
            if (url.startsWith("//")) url = "https:$url"
            PlayEpisode(name, url)
        }
        PlaySource(sourceNames.getOrElse(index) { "" }, episodes)
    }
}

public fun VersionCheckData.isForceUpdate(currentVersionCode: Long): Boolean =
    forceUpdate || currentVersionCode < forceMinVersionCode

public fun VersionCheckData.shouldShow(currentVersionCode: Long, dismissed: Boolean): Boolean =
    hasUpdate && (!dismissed || isForceUpdate(currentVersionCode))

private fun io.ktor.http.HttpStatusCode.isSuccess(): Boolean = value in 200..299
