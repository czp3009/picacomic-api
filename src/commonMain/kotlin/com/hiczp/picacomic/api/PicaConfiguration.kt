package com.hiczp.picacomic.api

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

public object PicaConstants {
    public const val API_KEY: String = "C69BAF41DA5ABD1FFEDC6D2FEA56B"
    public const val DEFAULT_AD_BASE_URL: String = "https://pica-ad-api.diwodiwo.xyz"
    public const val DEFAULT_VERSION_CODE: Long = 2026052809L
    public const val PICA_BASE_URL: String = "https://picaapi.picacomic.com/"
    public const val RECOMMENDATION_BASE_URL: String = "https://macapi1.com/"
    public const val ANIME_BASE_URL: String = "https://macapi1.com/maccms/json/baofeng/"
    public const val SMART_DNS_BASE_URL: String = "https://macapi1.com/"
}

public enum class RequestMode(public val apiHost: String, public val imageRootDomain: String) {
    OFFICIAL_DIRECT("picaapi.picacomic.com", "picacomic.com"),
    OFFICIAL_ENHANCED("picaapi.picacomic.com", "picacomic.com"),
    PROXY1_DIRECT("picaapi.go2778.com", "go2778.com"),
    PROXY2_DIRECT("picaapi.aabbcc.xyz", "aabbcc.xyz"),
}

public enum class ImageQuality(public val headerValue: String) {
    LOW("low"),
    MEDIUM("medium"),
    HIGH("high"),
    ORIGINAL("original"),
}

public enum class ComicSort(public val value: String) {
    NEWEST("dd"),
    OLDEST("da"),
    MOST_LIKED("ld"),
    MOST_VIEWED("vd"),
}

public enum class LeaderboardTimeframe(public val value: String) {
    LAST_24_HOURS("H24"),
    LAST_7_DAYS("D7"),
    LAST_30_DAYS("D30"),
}

public data class PicaIdentity(
    public val nonce: String,
    public val applicationUuid: String,
) {
    public companion object {
        public fun random(random: Random = Random.Default): PicaIdentity {
            val nonceAlphabet = "0123456789abcdefghijklmnopqrstuvwxyz"
            val hexAlphabet = "0123456789abcdef"
            return PicaIdentity(
                nonce = buildString(32) { repeat(32) { append(nonceAlphabet[random.nextInt(nonceAlphabet.length)]) } },
                applicationUuid = buildString(32) {
                    repeat(32) { append(hexAlphabet[random.nextInt(hexAlphabet.length)]) }
                },
            )
        }
    }
}

/** Mutable authentication state. Implement this interface to persist tokens outside the library. */
public interface PicaSession {
    public val identity: PicaIdentity

    public suspend fun credential(): String?

    public suspend fun updateCredential(credential: String?)
}

public class InMemoryPicaSession(
    initialCredential: String? = null,
    override val identity: PicaIdentity = PicaIdentity.random(),
) : PicaSession {
    private val mutex: Mutex = Mutex()
    private var value: String? = initialCredential

    override suspend fun credential(): String? = mutex.withLock { value }

    override suspend fun updateCredential(credential: String?): Unit = mutex.withLock {
        value = credential
    }
}

public fun interface PicaDelayProvider {
    public suspend fun delay(milliseconds: Long)
}

@OptIn(ExperimentalTime::class)
private fun systemEpochSeconds(): Long = Clock.System.now().epochSeconds

public data class PicaComicClientConfig(
    public val requestMode: RequestMode = RequestMode.OFFICIAL_DIRECT,
    public val imageQuality: ImageQuality = ImageQuality.MEDIUM,
    public val adBaseUrl: String = PicaConstants.DEFAULT_AD_BASE_URL,
    public val versionCode: Long = PicaConstants.DEFAULT_VERSION_CODE,
    public val timeProvider: () -> Long = ::systemEpochSeconds,
    public val retryDelaysMilliseconds: List<Long> = listOf(1_000L, 2_000L, 4_000L),
    public val directRetryDelayMilliseconds: Long = 2_000L,
    public val delayProvider: PicaDelayProvider = PicaDelayProvider { delay(it) },
    public val json: Json = defaultPicaJson(),
)

public fun defaultPicaJson(): Json = Json {
    encodeDefaults = true
    ignoreUnknownKeys = true
    isLenient = true
    explicitNulls = true
    coerceInputValues = false
}
