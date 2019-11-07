package com.hiczp.picacomic.api.service.auth.model

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    //昵称
    val name: String,
    //账号
    val email: String,
    //密码
    val password: String,
    //安全问题
    val question1: String,
    val answer1: String,
    val question2: String,
    val answer2: String,
    val question3: String,
    val answer3: String,
    //出生日期
    //format: 2019-11-14
    val birthday: String,
    //性别
    val gender: Gender
) {
    enum class Gender {
        @SerializedName("m")
        MALE,
        @SerializedName("f")
        FEMALE,
        @SerializedName("bot")
        BOT
    }
}
