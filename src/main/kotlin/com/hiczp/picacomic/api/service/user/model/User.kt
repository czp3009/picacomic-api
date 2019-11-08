package com.hiczp.picacomic.api.service.user.model

import com.google.gson.annotations.SerializedName
import com.hiczp.picacomic.api.service.Thumbnail

data class UserProfile(
    val user: User
)

data class User(
    @field:SerializedName("_id")
    val userId: String,
    val name: String,
    val email: String,
    val birthday: String,
    val gender: Gender,
    //自我介绍
    val slogan: String?,
    val title: String,
    val verified: Boolean,
    val exp: Int,
    val level: Int,
    val characters: List<String>,
    val role: String?,
    @field:SerializedName("activation_date")
    val activationDate: String?,
    val avatar: Thumbnail?,
    //头像的边框
    val character: String?,
    //format: 2019-11-07T13:23:37.536Z
    @field:SerializedName("created_at")
    val createdAt: String,
    //打哔咔
    val isPunched: Boolean
)

enum class Gender {
    @SerializedName("m")
    MALE,
    @SerializedName("f")
    FEMALE,
    @SerializedName("bot")
    BOT
}
