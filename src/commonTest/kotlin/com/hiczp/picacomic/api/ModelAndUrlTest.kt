package com.hiczp.picacomic.api

import com.hiczp.picacomic.api.model.AdvancedSearchBody
import com.hiczp.picacomic.api.model.AnimeCatalogResponse
import com.hiczp.picacomic.api.model.ChapterPagesData
import com.hiczp.picacomic.api.model.ComicDetailData
import com.hiczp.picacomic.api.model.ImageInfo
import com.hiczp.picacomic.api.model.PunchInResponse
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ModelAndUrlTest {
    @Test
    fun advancedSearchOmitsNullCategories(): Unit {
        val encoded = defaultPicaJson().encodeToString(AdvancedSearchBody(keyword = "test"))
        assertTrue("\"keyword\":\"test\"" in encoded)
        assertTrue("\"sort\":\"dd\"" in encoded)
        assertFalse("categories" in encoded)
    }

    @Test
    fun imageUrlRulesMatchClientBehavior(): Unit {
        assertEquals(
            "https://s3.picacomic.com/static/foo/bar.jpg",
            ImageInfo(path = "/foo//bar.jpg", fileServer = "https://s3.picacomic.com").url(),
        )
        assertEquals(
            "https://storage1.go2778.com/static/a.jpg",
            ImageInfo(path = "a.jpg", fileServer = "https://diwodiwo.xyz").routedUrl("go2778.com"),
        )
        assertEquals(
            "content://images/1",
            ImageInfo(path = "content://images/1", fileServer = "").url(),
        )
        assertEquals(
            "https://pica-ad-api.example/static/asset.jpg",
            ImageInfo(path = "/asset.jpg", fileServer = "https://pica-ad-api.example/").url(),
        )
    }

    @Test
    fun protocolFieldNamesDecodeIntoDescriptiveProperties(): Unit {
        val json = defaultPicaJson()
        val comic = json.decodeFromString<ComicDetailData>(
            """{
                "comic": {
                    "_id": "comic-id",
                    "_creator": {"_id": "creator-id", "exp": 7, "level": 2},
                    "thumb": {"path": "cover.jpg", "fileServer": "https://example.com"}
                }
            }""".trimIndent(),
        ).comic
        assertEquals("comic-id", comic.id)
        assertEquals("creator-id", comic.creator.id)
        assertEquals(7, comic.creator.experience)
        assertEquals(0, comic.episodeCount)

        val chapter = json.decodeFromString<ChapterPagesData>(
            """{
                "pages": {"docs": [], "total": 0, "limit": 40, "page": 1, "pages": 0},
                "ep": {"_id": "episode-id", "title": "Episode"}
            }""".trimIndent(),
        )
        assertEquals("episode-id", chapter.episode.id)
        assertTrue(chapter.pages.items.isEmpty())

        val checkIn = json.decodeFromString<PunchInResponse>("""{"res":{"status":"ok"}}""")
        assertEquals("ok", checkIn.result.status)
        assertNull(checkIn.result.punchInLastDay)
    }

    @Test
    fun requiredAndDefaultFieldsMatchTheDocumentedSerializerContract(): Unit {
        val json = defaultPicaJson()
        val response = json.decodeFromString<AnimeCatalogResponse>(
            """{
                "code": 1,
                "msg": "ok",
                "page": 1,
                "pagecount": 1,
                "limit": "20",
                "total": 1,
                "class": [{"type_id": 2, "type_name": "Anime", "type_pid": 0}],
                "list": [{"vod_id": 9, "vod_name": "Title", "vod_pic": "cover", "type_id": 2}]
            }""".trimIndent(),
        )
        assertEquals("ok", response.message)
        assertEquals("Anime", response.categories?.single()?.name)
        assertEquals("Title", response.anime.single().name)
        assertNull(response.anime.single().playUrl)

        assertFailsWith<SerializationException> {
            json.decodeFromString<ComicDetailData>(
                """{"comic":{"_id":"comic-id","_creator":{"_id":"creator-id","exp":0,"level":1}}}""",
            )
        }
    }
}
