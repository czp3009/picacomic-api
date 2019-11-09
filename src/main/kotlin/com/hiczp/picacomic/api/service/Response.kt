package com.hiczp.picacomic.api.service

import io.ktor.http.HttpStatusCode

data class Response<T>(
    val code: Int,
    val error: String?,
    val message: String,
    val detail: String?,
    val data: T
) {
    fun ok() = code == HttpStatusCode.OK.value

    val hasError get() = error != null

    val hasDetail get() = detail != null

    val hasData get() = data != null
}
