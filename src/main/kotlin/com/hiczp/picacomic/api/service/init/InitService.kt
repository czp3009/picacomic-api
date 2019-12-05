package com.hiczp.picacomic.api.service.init

import com.hiczp.caeruleum.annotation.Attribute
import com.hiczp.caeruleum.annotation.Get
import com.hiczp.caeruleum.annotation.Query
import com.hiczp.picacomic.api.NO_SIGN
import com.hiczp.picacomic.api.picaAPIBaseUrl
import com.hiczp.picacomic.api.service.Response
import com.hiczp.picacomic.api.service.init.model.ApplicationPagesResponse
import com.hiczp.picacomic.api.service.init.model.InitResponse
import com.hiczp.picacomic.api.service.init.model.WakaInitResponse

interface InitService {
    @Suppress("SpellCheckingInspection")
    @Attribute(NO_SIGN)
    @Get("http://68.183.234.72/init")
    suspend fun wakaInit(): WakaInitResponse

    @Get("$picaAPIBaseUrl/init")
    suspend fun platformInit(@Query platform: String = "android"): Response<InitResponse>

    /**
     * 获取历史版本
     */
    @Get("$picaAPIBaseUrl/applications")
    suspend fun getApplications(
        @Query platform: String = "android",
        @Query page: Int = 1
    ): Response<ApplicationPagesResponse>
}
