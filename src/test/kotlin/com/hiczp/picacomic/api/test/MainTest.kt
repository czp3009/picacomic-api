package com.hiczp.picacomic.api.test

import com.github.salomonbrys.kotson.*
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.hiczp.picacomic.api.PicaComicClient
import com.hiczp.picacomic.api.service.PredefinedCategory
import com.hiczp.picacomic.api.service.Thumbnail
import com.hiczp.picacomic.api.service.auth.model.RegisterRequest
import com.hiczp.picacomic.api.service.user.model.Gender
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.logging.LogLevel
import io.ktor.util.KtorExperimentalAPI
import io.ktor.util.cio.writeChannel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.io.copyAndClose
import kotlinx.coroutines.io.readRemaining
import kotlinx.io.core.readBytes
import org.apache.http.HttpHost
import org.junit.jupiter.api.*
import java.io.FileNotFoundException
import java.nio.file.Path
import kotlin.coroutines.coroutineContext

private fun Any.println() = println(this)

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MainTest {
    private lateinit var config: JsonObject
    private lateinit var picaComicClientBuilder: (LogLevel) -> PicaComicClient<*>
    private lateinit var picaComicClient: PicaComicClient<*>

    @BeforeAll
    fun init() {
        config = MainTest::class.java.getResourceAsStream("/config.json")?.let {
            JsonParser.parseReader(it.reader())
        }?.obj ?: throw FileNotFoundException("Rename '_config.json' to 'config.json' before start test")

        picaComicClientBuilder = { logLevel ->
            PicaComicClient(Apache, logLevel) {
                engine {
                    //set proxy
                    if (config["useProxy"].bool) {
                        customizeClient {
                            setProxy(HttpHost(config["httpProxyHost"].string, config["httpProxyPort"].int))
                        }
                    }
                    connectTimeout = 100_000
                    socketTimeout = 100_000
                }
            }.apply {
                config["token"].nullString?.takeIf { it.isNotEmpty() }?.also {
                    token = it
                }
            }
        }
        picaComicClient = picaComicClientBuilder(LogLevel.ALL)
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
    @UseExperimental(ObsoleteCoroutinesApi::class, KtorExperimentalAPI::class)
    @Test
    fun downloadTest() {
        val picaComicClient = picaComicClientBuilder(LogLevel.NONE)

        fun Path.deleteBeforeMkdirs() = this.also {
            val file = toFile()
            if (file.exists()) {
                file.deleteRecursively()
            }
            file.mkdirs()
        }

        runBlocking {
            //big comic
            //val comicId = "5da89d903510b43fb6c548d4"
            //small comic
            val comicId = "5c0bca4e5a84c7393ef6030e"

            val rateLimiter = rateLimiter(20)
            val titleDeferred = async { picaComicClient.comic.getDetail(comicId).data.title }
            val deferredEpisodes = picaComicClient.comic.getAllEpisodesAsync(comicId)
            val comicPath = Path.of("./download", titleDeferred.await()).deleteBeforeMkdirs()

            open class Message
            data class Init(val title: String, val total: Int) : Message()
            data class Increase(val title: String) : Message()

            val counter = actor<Message> {
                val total = HashMap<String, Int>()
                val data = mutableMapOf<String, Int>()
                for (message in channel) {
                    if (message is Init) {
                        total[message.title] = message.total
                        data[message.title] = 0
                    } else if (message is Increase) {
                        data[message.title] = data[message.title]!! + 1
                    }
                    data.forEach { (title, count) ->
                        val totalCount = total[title]!!
                        println("$title <${"=".repeat(count)}${" ".repeat(totalCount - count)}> $count/$totalCount")
                    }
                    kotlin.io.println()
                }
            }

            deferredEpisodes.map {
                async {
                    it.await().docs.forEach { episode ->
                        val title = episode.title
                        val order = episode.order!!
                        val episodePath = comicPath.resolve(title).deleteBeforeMkdirs()
                        picaComicClient.comic.getAllPagesAsync(comicId, order).forEach {
                            launch {
                                val comicPage = it.await()
                                counter.send(Init(title, comicPage.total))
                                comicPage.docs.map { it.media }.forEach {
                                    val imageFile = episodePath.resolve(it.originalName).toFile()
                                    picaComicClient.downloadFile(it).copyAndClose(imageFile.writeChannel())
                                    counter.send(Increase(title))
                                }
                            }
                        }
                    }
                }
            }.awaitAll()

            counter.close()
            rateLimiter.cancel()
        }

        picaComicClient.close()
    }

    @AfterAll
    fun dispose() {
        picaComicClient.close()
    }
}

@UseExperimental(ExperimentalCoroutinesApi::class)
private suspend fun rateLimiter(requestPerSecond: Long) =
    CoroutineScope(coroutineContext).produce {
        while (true) {
            send(Unit)
            delay(1_000 / requestPerSecond)
        }
    }
