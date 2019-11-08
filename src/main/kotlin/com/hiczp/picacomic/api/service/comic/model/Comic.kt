package com.hiczp.picacomic.api.service.comic.model

import com.google.gson.annotations.SerializedName
import com.hiczp.picacomic.api.service.Page

data class ComicList(
    val comics: Page<Comic>
)

data class Comic(
    @SerializedName("_id")
    val comicId: String
)
