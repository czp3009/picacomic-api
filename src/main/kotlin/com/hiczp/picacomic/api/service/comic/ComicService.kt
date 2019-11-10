package com.hiczp.picacomic.api.service.comic

import com.hiczp.caeruleum.annotation.*
import com.hiczp.picacomic.api.service.Page
import com.hiczp.picacomic.api.service.Response
import com.hiczp.picacomic.api.service.SortType
import com.hiczp.picacomic.api.service.comic.model.AdvancedSearchRequest
import com.hiczp.picacomic.api.service.comic.model.Comic
import com.hiczp.picacomic.api.utils.JSON_UTF8

@DefaultContentType(JSON_UTF8)
interface ComicService {
    /**
     * 取得漫画
     * category 生效时, 其他条件不生效
     *
     * @param page 分页
     * @param category 分类, 来自于 Category.title
     * @param tags 标签, 不明确格式
     * @param author 作者
     * @param finished 是否完结
     * @param sortType 排序条件, 在某些分类不工作
     * @param translate 翻译
     * @param creatorId 上传者
     */
    @Get("/comics")
    suspend fun get(
        @Query page: Int = 1,
        @Query("c") category: String? = null,
        @Query("t") tags: String? = null,
        @Query("a") author: String? = null,
        @Query("f") finished: Boolean? = null,
        @Query("s") sortType: SortType = SortType.DEPRECIATION_DESC,
        @Query("ct") translate: String? = null,
        @Query("ca") creatorId: String? = null
    ): Response<Page<Comic>>

    @Post("advanced-search")
    suspend fun advancedSearch(
        @Query page: Int = 1,
        @Body advancedSearchRequest: AdvancedSearchRequest
    ): Response<Page<Comic>>

    suspend fun advancedSearch(
        keyword: String,
        categories: List<String> = emptyList(),
        sort: SortType = SortType.DEPRECIATION_DESC,
        page: Int = 1
    ) = advancedSearch(page, AdvancedSearchRequest(keyword, categories, sort))
}
