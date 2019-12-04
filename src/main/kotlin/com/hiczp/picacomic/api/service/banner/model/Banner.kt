package com.hiczp.picacomic.api.service.banner.model

import com.google.gson.annotations.SerializedName
import com.hiczp.picacomic.api.service.Thumbnail

data class Banner(
    @field:SerializedName("_id")
    val bannerId: String,
    val title: String,
    val shortDescription: String,
    @field:SerializedName("_comic")
    val comicId: String?,
    @field:SerializedName("_game")
    val gameId: String?,
    /**
     * 类型决定了是否有 comicId, gameId 或者 link
     */
    val type: String,
    val link: String?,
    val thumb: Thumbnail
)
