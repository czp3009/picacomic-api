package com.hiczp.picacomic.api.service.episode

import com.hiczp.caeruleum.annotation.DefaultContentType
import com.hiczp.caeruleum.annotation.Get
import com.hiczp.caeruleum.annotation.Path
import com.hiczp.caeruleum.annotation.Query
import com.hiczp.picacomic.api.service.Response
import com.hiczp.picacomic.api.service.comic.model.ComicPagesResponse
import com.hiczp.picacomic.api.utils.JSON_UTF8

@DefaultContentType(JSON_UTF8)
interface EpisodeService {
    @Get("{epsId}/pages")
    suspend fun getPages(
        @Path("epsId") episodeId: String,
        @Query page: Int = 1
    ): Response<ComicPagesResponse>
}
