package com.hiczp.picacomic.api.service.init.model

import com.google.gson.annotations.SerializedName
import com.hiczp.picacomic.api.service.IdAndTitle
import com.hiczp.picacomic.api.service.Thumbnail
import com.hiczp.picacomic.api.service.user.model.Notification

data class InitResponse(
    val isPunched: Boolean,
    val latestApplication: LatestApplication,
    val imageServer: String,
    val apiLevel: Int,
    val minApiLevel: Int,
    val categories: List<IdAndTitle>,
    val notification: Notification?,
    val isIdUpdated: Boolean
) {
    data class LatestApplication(
        @field:SerializedName("_id")
        val latestApplicationId: String,
        val downloadUrl: String,
        val updateContent: String,
        val version: String,
        @field:SerializedName("updated_at")
        val updatedAt: String,
        @field:SerializedName("created_at")
        val createdAt: String,
        val apk: Thumbnail
    )
}
