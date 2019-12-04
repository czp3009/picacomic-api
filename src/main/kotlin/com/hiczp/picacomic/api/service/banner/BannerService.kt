package com.hiczp.picacomic.api.service.banner

import com.hiczp.caeruleum.annotation.DefaultContentType
import com.hiczp.caeruleum.annotation.Get
import com.hiczp.picacomic.api.service.Response
import com.hiczp.picacomic.api.service.banner.model.Banner
import com.hiczp.picacomic.api.utils.JSON_UTF8

@DefaultContentType(JSON_UTF8)
interface BannerService {
    @Get
    suspend fun get(): Response<List<Banner>>
}

