package com.hiczp.picacomic.api.service.picaapp

import com.hiczp.caeruleum.annotation.DefaultContentType
import com.hiczp.caeruleum.annotation.Get
import com.hiczp.picacomic.api.service.Response
import com.hiczp.picacomic.api.service.picaapp.model.PicaApp
import com.hiczp.picacomic.api.utils.JSON_UTF8

@DefaultContentType(JSON_UTF8)
interface PicaAppService {
    @Get
    suspend fun get(): Response<List<PicaApp>>
}
