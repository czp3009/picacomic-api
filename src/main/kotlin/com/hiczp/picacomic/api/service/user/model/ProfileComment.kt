package com.hiczp.picacomic.api.service.user.model

import com.google.gson.annotations.SerializedName
import com.hiczp.picacomic.api.service.IdAndTitle

data class ProfileComment(
    @field:SerializedName("_id")
    val commentId: String,
    val commentsCount: Int,
    @field:SerializedName("_user")
    val user: String,
    @field:SerializedName("_comic")
    val comic: IdAndTitle?,
    @field:SerializedName("_game")
    val game: IdAndTitle?,
    val content: String,
    val hide: Boolean,
    val isLiked: Boolean,
    val likesCount: Int,
    @field:SerializedName("created_at")
    val createdAt: String
)
