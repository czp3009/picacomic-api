package com.hiczp.picacomic.api.service.main

import com.hiczp.caeruleum.annotation.BaseUrl
import com.hiczp.caeruleum.annotation.Get
import com.hiczp.picacomic.api.service.main.model.InitResponse

@BaseUrl("https://picaapi.picacomic.com/")
interface MainService {
    @Get("init")
    suspend fun init(): InitResponse
}
