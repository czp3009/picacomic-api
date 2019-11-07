package com.hiczp.picacomic.api.test

import com.github.salomonbrys.kotson.bool
import com.github.salomonbrys.kotson.get
import com.github.salomonbrys.kotson.int
import com.github.salomonbrys.kotson.string
import com.google.gson.JsonParser
import com.hiczp.picacomic.api.PicaComicClient
import com.hiczp.picacomic.api.service.auth.model.RegisterRequest
import com.hiczp.picacomic.api.service.auth.model.SignInRequest
import io.ktor.client.engine.apache.Apache
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.runBlocking
import org.apache.http.HttpHost
import org.junit.jupiter.api.*
import java.io.FileNotFoundException

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MainTest {
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var picaComicClient: PicaComicClient<*>

    @UseExperimental(KtorExperimentalAPI::class)
    @BeforeAll
    fun init() {
        val json = MainTest::class.java.getResourceAsStream("/config.json")?.let {
            JsonParser.parseReader(it.reader())
        } ?: throw FileNotFoundException("Rename '_config.json' to 'config.json' before start test")
        email = json["email"].string
        password = json["password"].string

        picaComicClient = PicaComicClient(Apache) {
            engine {
                //set proxy
                if (json["useProxy"].bool) {
                    customizeClient {
                        setProxy(HttpHost(json["httpProxyHost"].string, json["httpProxyPort"].int))
                    }
                }
            }
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
                    "testAccount",
                    "onlyForTest",
                    "onlyForTest",
                    "test1",
                    "test1",
                    "test2",
                    "test2",
                    "test3",
                    "test3",
                    "2019-11-14",
                    RegisterRequest.Gender.BOT
                )
            )
        }
    }

    @Test
    fun signIn() {
        runBlocking {
            picaComicClient.auth.signIn(
                SignInRequest(email, password)
            )
        }
    }

    @AfterAll
    fun dispose() {
        picaComicClient.close()
    }
}
