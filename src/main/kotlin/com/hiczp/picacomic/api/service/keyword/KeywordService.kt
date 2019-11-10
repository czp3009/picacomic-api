package com.hiczp.picacomic.api.service.keyword

import com.hiczp.caeruleum.annotation.DefaultContentType
import com.hiczp.caeruleum.annotation.Get
import com.hiczp.picacomic.api.service.Response
import com.hiczp.picacomic.api.utils.JSON_UTF8

@DefaultContentType(JSON_UTF8)
interface KeywordService {
    @Get
    suspend fun get(): Response<List<String>>
}
