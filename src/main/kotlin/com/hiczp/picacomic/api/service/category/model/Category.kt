package com.hiczp.picacomic.api.service.category.model

import com.google.gson.annotations.SerializedName
import com.hiczp.picacomic.api.service.Thumbnail

data class Category(
    @field:SerializedName("_id")
    val categoryId: String?,
    val title: String,
    val thumb: Thumbnail,
    val description: String?,
    val isWeb: Boolean,
    val link: String?,
    //active 在当前版本客户端中不存在
    val active: Boolean?
)
