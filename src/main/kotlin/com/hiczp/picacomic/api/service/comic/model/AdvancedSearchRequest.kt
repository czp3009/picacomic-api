package com.hiczp.picacomic.api.service.comic.model

import com.hiczp.picacomic.api.service.SortType

data class AdvancedSearchRequest(
    val keyword: String,
    val categories: List<String> = emptyList(),
    val sort: SortType = SortType.DEPRECIATION_DESC
)
