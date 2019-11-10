package com.hiczp.picacomic.api.service.comic.model

import com.google.gson.annotations.SerializedName

data class Episode(
    @field:SerializedName("_id")
    val episodeId: String,
    val title: String,
    val order: Int?,
    @field:SerializedName("updated_at")
    val updatedAt: String?,
    val id: String?,
    val selected: Boolean?,
    val status: Int?
)
