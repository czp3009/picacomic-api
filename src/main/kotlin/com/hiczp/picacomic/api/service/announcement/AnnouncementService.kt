package com.hiczp.picacomic.api.service.announcement

import com.hiczp.caeruleum.annotation.DefaultContentType
import com.hiczp.caeruleum.annotation.Get
import com.hiczp.caeruleum.annotation.Query
import com.hiczp.picacomic.api.service.Page
import com.hiczp.picacomic.api.service.Response
import com.hiczp.picacomic.api.service.announcement.model.Announcement
import com.hiczp.picacomic.api.utils.JSON_UTF8

@DefaultContentType(JSON_UTF8)
interface AnnouncementService {
    @Get
    suspend fun get(@Query page: Int = 1): Response<Page<Announcement>>
}
