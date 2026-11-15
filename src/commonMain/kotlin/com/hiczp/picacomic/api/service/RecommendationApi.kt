package com.hiczp.picacomic.api.service

import com.hiczp.picacomic.api.model.ComicRecommendationsResponse
import com.hiczp.picacomic.api.model.RecommendationTrackingRequest
import com.hiczp.picacomic.api.model.RecommendationTrackingResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

public interface RecommendationApi {
    @POST("picacomic/rec/track")
    public suspend fun track(
        @Body
        request: RecommendationTrackingRequest,
    ): RecommendationTrackingResponse

    @GET("picacomic/rec/{comicId}")
    public suspend fun recommend(
        @Path("comicId")
        comicId: String,
        @Query("limit")
        limit: Int,
    ): ComicRecommendationsResponse
}
