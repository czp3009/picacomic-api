package com.hiczp.picacomic.api.service.game.model

import com.google.gson.annotations.SerializedName
import com.hiczp.picacomic.api.service.Thumbnail

data class Game(
    @field:SerializedName("_id")
    val gameId: String,
    val title: String,
    val version: String,
    val publisher: String,
    val suggest: Boolean,
    val adult: Boolean,
    val android: Boolean,
    val ios: Boolean,
    val icon: Thumbnail,
    val likesCount: Int?
)
