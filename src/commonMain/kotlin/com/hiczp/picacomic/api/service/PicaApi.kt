package com.hiczp.picacomic.api.service

import com.hiczp.picacomic.api.model.AdvancedSearchBody
import com.hiczp.picacomic.api.model.AnnouncementsResponse
import com.hiczp.picacomic.api.model.AvatarUpdateRequest
import com.hiczp.picacomic.api.model.BannersResponse
import com.hiczp.picacomic.api.model.CategoriesData
import com.hiczp.picacomic.api.model.ChapterPagesData
import com.hiczp.picacomic.api.model.CollectionsResponseData
import com.hiczp.picacomic.api.model.ComicDetailData
import com.hiczp.picacomic.api.model.CommentLikeResponse
import com.hiczp.picacomic.api.model.CommentResponseData
import com.hiczp.picacomic.api.model.ComicsListResponse
import com.hiczp.picacomic.api.model.ContentBody
import com.hiczp.picacomic.api.model.EpisodesData
import com.hiczp.picacomic.api.model.GameCommentResponseData
import com.hiczp.picacomic.api.model.GameDetailResponse
import com.hiczp.picacomic.api.model.GameLikeResponse
import com.hiczp.picacomic.api.model.GamesResponse
import com.hiczp.picacomic.api.model.KeywordsData
import com.hiczp.picacomic.api.model.LeaderboardData
import com.hiczp.picacomic.api.model.LoginData
import com.hiczp.picacomic.api.model.MyCommentsResponseData
import com.hiczp.picacomic.api.model.PicaResponse
import com.hiczp.picacomic.api.model.PunchInResponse
import com.hiczp.picacomic.api.model.RandomComicsResponse
import com.hiczp.picacomic.api.model.RegisterRequest
import com.hiczp.picacomic.api.model.SignInRequest
import com.hiczp.picacomic.api.model.SloganUpdateRequest
import com.hiczp.picacomic.api.model.UserProfileData
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.http.QueryMap
import de.jensklingenberg.ktorfit.http.Tag
import kotlinx.serialization.json.JsonElement

public object NoCacheRequest

/** Raw service for the 33 PicA endpoints. */
public interface PicaApi {
    @POST("auth/sign-in")
    public suspend fun signIn(
        @Body
        body: SignInRequest,
    ): PicaResponse<LoginData>

    @POST("auth/register")
    public suspend fun register(
        @Body
        body: RegisterRequest,
    ): PicaResponse<JsonElement>

    @GET("users/profile")
    public suspend fun getProfile(): PicaResponse<UserProfileData>

    @PUT("users/profile")
    public suspend fun updateSlogan(
        @Body
        body: SloganUpdateRequest,
    ): PicaResponse<JsonElement>

    @PUT("users/avatar")
    public suspend fun updateAvatar(
        @Body
        body: AvatarUpdateRequest,
    ): PicaResponse<JsonElement>

    @GET("users/favourite")
    public suspend fun getFavourites(
        @Query("page")
        page: Int,
        @Query("s")
        sort: String,
    ): PicaResponse<ComicsListResponse>

    @GET("users/my-comments")
    public suspend fun getMyComments(
        @Query("page")
        page: Int,
    ): PicaResponse<MyCommentsResponseData>

    @POST("users/punch-in")
    public suspend fun punchIn(
        @Body
        emptyBody: Map<String, String>,
    ): PicaResponse<PunchInResponse>

    @GET("categories")
    public suspend fun getCategories(): PicaResponse<CategoriesData>

    @GET("keywords")
    public suspend fun getKeywords(): PicaResponse<KeywordsData>

    @GET("banners")
    public suspend fun getBanners(): PicaResponse<BannersResponse>

    @GET("announcements")
    public suspend fun getAnnouncements(
        @Query("page")
        page: Int,
        @Query("platform")
        platform: String,
    ): PicaResponse<AnnouncementsResponse>

    @GET("collections")
    public suspend fun getCollections(): PicaResponse<CollectionsResponseData>

    @GET("comics")
    public suspend fun getComics(
        @QueryMap
        queryParameters: Map<String, String>,
    ): PicaResponse<ComicsListResponse>

    @POST("comics/advanced-search")
    public suspend fun advancedSearch(
        @Query("page")
        page: Int,
        @Body
        body: AdvancedSearchBody,
    ): PicaResponse<ComicsListResponse>

    @GET("comics/random")
    public suspend fun getRandomComics(
        @Tag("noCache")
        noCache: NoCacheRequest = NoCacheRequest,
    ): PicaResponse<RandomComicsResponse>

    @GET("comics/leaderboard")
    public suspend fun getLeaderboard(
        @Query("tt")
        timeframe: String,
        @Query("ct")
        category: String,
    ): PicaResponse<LeaderboardData>

    @GET("comics/{comicId}")
    public suspend fun getComicDetail(
        @Path("comicId")
        comicId: String,
    ): PicaResponse<ComicDetailData>

    @POST("comics/{comicId}/favourite")
    public suspend fun toggleFavourite(
        @Path("comicId")
        comicId: String,
        @Body
        emptyBody: Map<String, String>,
    ): PicaResponse<JsonElement>

    @POST("comics/{comicId}/like")
    public suspend fun toggleComicLike(
        @Path("comicId")
        comicId: String,
        @Body
        emptyBody: Map<String, String>,
    ): PicaResponse<JsonElement>

    @GET("comics/{comicId}/eps")
    public suspend fun getEpisodes(
        @Path("comicId")
        comicId: String,
        @Query("page")
        page: Int,
    ): PicaResponse<EpisodesData>

    @GET("comics/{comicId}/order/{order}/pages")
    public suspend fun getChapterPages(
        @Path("comicId")
        comicId: String,
        @Path("order")
        chapterOrder: String,
        @Query("page")
        page: Int,
    ): PicaResponse<ChapterPagesData>

    @GET("comics/{comicId}/comments")
    public suspend fun getComicComments(
        @Path("comicId")
        comicId: String,
        @Query("page")
        page: Int,
    ): PicaResponse<CommentResponseData>

    @POST("comics/{comicId}/comments")
    public suspend fun postComicComment(
        @Path("comicId")
        comicId: String,
        @Body
        body: ContentBody,
    ): PicaResponse<JsonElement>

    @GET("comments/{commentId}/childrens")
    public suspend fun getReplies(
        @Path("commentId")
        commentId: String,
        @Query("page")
        page: Int,
    ): PicaResponse<CommentResponseData>

    @POST("comments/{commentId}")
    public suspend fun replyComment(
        @Path("commentId")
        commentId: String,
        @Body
        body: ContentBody,
    ): PicaResponse<JsonElement>

    @POST("comments/{commentId}/like")
    public suspend fun toggleCommentLike(
        @Path("commentId")
        commentId: String,
        @Body
        emptyBody: Map<String, String>,
    ): PicaResponse<CommentLikeResponse>

    @POST("comments/{commentId}/report")
    public suspend fun reportComment(
        @Path("commentId")
        commentId: String,
        @Body
        emptyBody: Map<String, String>,
    ): PicaResponse<JsonElement>

    @GET("games")
    public suspend fun getGames(
        @Query("page")
        page: Int,
    ): PicaResponse<GamesResponse>

    @GET("games/{gameId}")
    public suspend fun getGameDetail(
        @Path("gameId")
        gameId: String,
    ): PicaResponse<GameDetailResponse>

    @GET("games/{gameId}/comments")
    public suspend fun getGameComments(
        @Path("gameId")
        gameId: String,
        @Query("page")
        page: Int,
    ): PicaResponse<GameCommentResponseData>

    @POST("games/{gameId}/comments")
    public suspend fun postGameComment(
        @Path("gameId")
        gameId: String,
        @Body
        body: ContentBody,
    ): PicaResponse<JsonElement>

    @POST("games/{gameId}/like")
    public suspend fun toggleGameLike(
        @Path("gameId")
        gameId: String,
        @Body
        emptyBody: Map<String, String>,
    ): PicaResponse<GameLikeResponse>
}
