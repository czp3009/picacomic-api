package com.hiczp.picacomic.api.service.comic.model

import com.google.gson.annotations.SerializedName
import com.hiczp.picacomic.api.service.ContentAndUser

data class SendCommentResponse(
    @field:SerializedName("_id")
    val commentId: String,
    val comment: ContentAndUser,
    @field:SerializedName("_comic")
    val comicId: String,
    @field:SerializedName("_parent")
    val parentId: String,
    val childsCount: Int,
    val likesCount: Int,
    @field:SerializedName("created_at")
    val createdAt: String
)
