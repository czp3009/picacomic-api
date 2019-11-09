package com.hiczp.picacomic.api.service.init

import com.hiczp.caeruleum.annotation.Attribute
import com.hiczp.caeruleum.annotation.BaseUrl
import com.hiczp.caeruleum.annotation.Get
import com.hiczp.picacomic.api.NO_SIGN
import com.hiczp.picacomic.api.service.init.model.WakaInitResponse

@BaseUrl("http://68.183.234.72/")
interface InitService {
    @Attribute(NO_SIGN)
    @Get("init")
    suspend fun init(): WakaInitResponse
}
