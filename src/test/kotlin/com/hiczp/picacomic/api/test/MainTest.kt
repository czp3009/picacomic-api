package com.hiczp.picacomic.api.test

import com.github.salomonbrys.kotson.bool
import com.github.salomonbrys.kotson.int
import com.github.salomonbrys.kotson.obj
import com.github.salomonbrys.kotson.string
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.hiczp.picacomic.api.PicaComicClient
import com.hiczp.picacomic.api.service.PredefinedCategory
import com.hiczp.picacomic.api.service.Thumbnail
import com.hiczp.picacomic.api.service.auth.model.RegisterRequest
import com.hiczp.picacomic.api.service.user.model.Gender
import io.ktor.client.engine.apache.Apache
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.io.jvm.javaio.copyTo
import kotlinx.coroutines.io.readRemaining
import kotlinx.io.core.readBytes
import org.apache.http.HttpHost
import org.junit.jupiter.api.*
import java.io.FileNotFoundException
import java.nio.file.Path

private fun Any.println() = println(this)

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MainTest {
    private lateinit var config: JsonObject
    private lateinit var picaComicClient: PicaComicClient<*>

    @BeforeAll
    fun init() {
        config = MainTest::class.java.getResourceAsStream("/config.json")?.let {
            JsonParser.parseReader(it.reader())
        }?.obj ?: throw FileNotFoundException("Rename '_config.json' to 'config.json' before start test")

        picaComicClient = PicaComicClient(Apache) {
            engine {
                //set proxy
                if (config["useProxy"].bool) {
                    customizeClient {
                        setProxy(HttpHost(config["httpProxyHost"].string, config["httpProxyPort"].int))
                    }
                }
            }
        }
        config["token"]?.string?.takeIf { it.isNotEmpty() }?.also {
            picaComicClient.token = it
        }
    }

    @Test
    fun initTest() {
        runBlocking {
            picaComicClient.init.init().println()
        }
    }

    @Disabled
    @Test
    fun register() {
        runBlocking {
            picaComicClient.auth.register(
                RegisterRequest(
                    "onlyForTest",
                    "onlyForTest",
                    "onlyForTest",
                    "test1",
                    "test1",
                    "test2",
                    "test2",
                    "test3",
                    "test3",
                    "1995-11-14",
                    Gender.BOT
                )
            ).println()
        }
    }

    @Disabled
    @Test
    fun signIn() {
        runBlocking {
            picaComicClient.signIn(config["email"].string, config["password"].string).println()
        }
    }

    @Test
    fun getNotification() {
        runBlocking {
            picaComicClient.user.getNotification().println()
        }
    }

    @Test
    fun getProfile() {
        runBlocking {
            picaComicClient.user.getProfile().println()
        }
    }

    @Test
    fun getOthersProfile() {
        runBlocking {
            picaComicClient.user.getProfile("5bd0256ea5860d7c1aa49d4b").println()
        }
    }

    @Disabled
    @Test
    fun dirty() {
        runBlocking {
            picaComicClient.user.dirty("5bd0256ea5860d7c1aa49d4b").println()
        }
    }

    @Test
    fun getMyComments() {
        runBlocking {
            picaComicClient.user.getMyComments().println()
        }
    }

    @Test
    fun getFavorite() {
        runBlocking {
            picaComicClient.user.getFavourite().println()
        }
    }

    @Disabled
    @Test
    fun punchIn() {
        runBlocking {
            picaComicClient.user.punchIn().println()
        }
    }

    @Test
    fun category() {
        runBlocking {
            picaComicClient.category.get().println()
        }
    }

    @Disabled
    @Test
    fun downloadFile() {
        runBlocking {
            picaComicClient.downloadFile(
                Thumbnail("https://storage1.picacomic.com", "艦隊收藏.jpg", "1ed52b9e-8ac3-47ae-bafc-c31bfab9b3d5.jpg")
            ).readRemaining().readBytes().contentToString().println()
        }
    }

    @Test
    fun search() {
        runBlocking {
            picaComicClient.comic.search(category = PredefinedCategory.全彩.name).println()
        }
    }

    @Test
    fun advancedSearch() {
        runBlocking {
            picaComicClient.comic.advancedSearch("罪与罚").println()
        }
    }

    @Test
    fun getKeywords() {
        runBlocking {
            picaComicClient.keyword.get().println()
        }
    }

    @Test
    fun getComicDetail() {
        runBlocking {
            picaComicClient.comic.getDetail("5822a6e3ad7ede654696e482").println()
        }
    }

    @Test
    fun getEpisode() {
        runBlocking {
            picaComicClient.comic.getEpisodes("5822a6e3ad7ede654696e482").println()
        }
    }

    @Test
    fun getAllEpisode() {
        runBlocking {
            picaComicClient.comic.getAllEpisodes("5d8a243a0d42090218c4af2b")
                .sortedBy { it.order }
                .println()
        }
    }

    @Test
    fun getComments() {
        runBlocking {
            picaComicClient.comic.getComments("5a99fc21ea8c1023dd61d1b0").println()
        }
    }

    @Test
    fun getComicPages() {
        runBlocking {
            picaComicClient.comic.getPages("5a99fc21ea8c1023dd61d1b0", 1).println()
        }
    }

    @Test
    fun getAllComicPages() {
        runBlocking {
            picaComicClient.comic.getAllPages("5da89d903510b43fb6c548d4", 1)
                .joinToString(separator = "\n") { it.media.urlString }
                .println()
        }
    }

    @Disabled
    @Test
    fun getAllComments() {
        runBlocking {
            picaComicClient.comic.getAllComments("5da89d903510b43fb6c548d4").println()
        }
    }

    @Disabled
    @Test
    fun like() {
        runBlocking {
            picaComicClient.comic.like("5da89d903510b43fb6c548d4").println()
        }
    }

    //this test will take long time
    @Disabled
    @UseExperimental(ExperimentalCoroutinesApi::class)
    @Test
    fun downloadTest() {
        runBlocking {
            val requestPerSecond = 20L
            val rateLimiter = produce {
                while (true) {
                    send(Unit)
                    delay(1_000 / requestPerSecond)
                }
            }

            suspend fun <T> (suspend () -> T).withRetry(): T {
                var result: Result<T>
                while (true) {
                    rateLimiter.receive()
                    result = runCatching { this() }
                    if (result.isSuccess) break
                }
                return result.getOrNull()!!
            }

            //big comic
            val comicId = "5da89d903510b43fb6c548d4"
            //small comic
            //val comicId = "5c0bca4e5a84c7393ef6030e"

            val titleDeferred = async { picaComicClient.comic.getDetail(comicId).data.title }
            val episodesDeferred = async { picaComicClient.comic.getAllEpisodes(comicId) }
            val comicPath = Path.of("./download", titleDeferred.await()).also { path ->
                val file = path.toFile()
                if (file.exists()) {
                    file.delete()
                }
                file.mkdirs()
            }
            val log = StringBuffer()
            episodesDeferred.await().map { episode ->
                val order = episode.order!!
                val episodePath = comicPath.resolve(order.toString()).also { it.toFile().mkdir() }
                async {
                    suspend { picaComicClient.comic.getAllPages(comicId, order) }
                        .withRetry()
                        .map { it.media }
                        .map {
                            async {
                                val imageFile = episodePath.resolve(it.originalName).toFile()
                                println("Start download ${it.originalName} in Episode $episode")
                                suspend { picaComicClient.downloadFile(it).copyTo(imageFile.outputStream()) }
                                    .withRetry()
                                println("${it.originalName} in Episode $episode downloaded")
                            }
                        }.also {
                            log.appendln("Episode $episode finished")
                        }
                }
            }.awaitAll().forEach { it.awaitAll() }
            println(log)
            rateLimiter.cancel()
        }
    }

    @AfterAll
    fun dispose() {
        picaComicClient.close()
    }
}
