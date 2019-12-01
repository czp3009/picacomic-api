package com.hiczp.picacomic.api.service.comic.model

import com.google.gson.annotations.SerializedName
import com.hiczp.picacomic.api.service.Thumbnail
import com.hiczp.picacomic.api.service.user.model.Gender

data class LeaderBoardKnight(
    @field:SerializedName("_id")
    val leaderBoardKnightId: String,
    val gender: Gender,
    val name: String,
    val slogan: String?,
    val title: String,
    val verified: Boolean,
    val exp: Int,
    val level: Int,
    val characters: List<String>,
    val role: String,
    val avatar: Thumbnail?,
    val comicsUploaded: Int,
    val character: String?
)
