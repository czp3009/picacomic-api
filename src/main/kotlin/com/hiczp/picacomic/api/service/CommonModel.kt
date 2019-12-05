@file:Suppress("unused")

package com.hiczp.picacomic.api.service

import com.google.gson.annotations.SerializedName
import com.hiczp.caeruleum.annotation.EncodeName
import com.hiczp.picacomic.api.service.user.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlin.coroutines.coroutineContext

internal typealias DeferredPages<T> = List<Deferred<Page<T>>>

internal suspend fun <T> DeferredPages<T>.awaitElements() = awaitAll().flatMap { it.docs }

@Suppress("MemberVisibilityCanBePrivate")
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

    companion object {
        suspend fun <T> travelAllAsync(pageProvider: suspend (page: Int) -> Page<T>): DeferredPages<T> {
            val coroutineScope = CoroutineScope(coroutineContext)
            val firstPageDeferred = coroutineScope.async { pageProvider(1) }
            val firstPage = firstPageDeferred.await()
            return if (firstPage.isLast) {
                listOf(firstPageDeferred)
            } else {
                ArrayList<Deferred<Page<T>>>(firstPage.pages).apply {
                    add(firstPageDeferred)
                    (2..firstPage.pages).forEach {
                        add(coroutineScope.async { pageProvider(it) })
                    }
                }
            }
        }

        suspend fun <T> travelAll(pageProvider: suspend (page: Int) -> Page<T>) =
            travelAllAsync(pageProvider).awaitElements()
    }
}

data class Thumbnail(
    val fileServer: String,
    val originalName: String,
    val path: String
) {
    val urlString
        get() = if (fileServer.endsWith('/')) {
            //"https://pica-web.wakamoment.tk/static/"
            "$fileServer$path"
        } else {
            //"https://storage1.picacomic.com"
            "$fileServer/static/$path"
        }
}

data class UserIdRequest(
    val userId: String
)

data class IdAndTitle(
    @field:SerializedName("_id")
    val id: String,
    val title: String
)

data class ContentAndUser(
    val content: String,
    @field:SerializedName("_user")
    val user: User
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
    WEB,
    @EncodeName("ads")
    @SerializedName("ads")
    ADVERT
}

//常用分类
@Suppress("NonAsciiCharacters", "EnumEntryName", "SpellCheckingInspection")
enum class PredefinedCategory {
    嗶咔漢化,
    全彩,
    長篇,
    同人,
    短篇,
    圓神領域,
    碧藍幻想,
    CG雜圖,
    `英語 ENG`,
    生肉,
    純愛,
    百合花園,
    耽美花園,
    偽娘哲學,
    後宮閃光,
    扶他樂園,
    姐姐系,
    妹妹系,
    SM,
    性轉換,
    足の恋,
    重口地帶,
    人妻,
    NTR,
    強暴,
    非人類,
    艦隊收藏,
    `Love Live`,
    `SAO 刀劍神域`,
    Fate,
    東方,
    WEBTOON,
    禁書目錄,
    歐美,
    Cosplay
}
