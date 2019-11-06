package com.hiczp.picacomic.api.test

import com.hiczp.picacomic.api.PicaComicClient
import io.ktor.client.engine.apache.Apache
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.runBlocking
import org.apache.http.HttpHost
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MainTest {
    private lateinit var picaComicClient: PicaComicClient<*>

    @UseExperimental(KtorExperimentalAPI::class)
    @BeforeAll
    fun init() {
        picaComicClient = PicaComicClient(Apache) {
            engine {
                //set proxy
                customizeClient {
                    setProxy(HttpHost("192.168.31.157", 10809))
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

    @AfterAll
    fun dispose() {
        picaComicClient.close()
    }
}
