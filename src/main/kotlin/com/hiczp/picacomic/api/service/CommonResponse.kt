package com.hiczp.picacomic.api.service

data class CommonResponse<T>(
    val code: Int,
    val error: String?,
    val message: String,
    val detail: String?,
    val data: T
)
