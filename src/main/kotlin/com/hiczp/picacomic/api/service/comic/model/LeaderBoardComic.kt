package com.hiczp.picacomic.api.service.comic.model

import com.google.gson.annotations.SerializedName
import com.hiczp.picacomic.api.service.Thumbnail

data class LeaderBoardComic(
    @field:SerializedName("_id")
    val comicId: String,
    val title: String,
    val author: String,
    val totalViews: Int,
    val totalLikes: Int,
    val likesCount: Int?,
    val pagesCount: Int,
    @field:SerializedName("epsCount")
    val episodeCount: Int,
    val finished: Boolean,
    val categories: List<String>,
    val thumb: Thumbnail,
    val viewsCount: Int,
    @Suppress("SpellCheckingInspection")
    /**
     * 过去 XX(eg."24小时") 指名次数
     */
    @field:SerializedName("leaderboardCount")
    val leaderBoardCount: Int
)
