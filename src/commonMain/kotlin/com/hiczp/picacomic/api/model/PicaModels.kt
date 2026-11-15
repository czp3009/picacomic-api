package com.hiczp.picacomic.api.model

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
public data class PicaResponse<T>(
    public val code: Int,
    public val message: String,
    public val error: String? = null,
    public val data: T? = null,
)

@Serializable
public data class SignInRequest(public val email: String, public val password: String)

@Serializable
public data class RegisterRequest(
    public val name: String,
    public val email: String,
    public val password: String,
    public val question1: String,
    public val answer1: String,
    public val question2: String,
    public val answer2: String,
    public val question3: String,
    public val answer3: String,
    public val birthday: String,
    public val gender: String,
)

@Serializable
public data class SloganUpdateRequest(public val slogan: String)

@Serializable
public data class AvatarUpdateRequest(public val avatar: String)

@Serializable
public data class ContentBody(public val content: String)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
public data class AdvancedSearchBody(
    public val keyword: String,
    public val sort: String = "dd",
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    public val categories: List<String>? = null,
)

@Serializable
public data class ImageInfo(
    public val originalName: String? = null,
    public val path: String,
    public val fileServer: String,
)

@Serializable
public data class ComicsListResponse(public val comics: ComicsPageData)

@Serializable
public data class ComicsPageData(
    @SerialName("docs")
    public val items: List<ComicItemData>,
    public val total: Int,
    public val page: Int,
    public val pages: Int,
    public val limit: Int = 40,
)

@Serializable
public data class RandomComicsResponse(public val comics: List<ComicItemData>)

@Serializable
public data class ComicItemData(
    @SerialName("_id")
    public val id: String,
    public val title: String,
    public val author: String? = null,
    public val totalViews: Int = 0,
    public val totalLikes: Int = 0,
    @SerialName("pagesCount")
    public val pageCount: Int = 0,
    @SerialName("epsCount")
    public val episodeCount: Int = 0,
    public val finished: Boolean = false,
    public val categories: List<String> = emptyList(),
    public val tags: List<String> = emptyList(),
    @SerialName("thumb")
    public val thumbnail: ImageInfo,
    public val likesCount: Int = 0,
)

@Serializable
public data class ComicDetailData(public val comic: ComicDetail)

@Serializable
public data class ComicDetail(
    @SerialName("_id")
    public val id: String,
    @SerialName("_creator")
    public val creator: Creator,
    public val title: String = "",
    public val description: String = "",
    @SerialName("thumb")
    public val thumbnail: ImageInfo,
    public val author: String = "",
    public val chineseTeam: String? = null,
    public val categories: List<String> = emptyList(),
    public val tags: List<String> = emptyList(),
    @SerialName("pagesCount")
    public val pageCount: Int = 0,
    @SerialName("epsCount")
    public val episodeCount: Int = 0,
    public val finished: Boolean = false,
    @SerialName("updated_at")
    public val updatedAt: String = "",
    @SerialName("created_at")
    public val createdAt: String = "",
    public val allowDownload: Boolean = true,
    public val allowComment: Boolean = true,
    public val totalLikes: Int? = 0,
    public val totalViews: Int? = 0,
    public val totalComments: Int? = 0,
    public val viewsCount: Int? = 0,
    public val likesCount: Int? = 0,
    public val commentsCount: Int? = 0,
    public val isFavourite: Boolean = false,
    public val isLiked: Boolean = false,
)

@Serializable
public data class Creator(
    @SerialName("_id")
    public val id: String,
    public val gender: String = "",
    public val name: String = "",
    public val title: String? = null,
    public val verified: Boolean = false,
    @SerialName("exp")
    public val experience: Int,
    public val level: Int,
    public val characters: List<String> = emptyList(),
    public val role: String? = null,
    public val slogan: String? = null,
    public val avatar: ImageInfo? = null,
)

@Serializable
public data class CategoriesData(public val categories: List<CategoryItem>)

@Serializable
public data class CategoryItem(
    @SerialName("_id")
    public val id: String? = null,
    public val title: String,
    public val description: String? = null,
    @SerialName("thumb")
    public val thumbnail: ImageInfo,
    public val isWeb: Boolean = false,
    public val active: Boolean = true,
    public val link: String? = null,
)

@Serializable
public data class KeywordsData(public val keywords: List<String>)

@Serializable
public data class BannersResponse(public val banners: List<BannerData>)

@Serializable
public data class BannerData(
    @SerialName("_id")
    public val id: String,
    public val title: String,
    public val shortDescription: String = "",
    public val link: String = "",
    public val type: String = "web",
    @SerialName("thumb")
    public val thumbnail: ImageInfo,
)

@Serializable
public data class AnnouncementsResponse(public val announcements: AnnouncementsData)

@Serializable
public data class AnnouncementsData(
    @SerialName("docs")
    public val items: List<Announcement>,
    public val total: Int,
    public val limit: Int,
    public val page: String,
    public val pages: Int,
)

@Serializable
public data class Announcement(
    @SerialName("_id")
    public val id: String,
    public val title: String,
    public val content: String,
    @SerialName("thumb")
    public val thumbnail: ImageInfo,
)

@Serializable
public data class CollectionsResponseData(public val collections: List<CollectionGroup>)

@Serializable
public data class CollectionGroup(public val title: String, public val comics: List<CollectionComicItem>)

@Serializable
public data class CollectionComicItem(
    @SerialName("_id")
    public val id: String,
    public val title: String,
    public val author: String,
    @SerialName("thumb")
    public val thumbnail: ImageInfo,
    public val categories: List<String> = emptyList(),
    public val tags: List<String> = emptyList(),
    public val finished: Boolean = false,
    @SerialName("epsCount")
    public val episodeCount: Int = 0,
    @SerialName("pagesCount")
    public val pageCount: Int = 0,
    public val totalViews: Int = 0,
    public val totalLikes: Int = 0,
)

@Serializable
public data class LeaderboardData(public val comics: List<LeaderboardComic>)

@Serializable
public data class LeaderboardComic(
    @SerialName("_id")
    public val id: String,
    public val title: String,
    public val author: String,
    public val totalViews: Int = 0,
    public val totalLikes: Int = 0,
    @SerialName("pagesCount")
    public val pageCount: Int = 0,
    @SerialName("epsCount")
    public val episodeCount: Int = 0,
    public val finished: Boolean = false,
    public val categories: List<String> = emptyList(),
    public val tags: List<String> = emptyList(),
    @SerialName("thumb")
    public val thumbnail: ImageInfo,
    public val likesCount: Int = 0,
)

@Serializable
public data class EpisodesData(
    @SerialName("eps")
    public val episodes: EpisodesInfo,
)

@Serializable
public data class EpisodesInfo(
    @SerialName("docs")
    public val items: List<Episode>,
    public val total: Int,
    public val limit: Int,
    public val page: Int,
    public val pages: Int,
)

@Serializable
public data class Episode(
    @SerialName("_id")
    public val id: String,
    public val title: String,
    @SerialName("updated_at")
    public val updatedAt: String,
    public val order: Int,
)

@Serializable
public data class ChapterPagesData(
    public val pages: PagesInfo,
    @SerialName("ep")
    public val episode: EpisodeInfo,
)

@Serializable
public data class PagesInfo(
    @SerialName("docs")
    public val items: List<PageItem>,
    public val total: Int,
    public val limit: Int,
    public val page: Int,
    public val pages: Int,
)

@Serializable
public data class PageItem(
    @SerialName("_id")
    public val id: String,
    public val media: ImageInfo,
)

@Serializable
public data class EpisodeInfo(
    @SerialName("_id")
    public val id: String,
    public val title: String,
    public val order: Int? = null,
    @SerialName("updated_at")
    public val updatedAt: String? = null,
)

@Serializable
public data class CommentResponseData(
    public val comments: CommentsPageData,
    public val topComments: List<Comment> = emptyList(),
)

@Serializable
public data class CommentsPageData(
    @SerialName("docs")
    public val items: List<Comment>,
    public val total: Int,
    public val limit: Int,
    public val page: Int,
    public val pages: Int,
)

@Serializable
public data class Comment(
    @SerialName("_id")
    public val id: String,
    public val content: String,
    @SerialName("created_at")
    public val createdAt: String,
    @SerialName("_user")
    public val user: CommentUser,
    @SerialName("_comic")
    public val comicId: String = "",
    public val totalComments: Int = 0,
    public val isTop: Boolean = false,
    public val hide: Boolean = false,
    public val isLiked: Boolean = false,
    public val likesCount: Int = 0,
    public val commentsCount: Int = 0,
)

@Serializable
public data class CommentUser(
    @SerialName("_id")
    public val id: String,
    public val gender: String,
    public val name: String,
    public val title: String? = null,
    public val verified: Boolean = false,
    @SerialName("exp")
    public val experience: Int = 0,
    public val level: Int = 0,
    public val characters: List<String> = emptyList(),
    public val role: String? = null,
    public val avatar: ImageInfo? = null,
    public val slogan: String? = null,
    public val character: String? = null,
)

@Serializable
public data class CommentLikeResponse(public val action: String)

@Serializable
public data class MyCommentsResponseData(public val comments: MyCommentsPageData)

@Serializable
public data class MyCommentsPageData(
    @SerialName("docs")
    public val items: List<MyCommentItem>,
    public val total: Int,
    public val limit: Int,
    public val page: String,
    public val pages: Int,
)

@Serializable
public data class MyCommentItem(
    @SerialName("_id")
    public val id: String,
    public val content: String,
    @SerialName("created_at")
    public val createdAt: String,
    @SerialName("_comic")
    public val comic: MyCommentComicRef? = null,
    public val totalComments: Int = 0,
    public val likesCount: Int = 0,
    public val commentsCount: Int = 0,
    public val hide: Boolean = false,
    public val isLiked: Boolean = false,
)

@Serializable
public data class MyCommentComicRef(
    @SerialName("_id")
    public val id: String,
    public val title: String,
)

@Serializable
public data class LoginData(public val token: String)

@Serializable
public data class UserProfileData(public val user: UserProfile)

@Serializable
public data class UserProfile(
    @SerialName("_id")
    public val id: String,
    public val email: String,
    public val name: String,
    public val title: String? = null,
    public val slogan: String? = null,
    public val avatar: ImageInfo? = null,
    @SerialName("exp")
    public val experience: Int = 0,
    public val level: Int = 1,
    public val gender: String = "bot",
    public val verified: Boolean = false,
    public val isPunched: Boolean = false,
    public val characters: List<String> = emptyList(),
    public val birthday: String? = null,
    @SerialName("created_at")
    public val createdAt: String? = null,
)

@Serializable
public data class PunchInResponse(
    @SerialName("res")
    public val result: PunchInData,
)

@Serializable
public data class PunchInData(public val status: String, public val punchInLastDay: String? = null)

@Serializable
public data class GamesResponse(public val games: GamesPageData)

@Serializable
public data class GamesPageData(
    @SerialName("docs")
    public val items: List<GameItem>,
    public val total: Int,
    public val limit: Int,
    public val page: Int,
    public val pages: Int,
)

@Serializable
public data class GameItem(
    @SerialName("_id")
    public val id: String,
    public val title: String,
    public val version: String,
    public val publisher: String,
    public val icon: ImageInfo,
    public val ios: Boolean,
    public val android: Boolean,
    public val picaLink: Boolean,
    public val adult: Boolean,
    public val suggest: Boolean,
    public val iosLinks: List<String> = emptyList(),
    public val androidLinks: List<String> = emptyList(),
)

@Serializable
public data class GameDetailResponse(public val game: GameDetail)

@Serializable
public data class GameDetail(
    @SerialName("_id")
    public val id: String,
    public val title: String,
    public val version: String,
    public val publisher: String,
    public val description: String,
    public val icon: ImageInfo,
    public val ios: Boolean,
    public val android: Boolean,
    public val picaLink: Boolean,
    public val adult: Boolean,
    public val suggest: Boolean,
    public val iosLinks: List<String> = emptyList(),
    public val androidLinks: List<String> = emptyList(),
    public val downloadsCount: Int = 0,
    public val likesCount: Int = 0,
    public val commentsCount: Int = 0,
    public val isLiked: Boolean = false,
    public val screenshots: List<ImageInfo> = emptyList(),
    public val androidSize: Double? = null,
    public val iosSize: Double? = null,
    public val updateContent: String? = null,
    public val videoLink: String? = null,
    @SerialName("created_at")
    public val createdAt: String,
    @SerialName("updated_at")
    public val updatedAt: String,
)

@Serializable
public data class GameLikeResponse(public val action: String)

@Serializable
public data class GameCommentResponseData(
    public val comments: GameCommentsPageData,
    public val topComments: List<GameComment> = emptyList(),
)

@Serializable
public data class GameCommentsPageData(
    @SerialName("docs")
    public val items: List<GameComment>,
    public val total: Int,
    public val limit: Int,
    public val page: String,
    public val pages: Int,
)

@Serializable
public data class GameComment(
    @SerialName("_id")
    public val id: String,
    public val content: String,
    @SerialName("_game")
    public val gameId: String,
    @SerialName("created_at")
    public val createdAt: String,
    @SerialName("_user")
    public val user: CommentUser,
    public val totalComments: Int = 0,
    public val likesCount: Int = 0,
    public val commentsCount: Int = 0,
    public val isTop: Boolean = false,
    public val hide: Boolean = false,
    public val isLiked: Boolean = false,
)

public typealias UntypedPicaResponse = PicaResponse<JsonElement>
