package com.hiczp.picacomic.api.internal

import kotlin.test.Test
import kotlin.test.assertEquals

class PicaSignerTest {
    @Test
    fun matchesDocumentedSignatureVector(): Unit {
        val query = "s=dd&space%20key=a%20b&plus=%2B&percent=%25&slash=%2F&" +
                "unicode=%E5%93%94%E5%92%94&empty=&page=1"
        assertEquals(
            "0ca103531aa60ee954a3cc02d60e8fbe371f7b06fbf167413d0b51463ba6a9ac",
            PicaSigner.sign(
                encodedPath = "/comics",
                encodedQuery = query,
                time = "1700000000",
                nonce = "0123456789abcdefghijklmnopqrstuv",
                method = "GET",
            ),
        )
    }
}
