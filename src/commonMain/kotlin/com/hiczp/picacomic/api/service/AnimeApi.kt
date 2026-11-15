package com.hiczp.picacomic.api.service

import com.hiczp.picacomic.api.model.AnimeCatalogResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query

public interface AnimeApi {
    @GET(".")
    public suspend fun getCategories(
        @Query("ac")
        action: String,
    ): AnimeCatalogResponse

    @GET(".")
    public suspend fun getCategoryPage(
        @Query("ac")
        action: String,
        @Query("t")
        categoryId: Int,
        @Query("pg")
        page: Int,
    ): AnimeCatalogResponse

    @GET(".")
    public suspend fun query(
        @Query("ac")
        action: String,
        @Query("t")
        categoryId: Int? = null,
        @Query("pg")
        page: Int? = null,
        @Query("wd")
        keyword: String? = null,
        @Query("h")
        hours: Int? = null,
        @Query("ids")
        animeIds: String? = null,
        @Query("level")
        level: Int? = null,
        @Query("area")
        area: String? = null,
        @Query("year")
        year: String? = null,
        @Query("lang")
        language: String? = null,
        @Query("letter")
        letter: String? = null,
        @Query("by")
        orderBy: String? = null,
        @Query("sort")
        sort: String? = null,
        @Query("random")
        random: String? = null,
    ): AnimeCatalogResponse

    @GET(".")
    public suspend fun getDetail(
        @Query("ac")
        action: String,
        @Query("ids")
        animeId: Int,
    ): AnimeCatalogResponse
}
