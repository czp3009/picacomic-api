package com.hiczp.picacomic.api.service.comment.model

import com.google.gson.annotations.SerializedName
import com.hiczp.picacomic.api.service.user.model.User

/**
 * A comment can be comic's or game's
 */
data class Comment(
    @field:SerializedName("_id")
    val commentId: String,
    val content: String,
    @field:SerializedName("_user")
    val user: User,
    @field:SerializedName("_comic")
    val comicId: String?,
    @field:SerializedName("_game")
    val gameId: String?,
    val isTop: Boolean,
    val hide: Boolean,
    @field:SerializedName("created_at")
    val createdAt: String,
    /**
     * 置顶评论有此字段
     */
    val ip: String?,
    val id: String?,
    val likesCount: Int,
    val commentsCount: Int,
    val isLiked: Boolean
)
