package com.hiczp.picacomic.api.test

import com.github.salomonbrys.kotson.bool
import com.github.salomonbrys.kotson.int
import com.github.salomonbrys.kotson.obj
import com.github.salomonbrys.kotson.string
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.hiczp.picacomic.api.PicaComicClient
import com.hiczp.picacomic.api.service.Thumbnail
import com.hiczp.picacomic.api.service.auth.model.RegisterRequest
import com.hiczp.picacomic.api.service.user.model.Gender
import io.ktor.client.engine.apache.Apache
import kotlinx.coroutines.io.readRemaining
import kotlinx.coroutines.runBlocking
import kotlinx.io.core.readBytes
import org.apache.http.HttpHost
import org.junit.jupiter.api.*
import java.io.FileNotFoundException

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
            picaComicClient.main.init().println()
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

    @Test
    fun downloadFile() {
        runBlocking {
            picaComicClient.downloadFile(
                Thumbnail("https://storage1.picacomic.com", "艦隊收藏.jpg", "1ed52b9e-8ac3-47ae-bafc-c31bfab9b3d5.jpg")
            ).readRemaining().readBytes().contentToString().println()
        }
    }

    @AfterAll
    fun dispose() {
        picaComicClient.close()
    }
}
