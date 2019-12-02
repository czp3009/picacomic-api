package com.hiczp.picacomic.api.service.comic

import com.hiczp.caeruleum.annotation.*
import com.hiczp.picacomic.api.service.Page
import com.hiczp.picacomic.api.service.Response
import com.hiczp.picacomic.api.service.SortType
import com.hiczp.picacomic.api.service.awaitElements
import com.hiczp.picacomic.api.service.comic.model.*
import com.hiczp.picacomic.api.service.comment.model.CommentsResponse
import com.hiczp.picacomic.api.utils.JSON_UTF8

@DefaultContentType(JSON_UTF8)
interface ComicService {
    /**
     * 取得漫画列表
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
    suspend fun search(
        @Query page: Int = 1,
        @Query("c") category: String? = null,
        @Query("t") tags: String? = null,
        @Query("a") author: String? = null,
        @Query("f") finished: Boolean? = null,
        @Query("s") sortType: SortType = SortType.DEPRECIATION_DESC,
        @Query("ct") translate: String? = null,
        @Query("ca") creatorId: String? = null
    ): Response<Page<Comic>>

    /**
     * 高级搜索
     */
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

    @Get("{comicId}")
    suspend fun getDetail(@Path comicId: String): Response<ComicDetail>

    @Get("{comicId}/eps")
    suspend fun getEpisodes(
        @Path comicId: String,
        @Query page: Int = 1
    ): Response<Page<Episode>>

    suspend fun getAllEpisodesAsync(comicId: String) =
        Page.travelAllAsync { getEpisodes(comicId, it).data }

    suspend fun getAllEpisodes(comicId: String) =
        getAllEpisodesAsync(comicId).awaitElements()

    @Get("{comicId}/comments")
    suspend fun getComments(
        @Path comicId: String,
        @Query page: Int = 1
    ): Response<CommentsResponse>

    suspend fun getAllCommentsAsync(comicId: String) =
        Page.travelAllAsync { getComments(comicId, it).data.comments }

    suspend fun getAllComments(comicId: String) =
        getAllCommentsAsync(comicId).awaitElements()

    /**
     * 哔咔留言版 其实是一个漫画下面的评论区
     */
    suspend fun getMessageBoardComments(page: Int = 1) =
        getComments("5822a6e3ad7ede654696e482", page)

    /**
     * 发送评论
     */
    @Post("{comicId}/comments")
    suspend fun sendComment(@Path comicId: String, @Body commentRequest: CommentRequest): Response<SendCommentResponse>

    suspend fun sendComment(comicId: String, content: String) =
        sendComment(comicId, CommentRequest(content))

    /**
     * 得到漫画本体(每个图像的路径)
     *
     * @param order 第几话, 来自 Episode.order
     */
    @Get("{comicId}/order/{order}/pages")
    suspend fun getPages(
        @Path comicId: String,
        @Path("order") order: Int,
        @Query page: Int = 1
    ): Response<ComicPagesResponse>

    suspend fun getAllPagesAsync(comicId: String, order: Int) =
        Page.travelAllAsync { getPages(comicId, order, it).data.pages }

    suspend fun getAllPages(comicId: String, order: Int) =
        getAllPagesAsync(comicId, order).awaitElements()

    /**
     * 喜欢某漫画, 再次调用则不喜欢
     */
    @Post("{comicId}/like")
    suspend fun like(@Path comicId: String): Response<String>

    /**
     * 收藏某漫画, 再次调用则取消收藏
     */
    @Post("{comicId}/favourite")
    suspend fun favourite(@Path comicId: String): Response<String>

    @Get("{comicId}/recommendation")
    suspend fun getRecommendation(@Path comicId: String): Response<List<Comic>>

    /**
     * 哔咔排行榜 -> 最热门
     *
     * @param tt TimeType, 例如 H24, D7, D30
     * @param ct 不明确, 例如 VC
     */
    @Get("leaderboard")
    suspend fun getLeaderBoard(
        @Query("tt") tt: String = "H24",
        @Query("ct") ct: String = "VC"
    ): Response<List<LeaderBoardComic>>

    /**
     * 哔咔排行榜 -> 骑士榜
     */
    @Get("knight-leaderboard")
    suspend fun getKnightLeaderBoard(): Response<List<LeaderBoardKnight>>

    @Get("random")
    suspend fun random(): Response<List<Comic>>
}
