package com.hiczp.picacomic.api.service.main

import com.hiczp.caeruleum.annotation.Get
import com.hiczp.picacomic.api.service.main.model.InitResponse

interface MainService {
    @Get("init")
    suspend fun init(): InitResponse
}
