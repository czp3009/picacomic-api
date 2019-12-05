package com.hiczp.picacomic.api.service.init.model

import com.google.gson.annotations.SerializedName
import com.hiczp.picacomic.api.service.Thumbnail

data class LatestApplication(
    @field:SerializedName("_id")
    val latestApplicationId: String,
    val downloadUrl: String,
    val updateContent: String,
    val version: String,
    @field:SerializedName("updated_at")
    val updatedAt: String?,
    @field:SerializedName("created_at")
    val createdAt: String,
    val apk: Thumbnail
)
