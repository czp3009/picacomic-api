package com.hiczp.picacomic.api.service.game.model

import com.google.gson.annotations.SerializedName
import com.hiczp.picacomic.api.service.Thumbnail

data class GameDetail(
    @field:SerializedName("_id")
    val gameId: String,
    val title: String,
    val description: String,
    val version: String,
    val icon: Thumbnail,
    val publisher: String,
    val ios: Boolean,
    val iosLinks: List<String>,
    val android: String,
    val androidLinks: List<String>,
    val adult: Boolean,
    val suggest: Boolean,
    val downloadsCount: Int,
    val screenshots: List<Thumbnail>,
    val androidSize: Float,
    val iosSize: Float,
    val updateContent: String,
    val videoLink: String,
    @field:SerializedName("updated_at")
    val updatedAt: String,
    @field:SerializedName("created_at")
    val createdAt: String,
    val likesCount: Int,
    val isLiked: Boolean,
    /**
     * 2019-11-21 此字段永远为 0, 但是实际上存在评论
     */
    val commentsCount: Int
)
