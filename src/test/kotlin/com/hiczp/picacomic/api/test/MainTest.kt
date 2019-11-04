package com.hiczp.picacomic.api.test

import com.hiczp.picacomic.api.PicaComicClient
import io.ktor.client.engine.cio.CIO
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.runBlocking
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
        picaComicClient = PicaComicClient(CIO)
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
