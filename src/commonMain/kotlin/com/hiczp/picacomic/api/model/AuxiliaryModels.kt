package com.hiczp.picacomic.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class RecommendationTrackingRequest(
    @SerialName("from_id")
    public val fromComicId: String,
    @SerialName("to_id")
    public val toComicId: String,
)

@Serializable
public data class RecommendationTrackingResponse(
    public val code: Int = 0,
    @SerialName("msg")
    public val message: String = "",
)

@Serializable
public data class ComicRecommendationsResponse(
    public val code: Int = 0,
    public val recommendations: List<String> = emptyList(),
    public val count: Int = 0,
)

@Serializable
public data class AnimeCatalogResponse(
    public val code: Int,
    @SerialName("msg")
    public val message: String,
    public val page: Int,
    @SerialName("pagecount")
    public val pageCount: Int,
    public val limit: String,
    public val total: Int,
    @SerialName("class")
    public val categories: List<AnimeCategory>? = null,
    @SerialName("list")
    public val anime: List<AnimeEntry> = emptyList(),
)

@Serializable
public data class AnimeCategory(
    @SerialName("type_id")
    public val typeId: Int,
    @SerialName("type_name")
    public val name: String,
    @SerialName("type_pid")
    public val parentTypeId: Int,
)

@Serializable
public data class AnimeEntry(
    @SerialName("vod_id")
    public val videoId: Int,
    @SerialName("vod_name")
    public val name: String,
    @SerialName("vod_pic")
    public val pictureUrl: String,
    @SerialName("vod_remarks")
    public val remarks: String? = null,
    @SerialName("vod_year")
    public val year: String? = null,
    @SerialName("vod_area")
    public val area: String? = null,
    @SerialName("vod_lang")
    public val language: String? = null,
    @SerialName("vod_actor")
    public val actors: String? = null,
    @SerialName("vod_director")
    public val director: String? = null,
    @SerialName("vod_content")
    public val content: String? = null,
    @SerialName("vod_play_url")
    public val playUrl: String? = null,
    @SerialName("vod_play_from")
    public val playSource: String? = null,
    @SerialName("vod_score")
    public val score: String? = null,
    @SerialName("vod_total")
    public val totalEpisodes: Int? = null,
    @SerialName("vod_serial")
    public val currentEpisode: String? = null,
    @SerialName("type_id")
    public val typeId: Int,
    @SerialName("type_name")
    public val typeName: String? = null,
)

@Serializable
public data class SmartDnsResponse(
    public val code: Int = 0,
    public val message: String = "",
    public val data: SmartDnsData? = null,
)

@Serializable
public data class SmartDnsData(
    public val domain: String = "",
    public val clientIp: String = "",
    public val clientCountry: String = "",
    public val fromCache: Boolean = false,
    public val degraded: Boolean = false,
    public val lines: Map<String, SmartDnsLine> = emptyMap(),
)

@Serializable
public data class SmartDnsLine(
    @SerialName("ips")
    public val addresses: List<String> = emptyList(),
    public val status: String = "",
    @SerialName("pollutedIps")
    public val pollutedAddresses: List<String> = emptyList(),
)

@Serializable
public data class InitConfigResponse(
    public val status: String,
    public val addresses: List<String>? = null,
    @SerialName("waka")
    public val adBaseUrl: String,
    public val adKeyword: String? = null,
)

@Serializable
public data class VersionCheckResponse(
    public val code: Int,
    public val message: String,
    public val data: VersionCheckData? = null,
)

@Serializable
public data class VersionCheckData(
    public val hasUpdate: Boolean = false,
    public val latestVersionCode: Long = 0,
    public val latestVersionName: String = "",
    public val forceMinVersionCode: Long = 0,
    public val forceUpdate: Boolean = false,
    public val downloadUrl: String = "",
    public val fallbackUrls: List<String> = emptyList(),
    public val changelog: String = "",
    public val releaseTime: String = "",
    public val fileSize: Long? = null,
    @SerialName("md5")
    public val md5Checksum: String? = null,
)

@Serializable
public data class AdZonesResponse(public val code: Int, public val data: AdZonesData)

@Serializable
public data class AdZonesData(public val zones: List<AdZone>)

@Serializable
public data class AdZone(
    public val id: String,
    public val name: String,
    public val remark: String = "",
    @SerialName("ad")
    public val advertisement: AdContent,
)

@Serializable
public data class AdContent(
    public val image: String,
    public val title: String,
    public val link: String = "",
    public val rawLink: String = "",
) {
    public val clickUrl: String
        get() = link.ifEmpty { rawLink }

    public val isVideo: Boolean
        get() = image.substringBefore('?').endsWith(".mp4", ignoreCase = true)
}

public data class AnimePage(
    public val items: List<AnimeEntry>,
    public val page: Int,
    public val pages: Int,
    public val limit: Int,
    public val total: Int,
)

public data class PlaySource(public val name: String, public val episodes: List<PlayEpisode>)

public data class PlayEpisode(public val name: String, public val url: String)
