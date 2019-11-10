package com.hiczp.picacomic.api.service.comic.model

import com.google.gson.annotations.SerializedName
import com.hiczp.picacomic.api.service.Thumbnail
import com.hiczp.picacomic.api.service.user.model.User

data class ComicDetail(
    @field:SerializedName("_id")
    val comicId: String,
    @field:SerializedName("_creator")
    val creator: User,
    val title: String,
    val description: String,
    val thumb: Thumbnail,
    val author: String,
    val chineseTeam: String,
    val categories: List<String>,
    val tags: List<String>,
    val pagesCount: Int,
    @field:SerializedName("epsCount")
    val episodeCount: Int,
    val finished: Boolean,
    @field:SerializedName("updated_at")
    val updatedAt: String,
    @field:SerializedName("created_at")
    val createdAt: String,
    val allowDownload: Boolean,
    val allowComment: Boolean,
    val totalLikes: Int?,
    val totalViews: Int?,
    val viewsCount: Int,
    val likesCount: Int,
    val isFavourite: Boolean,
    val isLiked: Boolean,
    val commentsCount: Int
)
