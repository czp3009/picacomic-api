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
    val finished: Boolean,
    val categories: List<String>,
    val thumb: Thumbnail,
    val id: String?,
    val likesCount: Int,
    /**
     * "看了这个本子的人也在看" 中不包括以下两个字段
     */
    val totalViews: Int?,
    val totalLikes: Int?
)
