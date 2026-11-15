package com.hiczp.picacomic.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PicaComicClientTest {
    private val jsonHeaders = headersOf(HttpHeaders.ContentType, "application/json")
    private val identity = PicaIdentity(
        nonce = "0123456789abcdefghijklmnopqrstuv",
        applicationUuid = "0123456789abcdef0123456789abcdef",
    )

    @Test
    fun queryEncodingAndSigningUseFinalOrderedUrl(): Unit {
        var encodedQuery = ""
        var signature = ""
        var acceptValues: List<String> = emptyList()
        var capturedHeaders: Map<String, String> = emptyMap()
        val engine = MockEngine { request ->
            encodedQuery = request.url.encodedQuery
            signature = request.headers["signature"].orEmpty()
            acceptValues = request.headers.getAll(HttpHeaders.Accept).orEmpty()
            capturedHeaders = request.headers.entries().associate { it.key to it.value.single() }
            respond(
                """{"code":200,"message":"success","data":{"comics":{"docs":[],"total":0,"page":1,"pages":0}}}""",
                headers = jsonHeaders,
            )
        }
        val client = client(engine)
        val params = linkedMapOf(
            "s" to "dd",
            "space key" to "a b",
            "plus" to "+",
            "percent" to "%",
            "slash" to "/",
            "unicode" to "哔咔",
            "empty" to "",
            "page" to "1",
        )

        runTest { client.picaApi.getComics(params) }

        assertEquals(
            "s=dd&space%20key=a%20b&plus=%2B&percent=%25&slash=%2F&" +
                    "unicode=%E5%93%94%E5%92%94&empty=&page=1",
            encodedQuery,
        )
        assertEquals(
            "0ca103531aa60ee954a3cc02d60e8fbe371f7b06fbf167413d0b51463ba6a9ac",
            signature,
        )
        assertEquals(listOf("application/vnd.picacomic.com.v1+json"), acceptValues)
        assertEquals("1700000000", capturedHeaders["time"])
        assertEquals(identity.nonce, capturedHeaders["nonce"])
        assertEquals(PicaConstants.API_KEY, capturedHeaders["api-key"])
        assertEquals("1", capturedHeaders["app-channel"])
        assertEquals("2.3.1.20241111", capturedHeaders["app-version"])
        assertEquals(identity.applicationUuid, capturedHeaders["app-uuid"])
        assertEquals("android", capturedHeaders["app-platform"])
        assertEquals("20250144", capturedHeaders["app-build-version"])
        assertEquals("okhttp/3.8.1", capturedHeaders[HttpHeaders.UserAgent])
        assertEquals("medium", capturedHeaders["image-quality"])
        client.close()
    }

    @Test
    fun signInStoresCredentialAndSubsequentRequestsUseIt(): Unit {
        val authorizations = mutableListOf<String?>()
        var signInContentType: String? = null
        val engine = MockEngine { request ->
            authorizations += request.headers[HttpHeaders.Authorization]
            when (request.url.encodedPath) {
                "/auth/sign-in" -> {
                    signInContentType = request.headers[HttpHeaders.ContentType]
                    respond(
                        """{"code":200,"message":"success","data":{"token":"new-token"}}""",
                        headers = jsonHeaders,
                    )
                }

                "/users/profile" -> respond(
                    """{"code":200,"message":"success","data":{"user":{"_id":"u","email":"e","name":"n"}}}""",
                    headers = jsonHeaders,
                )

                else -> error("Unexpected path ${request.url.encodedPath}")
            }
        }
        val client = client(engine)

        runTest {
            assertEquals("new-token", client.signIn("account", "password").token)
            assertEquals("new-token", client.credential())
            client.getProfile()
            client.updateCredential("external-token")
            assertEquals("external-token", client.credential())
            client.signOut()
            assertNull(client.credential())
        }

        assertEquals(listOf(null, "new-token"), authorizations)
        assertEquals("application/json; charset=UTF-8", signInContentType)
        client.close()
    }

    @Test
    fun legacyLoginRetryClearsRejectedCredential(): Unit {
        var calls = 0
        val authorizations = mutableListOf<String?>()
        val engine = MockEngine { request ->
            calls++
            authorizations += request.headers[HttpHeaders.Authorization]
            if (calls == 1) {
                respondError(
                    HttpStatusCode.Unauthorized,
                    """{"message":"invalid email or password","error":"1004"}""",
                    jsonHeaders,
                )
            } else {
                respond(
                    """{"code":200,"message":"success","data":{"token":"replacement"}}""",
                    headers = jsonHeaders,
                )
            }
        }
        val session = InMemoryPicaSession("rejected", identity)
        val client = client(engine, session)

        runTest { assertEquals("replacement", client.signIn("a@b", "password").token) }

        assertEquals(2, calls)
        assertEquals(listOf("rejected", null), authorizations)
        client.close()
    }

    @Test
    fun retriesEnvelope1023AndRecreatesRequest(): Unit {
        var calls = 0
        var delays = 0
        val engine = MockEngine {
            calls++
            if (calls == 1) {
                respond(
                    """{"code":200,"message":"success","error":"1023"}""",
                    headers = jsonHeaders,
                )
            } else {
                respond(
                    """{"code":200,"message":"success","data":{"keywords":["ok"]}}""",
                    headers = jsonHeaders,
                )
            }
        }
        val config = config().copy(
            retryDelaysMilliseconds = listOf(1),
            delayProvider = PicaDelayProvider { delays++ },
        )
        val client = PicaComicClient(HttpClient(engine), InMemoryPicaSession(identity = identity), config)

        runTest { assertEquals(listOf("ok"), client.getKeywords().keywords) }
        assertEquals(2, calls)
        assertEquals(1, delays)
        client.close()
    }

    @Test
    fun macApiFailsOverAndAdPlacementRunsD06(): Unit {
        val hosts = mutableListOf<String>()
        val urls = mutableListOf<String>()
        val engine = MockEngine { request ->
            urls += request.url.toString()
            when {
                request.url.host == "macapi1.com" -> {
                    hosts += request.url.host
                    respondError(HttpStatusCode.InternalServerError)
                }

                request.url.host == "macapi2.com" -> {
                    hosts += request.url.host
                    respond(
                        """{"code":1,"recommendations":["comic-2"],"count":1}""",
                        headers = jsonHeaders,
                    )
                }

                request.url.encodedPath == "/android/font" -> respond(
                    "",
                    HttpStatusCode.Found,
                    headersOf(HttpHeaders.Location, "?zones=abc%2Bdef"),
                )

                request.url.encodedPath == "/get-ad-zones" -> respond(
                    """{"code":200,"data":{"zones":[{"id":"1","name":"z","ad":{"image":"a.jpg","title":"A"}}]}}""",
                    headers = jsonHeaders,
                )

                else -> error("Unexpected request ${request.url}")
            }
        }
        val client = client(engine)

        runTest {
            assertEquals(listOf("comic-2"), client.recommend("comic-1"))
            assertEquals("1", client.getAdZones(AdPlacement.FONT, "https://ads.invalid")?.single()?.id)
        }

        assertEquals(listOf("macapi1.com", "macapi2.com"), hosts)
        assertTrue(urls.any { "get-ad-zones?z=abc+def" in it })
        client.close()
    }

    @Test
    fun randomUsesNoCacheAndAnimeQueryUsesRetrofitSpaceEncoding(): Unit {
        var randomCacheControl: String? = null
        var animeQuery: String? = null
        val engine = MockEngine { request ->
            when {
                request.url.encodedPath == "/comics/random" -> {
                    randomCacheControl = request.headers[HttpHeaders.CacheControl]
                    respond(
                        """{"code":200,"message":"success","data":{"comics":[]}}""",
                        headers = jsonHeaders,
                    )
                }

                request.url.host == "macapi1.com" -> {
                    animeQuery = request.url.encodedQuery
                    respond(
                        """{"code":1,"msg":"ok","page":1,"pagecount":1,"limit":"20","total":0,"list":[]}""",
                        headers = jsonHeaders,
                    )
                }

                else -> error("Unexpected request ${request.url}")
            }
        }
        val client = client(engine)

        runTest {
            client.getRandomComics()
            client.queryAnime(keyword = "a b")
        }

        assertEquals("no-cache", randomCacheControl)
        assertEquals("ac=list&pg=1&wd=a%20b&sort=desc", animeQuery)
        client.close()
    }

    @Test
    fun initRetriesAndVersionUrlsAreNormalized(): Unit {
        var initCalls = 0
        val engine = MockEngine { request ->
            when (request.url.encodedPath) {
                "/init" -> {
                    initCalls++
                    if (initCalls == 1) {
                        respondError(HttpStatusCode.InternalServerError)
                    } else {
                        respond(
                            """{"status":"ok","waka":"https://waka.example","addresses":null}""",
                            headers = jsonHeaders,
                        )
                    }
                }

                "/app/picacomic/version/check" -> respond(
                    """{
                        "code":200,
                        "message":"ok",
                        "data":{
                            "hasUpdate":true,
                            "forceUpdate":true,
                            "downloadUrl":"/download.apk",
                            "fallbackUrls":["https://cdn.example/a.apk","fallback.apk"]
                        }
                    }""".trimIndent(),
                    headers = jsonHeaders,
                )

                else -> error("Unexpected request ${request.url}")
            }
        }
        val client = client(engine)

        runTest {
            val init = client.getInitConfig()
            assertEquals("https://waka.example", init.adBaseUrl)
            assertEquals(emptyList(), init.addresses)

            val version = client.checkVersion()
            assertEquals("https://picapp.picappns.com/download.apk", version.downloadUrl)
            assertEquals(
                listOf("https://cdn.example/a.apk", "https://picapp.picappns.comfallback.apk"),
                version.fallbackUrls,
            )
            assertTrue(version.shouldShow(PicaConstants.DEFAULT_VERSION_CODE, dismissed = true))
        }

        assertEquals(2, initCalls)
        client.close()
    }

    @Test
    fun pathParametersEncodeSlashesLikeRetrofit(): Unit {
        val paths = mutableListOf<String>()
        val engine = MockEngine { request ->
            paths += request.url.encodedPath
            if (request.url.host.startsWith("picaapi.")) {
                respond(
                    """{
                        "code":200,
                        "message":"success",
                        "data":{
                            "comic":{
                                "_id":"a/b",
                                "_creator":{"_id":"u","exp":0,"level":1},
                                "thumb":{"path":"thumb.jpg","fileServer":"https://s3.picacomic.com"}
                            }
                        }
                    }""".trimIndent(),
                    headers = jsonHeaders,
                )
            } else {
                respond(
                    """{"code":1,"recommendations":[],"count":0}""",
                    headers = jsonHeaders,
                )
            }
        }
        val client = client(engine)

        runTest {
            client.getComicDetail("a/b")
            client.recommend("x/y")
        }

        assertEquals(listOf("/comics/a%2Fb", "/picacomic/rec/x%2Fy"), paths)
        client.close()
    }

    private fun client(
        engine: MockEngine,
        session: PicaSession = InMemoryPicaSession(identity = identity),
    ): PicaComicClient = PicaComicClient(HttpClient(engine), session, config())

    private fun config(): PicaComicClientConfig = PicaComicClientConfig(
        timeProvider = { 1_700_000_000L },
        retryDelaysMilliseconds = emptyList(),
        directRetryDelayMilliseconds = 0,
    )

}
