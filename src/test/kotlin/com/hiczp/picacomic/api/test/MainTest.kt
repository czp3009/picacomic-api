package com.hiczp.picacomic.api.test

import com.github.salomonbrys.kotson.bool
import com.github.salomonbrys.kotson.int
import com.github.salomonbrys.kotson.obj
import com.github.salomonbrys.kotson.string
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.hiczp.picacomic.api.PicaComicClient
import com.hiczp.picacomic.api.service.auth.model.RegisterRequest
import com.hiczp.picacomic.api.service.user.model.Gender
import io.ktor.client.engine.apache.Apache
import kotlinx.coroutines.runBlocking
import org.apache.http.HttpHost
import org.junit.jupiter.api.*
import java.io.FileNotFoundException

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
    fun test() {
        runBlocking {
            picaComicClient.main.init()
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
            )
        }
    }

    @Disabled
    @Test
    fun signIn() {
        runBlocking {
            picaComicClient.signIn(config["email"].string, config["password"].string)
        }
    }

    @Test
    fun getProfile() {
        runBlocking {
            picaComicClient.user.getProfile()
        }
    }

    @Test
    fun getFavorite() {
        runBlocking {
            picaComicClient.user.getFavourite()
        }
    }

    @AfterAll
    fun dispose() {
        picaComicClient.close()
    }
}
