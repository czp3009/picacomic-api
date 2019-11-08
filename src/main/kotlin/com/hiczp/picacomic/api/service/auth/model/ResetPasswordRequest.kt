package com.hiczp.picacomic.api.service.auth.model

data class ResetPasswordRequest(
    val email: String,
    val questionNo: Int,
    val answer: String
)
