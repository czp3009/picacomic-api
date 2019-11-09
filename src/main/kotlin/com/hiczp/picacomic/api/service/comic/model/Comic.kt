package com.hiczp.picacomic.api.service.comic.model

import com.google.gson.annotations.SerializedName
import com.hiczp.picacomic.api.service.Thumbnail

data class Comic(
    @field:SerializedName("_id")
    val comicId: String,
    val title: String,
    val author: String,
    val pagesCount: Int,
    @field:SerializedName("epsCount")
    val episodeCount: Int,
    val categories: List<String>,
    val finished: Boolean,
    val thumb: Thumbnail,
    val likesCount: Int
)
