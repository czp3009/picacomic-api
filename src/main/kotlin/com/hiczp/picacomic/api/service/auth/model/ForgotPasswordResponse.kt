package com.hiczp.picacomic.api.service.auth.model

data class ForgotPasswordResponse(
    val question1: String,
    val question2: String,
    val question3: String
)
