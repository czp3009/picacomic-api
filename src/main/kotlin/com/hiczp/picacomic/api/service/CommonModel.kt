package com.hiczp.picacomic.api.service

import com.google.gson.annotations.SerializedName

data class Page<T>(
    val limit: Int,
    val page: Int,
    val pages: Int,
    val total: Int,
    val docs: T
)

enum class SortType {
    //新到旧
    @SerializedName("dd")
    DEPRECIATION_DESC,
    //旧到新
    @SerializedName("da")
    DEPRECIATION_ASC,
    //最多爱心
    @SerializedName("ld")
    LIKE_DESC,
    //最多绅士指名
    @SerializedName("vd")
    VIEW_DESC
}

data class Thumbnail(
    val fileServer: String,
    val originalName: String,
    val path: String
)
