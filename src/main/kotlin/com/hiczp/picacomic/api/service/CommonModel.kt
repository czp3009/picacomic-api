package com.hiczp.picacomic.api.service

import com.google.gson.annotations.SerializedName
import com.hiczp.caeruleum.annotation.EncodeName

data class Page<T>(
    val limit: Int,
    val page: Int,
    val pages: Int,
    val total: Int,
    val docs: List<T>
) {
    val isFirst get() = page <= 1

    val isLast get() = page >= pages

    val isEmpty get() = total == 0

    val previousPage get() = if (isFirst) 1 else page - 1

    val nextPage get() = if (isLast) pages else page + 1
}

data class Thumbnail(
    val fileServer: String,
    val originalName: String,
    val path: String
) {
    val urlString get() = "$fileServer/static/$path"
}

data class IdAndTitle(
    @field:SerializedName("_id")
    val id: String,
    val title: String
)

enum class SortType {
    //新到旧
    @EncodeName("dd")
    @SerializedName("dd")
    DEPRECIATION_DESC,
    //旧到新
    @EncodeName("da")
    @SerializedName("da")
    DEPRECIATION_ASC,
    //最多爱心
    @EncodeName("ld")
    @SerializedName("ld")
    LIKE_DESC,
    //最多绅士指名
    @EncodeName("vd")
    @SerializedName("vd")
    VIEW_DESC
}

enum class RedirectType {
    @EncodeName("comic")
    @SerializedName("comic")
    COMIC,
    @EncodeName("game")
    @SerializedName("game")
    GAME,
    @EncodeName("comment")
    @SerializedName("comment")
    COMMENT,
    @EncodeName("app")
    @SerializedName("app")
    APP,
    @EncodeName("web")
    @SerializedName("web")
    WEB
}
