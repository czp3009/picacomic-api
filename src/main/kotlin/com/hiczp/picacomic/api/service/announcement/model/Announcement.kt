package com.hiczp.picacomic.api.service.announcement.model

import com.google.gson.annotations.SerializedName
import com.hiczp.picacomic.api.service.Thumbnail

data class Announcement(
    @field:SerializedName("_id")
    val announcementId: String,
    val title: String,
    val content: String,
    @field:SerializedName("created_at")
    val createdAt: String,
    val thumb: Thumbnail
)
