package com.hiczp.picacomic.api.service.user.model

import com.google.gson.annotations.SerializedName
import com.hiczp.picacomic.api.service.RedirectType
import com.hiczp.picacomic.api.service.Thumbnail

data class Notification(
    @field:SerializedName("_id")
    val notificationId: String,
    val system: Boolean,
    @field:SerializedName("_sender")
    val sender: User?,
    val title: String,
    val cover: Thumbnail,
    val content: String,
    val redirectType: RedirectType?,
    /**
     * 对应具体类型的 ID, 例如 comicId, commentId, 由 redirectType 赋予含义
     */
    @field:SerializedName("_redirectId")
    val redirectId: String,
    /**
     * redirectType 为 app 和 web 时会有此字段
     */
    val link: String?,
    @field:SerializedName("created_at")
    val createdAt: String
)
