package com.hiczp.picacomic.api.service.comic.model

import com.google.gson.annotations.SerializedName
import com.hiczp.picacomic.api.service.Page
import com.hiczp.picacomic.api.service.Thumbnail

data class ComicPagesResponse(
    val pages: Page<ComicPage>,
    @field:SerializedName("ep")
    val episode: Episode
)

data class ComicPage(
    @field:SerializedName("_id")
    val comicPageId: String,
    val media: Thumbnail,
    val id: String?
)
